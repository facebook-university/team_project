package com.example.dine_and_donate.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.dine_and_donate.Models.User;
import com.example.dine_and_donate.R;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.parceler.Parcels;

public class EditProfileActivity extends AppCompatActivity {

    private EditText mEditName;
    private EditText mEditNumber;
    private ImageButton mClearName;
    private ImageButton mClearNumber;
    private Button mSaveBtn;
    private TextView mNumberTextView;

    private FirebaseDatabase mDatabase;
    private FirebaseUser mFbUser;
    private DatabaseReference mRef;
    private DatabaseReference mRefForUser;

    private User mCurrentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_prof_activity);

        mEditName = findViewById(R.id.edit_name_et);
        mEditNumber = findViewById(R.id.edit_number_et);
        mClearName = findViewById(R.id.edit_name_btn);
        mClearNumber = findViewById(R.id.edit_number_btn);
        mSaveBtn = findViewById(R.id.save_btn);
        mNumberTextView = findViewById(R.id.edit_number_tv);

        mDatabase = FirebaseDatabase.getInstance();
        mFbUser = FirebaseAuth.getInstance().getCurrentUser();
        mRef = mDatabase.getReference(); //need an instance of database reference
        mRefForUser = mRef.child("users").child(mFbUser.getUid());


        mClearName.setVisibility(View.INVISIBLE);

        //retrieve values from database
        mRefForUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mEditName.setText(dataSnapshot.child("name").getValue().toString());
                mEditNumber.setVisibility(View.INVISIBLE);
                mClearNumber.setVisibility(View.INVISIBLE);
                mNumberTextView.setVisibility(View.INVISIBLE);
                if ((Boolean) dataSnapshot.child("isOrg").getValue()) {
                    mEditNumber.setVisibility(View.VISIBLE);
                    mClearNumber.setVisibility(View.VISIBLE);
                    mNumberTextView.setVisibility(View.VISIBLE);
                    mEditNumber.setText(dataSnapshot.child("phoneNumber").getValue().toString());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        //retrieve User object
        mCurrentUser = Parcels.unwrap(getIntent().getParcelableExtra(User.class.getSimpleName()));
        populateFields();
        setUpOnClickListeners();
    }

    private void populateFields() {
        mEditName.setText(mCurrentUser.name);
        mEditNumber.setVisibility(View.INVISIBLE);
        mClearNumber.setVisibility(View.INVISIBLE);
        mNumberTextView.setVisibility(View.INVISIBLE);
        if (mCurrentUser.isOrg) {
            mEditNumber.setVisibility(View.VISIBLE);
            mClearNumber.setVisibility(View.VISIBLE);
            mNumberTextView.setVisibility(View.VISIBLE);
            mEditNumber.setText(mCurrentUser.phoneNumber);
        }
    }

    private void setUpOnClickListeners() {
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
                final String newName = mEditName.getText().toString();
                mRef.child("users").child(mFbUser.getUid()).child("name").setValue(newName, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                        if (databaseError != null) {
                            Toast.makeText(EditProfileActivity.this, "Error Saving Data To Database", Toast.LENGTH_LONG).show();
                        } else {
                            mCurrentUser.setName(newName);
                        }
                        Intent intent = new Intent(EditProfileActivity.this, HomeActivity.class);
                        intent.putExtra(User.class.getSimpleName(), Parcels.wrap(mCurrentUser));
                        startActivity(intent);
                    }
                });
            }
        });

        mEditName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setState(mEditName, mClearName);
            }
        });
    }

    private void setState(EditText etField, ImageButton clearField) {
        if(etField.getText().toString().isEmpty()) {
            clearField.setVisibility(View.INVISIBLE);
        } else {
            clearField.setVisibility(View.VISIBLE);
        }
    }
}


