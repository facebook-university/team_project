package com.example.dine_and_donate;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

public class SignUpActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private Spinner spinner;
    private EditText name;
    private EditText email;
    private EditText password;
    private EditText orgPhone;
    private Button signUpBtn;
    private Button backToLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup_activity);

        spinner = findViewById(R.id.user_options);
        name = findViewById(R.id.et_name);
        email = findViewById(R.id.et_email);
        password = findViewById(R.id.et_password2);
        orgPhone = findViewById(R.id.et_org_phone);
        signUpBtn = findViewById(R.id.final_signup_btn);
        backToLogin = findViewById(R.id.back_to_login_btn);

        name.setVisibility(View.GONE);
        email.setVisibility(View.GONE);
        password.setVisibility(View.GONE);
        orgPhone.setVisibility(View.GONE);
        signUpBtn.setVisibility(View.GONE);

        //display specific text views depending on user type selected
        spinner = findViewById(R.id.user_options);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.user_types, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        //show appropriate text views based on user type selection
        spinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = parent.getSelectedItem().toString();
                //show all fields except phone number for consumer user type
                if (selectedItem.equals("Consumer")) {
                    name.setVisibility(View.VISIBLE);
                    email.setVisibility(View.VISIBLE);
                    password.setVisibility(View.VISIBLE);
                    signUpBtn.setVisibility(View.VISIBLE);
                    orgPhone.setVisibility(View.GONE);
                }  else if(spinner.getSelectedItem().toString().equals("Organization")) {
                    orgPhone.setVisibility(View.VISIBLE);
                    name.setVisibility(View.VISIBLE);
                    email.setVisibility(View.VISIBLE);
                    signUpBtn.setVisibility(View.VISIBLE);
                    password.setVisibility(View.VISIBLE);
                } else {
                    name.setVisibility(View.GONE);
                    email.setVisibility(View.GONE);
                    password.setVisibility(View.GONE);
                    orgPhone.setVisibility(View.GONE);
                    signUpBtn.setVisibility(View.GONE);
                }
            }

            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //go to main page (map) once signed up
        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean validInfo = !(emptyField(name) && emptyField(email) && emptyField(password) && emptyField(orgPhone));
                //if none of the fields are empty, then sign up information is valid
                if(validInfo) {
                    //if password length is less than 5, then user can not successfully sign up
                    if(password.getText().toString().length() < 5) {
                        password.setError("You must have at least 5 characters");
                    } else {
                        Intent intent = new Intent(SignUpActivity.this, MapActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }
            }
        });

        //go back to login page
        backToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
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
}