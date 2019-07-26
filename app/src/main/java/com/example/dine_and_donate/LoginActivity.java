package com.example.dine_and_donate;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.dine_and_donate.Activities.HomeActivity;
import com.example.dine_and_donate.Models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    private EditText mEmail;
    private EditText mPassword;
    private Button mLogin;
    private Button mSignup;
    private FirebaseAuth mAuth;
    private User mCurrentUserModel;
    private BottomNavigationView bottomNavigationView;
    private FirebaseDatabase mDatabase;
    private FirebaseUser mFbUser;
    private DatabaseReference mRef;
    private DatabaseReference mRefChild;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();
        mFbUser = FirebaseAuth.getInstance().getCurrentUser();
        mRef = mDatabase.getReference();
        mRefChild = mRef.child("users");

        //if someone is already signed in, skip sign in process
        if(mFbUser != null) {
            createUserModel();
        }

        mEmail = findViewById(R.id.et_email);
        mPassword = findViewById(R.id.et_password);
        mLogin = findViewById(R.id.login_btn);
        mSignup = findViewById(R.id.signup_btn);

        //action for login button
        mLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn(mEmail.getText().toString(), mPassword.getText().toString());
            }
        });

        //action for signup button
        mSignup.setOnClickListener(new View.OnClickListener() {

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
                    // Sign in success, update UI with the signed-in user's information
                    if (task.isSuccessful()) {
                        Log.d("signIn", "signInWithEmail:success");
                        mFbUser = FirebaseAuth.getInstance().getCurrentUser();
                        createUserModel();
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w("signIn", "signInWithEmail:failure", task.getException());
                        Toast.makeText(LoginActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                    }
                }
            });
    }

    private void createUserModel() {
        mRefChild.child(mFbUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() { // called in onCreate and when database has been changed
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) { // called when database read is successful
                mCurrentUserModel = new User();
                mCurrentUserModel.setName(dataSnapshot.child("name").getValue().toString());
                mCurrentUserModel.setOrg(Boolean.parseBoolean(dataSnapshot.child("isOrg").getValue().toString()));
                mCurrentUserModel.setEmail(dataSnapshot.child("email").getValue().toString());
                if(mCurrentUserModel.getIsOrg()) {
                    mCurrentUserModel.setPhoneNumber(dataSnapshot.child("phoneNumber").getValue().toString());
                }

                // initiate saved events dictionary
                Map<String, String> events = mCurrentUserModel.getSavedEventsIDs();
                if (events != null) {
                    for (DataSnapshot eventChild : dataSnapshot.child("Events").getChildren()) {
                        events.put(eventChild.getKey(), eventChild.getValue().toString());
                    }
                } else {
                    events = new HashMap<String, String>();
                }
                mCurrentUserModel.addSavedEventID(events);
                goToProfile();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("tag", "onCancelled", databaseError.toException());
            }
        });
    }

    private void goToProfile() {
        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
        intent.putExtra(User.class.getSimpleName(), Parcels.wrap(mCurrentUserModel));
        startActivity(intent);
        finish();
    }
}
