package com.example.dine_and_donate.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.dine_and_donate.Models.User;
import com.example.dine_and_donate.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.parceler.Parcels;

public class SignUpActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private Spinner mSpinner;
    private EditText mName;
    private EditText mEmail;
    private EditText mPassword;
    private EditText mOrgPhone;
    private Button mSignUpBtn;
    private Button mBackToLogin;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private User mCreatedUser;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup_activity);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mCreatedUser = new User();

        mSpinner = findViewById(R.id.user_options);
        mName = findViewById(R.id.et_name);
        mEmail = findViewById(R.id.et_email);
        mPassword = findViewById(R.id.et_password2);
        mOrgPhone = findViewById(R.id.et_org_phone);
        mSignUpBtn = findViewById(R.id.final_signup_btn);
        mBackToLogin = findViewById(R.id.back_to_login_btn);

        mName.setVisibility(View.GONE);
        mEmail.setVisibility(View.GONE);
        mPassword.setVisibility(View.GONE);
        mOrgPhone.setVisibility(View.GONE);
        mSignUpBtn.setVisibility(View.GONE);

        //display specific text views depending on user type selected
        mSpinner = findViewById(R.id.user_options);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.user_types, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinner.setAdapter(adapter);

        //show appropriate text views based on user type selection
        mSpinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = parent.getSelectedItem().toString();
                //show all fields except phone number for consumer user type
                if (selectedItem.equals("Consumer")) {
                    mName.setVisibility(View.VISIBLE);
                    mEmail.setVisibility(View.VISIBLE);
                    mPassword.setVisibility(View.VISIBLE);
                    mSignUpBtn.setVisibility(View.VISIBLE);
                    mOrgPhone.setVisibility(View.GONE);
                }  else if(mSpinner.getSelectedItem().toString().equals("Organization")) {
                    mOrgPhone.setVisibility(View.VISIBLE);
                    mName.setVisibility(View.VISIBLE);
                    mEmail.setVisibility(View.VISIBLE);
                    mSignUpBtn.setVisibility(View.VISIBLE);
                    mPassword.setVisibility(View.VISIBLE);
                } else {
                    mName.setVisibility(View.GONE);
                    mEmail.setVisibility(View.GONE);
                    mPassword.setVisibility(View.GONE);
                    mOrgPhone.setVisibility(View.GONE);
                    mSignUpBtn.setVisibility(View.GONE);
                }
            }

            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //go to main page (map) once signed up
        mSignUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean validInfo = !(emptyField(mName) && emptyField(mEmail) && emptyField(mPassword) && emptyField(mOrgPhone));
                //if none of the fields are empty, then sign up information is valid
                if(validInfo) {
                    //if password length is less than 5, then user can not successfully sign up
                    if(mPassword.getText().toString().length() < 6) {
                        mPassword.setError("You must have at least 6 characters");
                    } else {
                        createAccount(mEmail.getText().toString(), mPassword.getText().toString());
                    }
                }
            }
        });

        //go back to login page
        mBackToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigationHelper(LoginActivity.class);
            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }

    //method to check if fields are empty
    private boolean emptyField(EditText text) {
        String userEmail = text.getText().toString().trim();
        //return true if field is empty
        if(userEmail.isEmpty()) {
            text.setError("Field can not be empty");
            return true;
        } else {
            text.setError(null);
            return false;
        }
    }

    private void createAccount(final String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        mUser = mAuth.getCurrentUser();
                        Toast.makeText(SignUpActivity.this, "Account created", Toast.LENGTH_SHORT).show();
                        mCreatedUser = writeNewUser(mUser.getUid(), mName.getText().toString(), email, mSpinner.getSelectedItem().toString().equals("Organization"));
                        navigationHelper(HomeActivity.class);
                    } else {
                        // If sign in fails, display a message to the user.
                        //Log.w("create", "createUserWithEmail:failure", task.getException());
                        FirebaseAuthException e = (FirebaseAuthException )task.getException();
                        Log.e("LoginActivity", "Failed Registration", e);
                        Toast.makeText(SignUpActivity.this, "Sign up failed", Toast.LENGTH_SHORT).show();
                    }
                }
            });
    }

    private User writeNewUser(String userId, String name, String email, boolean isOrg) {
        User user;
        if(!isOrg) {
            user = new User(name, email);
        } else {
            user = new User(name, email, mOrgPhone.getText().toString());
        }
        mDatabase.child("users").child(userId).setValue(user);
        return user;
    }

    private void navigationHelper(Class goToClass) {
        Intent intent = new Intent(SignUpActivity.this, goToClass);
        intent.putExtra(User.class.getSimpleName(), Parcels.wrap(mCreatedUser));
        startActivity(intent);
        finish();
    }
}