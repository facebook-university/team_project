package com.example.dine_and_donate;

import android.content.Intent;
import android.graphics.Movie;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.dine_and_donate.Models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.parceler.Parcels;

public class LoginActivity extends AppCompatActivity {

    private EditText email;
    private EditText password;
    private Button login;
    private Button signup;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);

        mAuth = FirebaseAuth.getInstance();
        final FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null) { // if someone is already signed in, skip sign in process
            startNewActivity();
        }
        email = findViewById(R.id.et_email);
        password = findViewById(R.id.et_password);
        login = findViewById(R.id.login_btn);
        signup = findViewById(R.id.signup_btn);

        //action for login button
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn(email.getText().toString(), password.getText().toString());
            }
        });

        //action for signup button
        signup.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //go to Sign Up page
                Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }


    private void signIn(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("signIn", "signInWithEmail:success");
                            startNewActivity();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("signIn", "signInWithEmail:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                        }

                        // ...
                    }
                });
    }

    private void startNewActivity() {
        final FirebaseUser user = mAuth.getCurrentUser();
        final User userInfo = new User();
        FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
        DatabaseReference mRef = mDatabase.getReference();

        DatabaseReference ref = mRef.child("users"); // reference to users in database

        ref.addListenerForSingleValueEvent(new ValueEventListener() { // called in onCreate and when database has been changed
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) { // called when database read is successful
                DataSnapshot userData = dataSnapshot.child(user.getUid());
                userInfo.setName(userData.child("name").getValue().toString());
                userInfo.setOrg(Boolean.parseBoolean(userData.child("isOrg").getValue().toString()));
                userInfo.setEmail(userData.child("email").getValue().toString());
                if(userInfo.isOrg()) {
                    userInfo.setPhoneNumber(userData.child("phoneNumber").getValue().toString());
                }
                Intent intent = new Intent(LoginActivity.this, ProfileActivity.class);
                intent.putExtra(User.class.getSimpleName(), Parcels.wrap(userInfo));
                startActivity(intent);
                finish();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("tag", "onCancelled", databaseError.toException());
            }
        });
    }

}
