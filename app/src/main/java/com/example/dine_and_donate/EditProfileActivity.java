package com.example.dine_and_donate;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class EditProfileActivity extends AppCompatActivity {

    private EditText mEditName;
    private EditText mEditNumber;
    private ImageButton mClearName;
    private ImageButton mClearNumber;
    private ImageView mProfPic;
    private Button mSaveBtn;

    private FirebaseDatabase mDatabase;
    private FirebaseUser mFbUser;
    private DatabaseReference mRef;
    private DatabaseReference ref;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_prof_activity);

        mEditName = findViewById(R.id.edit_name_et);
        mEditNumber = findViewById(R.id.edit_number_et);
        mClearName = findViewById(R.id.edit_name_btn);
        mClearNumber = findViewById(R.id.edit_number_btn);
        mSaveBtn = findViewById(R.id.save_btn);

        mClearName.setVisibility(View.INVISIBLE);
        mClearNumber.setVisibility(View.INVISIBLE);


        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();
        mFbUser = FirebaseAuth.getInstance().getCurrentUser();
        mRef = mDatabase.getReference();
        ref = mRef.child("users");

        mEditName.setText(ref.child(mFbUser.getUid()).child("name").getKey());
        mEditName.setText(ref.child(mFbUser.getUid()).child("phoneNumber").getKey());


        mClearName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditName.setText("");
            }
        });

        mClearNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditNumber.setText("");
            }
        });

        mSaveBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                ref.child(mFbUser.getUid()).child("name").setValue(mEditName.getText());
                ref.child(mFbUser.getUid()).child("phoneNumber").setValue(mEditNumber.getText());
            }
        });

    }



}
