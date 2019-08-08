package com.example.dine_and_donate.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

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
    private TextView mBackToLogin;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private User mCreatedUser;
    private DatabaseReference mDatabase;
    private MenuItem mProgressSpinner;

    private ConstraintLayout constraintLayout;
    private ConstraintSet constraintSet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup_activity);

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.top_bar);

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

        mName.setVisibility(View.VISIBLE);
        mEmail.setVisibility(View.VISIBLE);
        mPassword.setVisibility(View.VISIBLE);
        mOrgPhone.setVisibility(View.GONE);
        mSignUpBtn.setVisibility(View.VISIBLE);

        constraintLayout = findViewById(R.id.constraintLayoutSignUp);

        //display specific text views depending on user type selected
        mSpinner = findViewById(R.id.user_options);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.user_types, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinner.setAdapter(adapter);
        mSpinner.setSelection(1);

        //show appropriate text views based on user type selection
        mSpinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = parent.getSelectedItem().toString();
                //show all fields except phone number for consumer user type
                if (selectedItem.equals("Dine and Donate")) {
                    mOrgPhone.setVisibility(View.GONE);
                    mName.setHint("Name");
                    constraintSet = new ConstraintSet();
                    constraintSet.clone(constraintLayout);
                    constraintSet.connect(mSignUpBtn.getId(), ConstraintSet.TOP, mPassword.getId(), ConstraintSet.BOTTOM, 30);
                    constraintSet.applyTo(constraintLayout);
                } else if (mSpinner.getSelectedItem().toString().equals("Fundraise")) {
                    mOrgPhone.setVisibility(View.VISIBLE);
                    mName.setHint("Organization Name");
                    constraintSet = new ConstraintSet();
                    constraintSet.clone(constraintLayout);
                    constraintSet.connect(mSignUpBtn.getId(), ConstraintSet.TOP, mOrgPhone.getId(), ConstraintSet.BOTTOM, 30);
                    constraintSet.applyTo(constraintLayout);
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
                if (validInfo) {
                    //if password length is less than 5, then user can not successfully sign up
                    if (mPassword.getText().toString().length() < 6) {
                        mPassword.setError("You must have at least 6 characters");
                    } else {
                        setLoading(true);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        mProgressSpinner = menu.findItem(R.id.miActionProgress);
        return true;
    }

    public void setLoading(boolean isLoading) {
        if(isLoading) {
            mProgressSpinner.setVisible(true);
        } else {
            mProgressSpinner.setVisible(false);
        }
    }

    //method to check if fields are empty
    private boolean emptyField(EditText text) {
        String userEmail = text.getText().toString().trim();
        //return true if field is empty
        if (userEmail.isEmpty()) {
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
                            mCreatedUser = writeNewUser(mUser.getUid(), mName.getText().toString(), email, mSpinner.getSelectedItem().toString().equals("Fundraise"), "");
                            navigationHelper(HomeActivity.class);
                        } else {
                            // If sign in fails, display a message to the user.
                            FirebaseAuthException e = (FirebaseAuthException) task.getException();
                            Log.e("LoginActivity", "Failed Registration", e);
                            Toast.makeText(SignUpActivity.this, "Sign up failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private User writeNewUser(String userId, String name, String email, boolean isOrg, String profPicUri) {
        User user;
        if (!isOrg) {
            user = new User(name, email, profPicUri);
        } else {
            user = new User(name, email, mOrgPhone.getText().toString(), profPicUri);
        }
        mDatabase.child("users").child(userId).setValue(user);
        return user;
    }

    private void navigationHelper(Class goToClass) {
        Intent intent = new Intent(SignUpActivity.this, goToClass);
        intent.putExtra(User.class.getSimpleName(), Parcels.wrap(mCreatedUser));
        startActivity(intent);
        finish();
        setLoading(false);
    }
}