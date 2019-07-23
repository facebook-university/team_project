package com.example.dine_and_donate;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class EditProfileActivity extends AppCompatActivity {

    private EditText mEditName;
    private EditText mEditNumber;
    private ImageButton mClearName;
    private ImageButton mClearNumber;
    private ImageView mProfPic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_prof_activity);

        mEditName = findViewById(R.id.edit_name_et);
        mEditNumber = findViewById(R.id.edit_number_et);
        mClearName = findViewById(R.id.edit_name_btn);
        mClearNumber = findViewById(R.id.edit_number_btn);



    }
}
