package com.example.dine_and_donate.Activities;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.dine_and_donate.Models.User;
import com.example.dine_and_donate.R;
import com.example.dine_and_donate.UploadUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.parceler.Parcels;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.example.dine_and_donate.UploadUtil.GALLERY_REQUEST_CODE;

public class EditProfileActivity extends AppCompatActivity {

    private EditText mEditName;
    private EditText mEditNumber;
    private ImageButton mClearName;
    private ImageButton mClearNumber;
    private Button mSaveBtn;
    private TextView mNumberTextView;
    private Button mChangeProfPic;
    private CircleImageView mProfPic;

    private FirebaseDatabase mDatabase;
    private FirebaseUser mFbUser;
    private DatabaseReference mRef;
    private DatabaseReference mRefForUser;
    private StorageReference mStorageRef;

    private User mCurrentUser;
    private Uri mLocalImageUri;
    private UploadUtil uploadUtil;
    private Intent mIntent;
    private Task<Uri> urlTask;
    private Uri[] downloadUri;

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
        mChangeProfPic = findViewById(R.id.change_prof_pic_btn);
        mProfPic = findViewById(R.id.circular_edit_prof_pic);

        mDatabase = FirebaseDatabase.getInstance();
        mFbUser = FirebaseAuth.getInstance().getCurrentUser();
        mRef = mDatabase.getReference(); //need an instance of database reference
        mRefForUser = mRef.child("users").child(mFbUser.getUid());

        downloadUri = new Uri[1];
        mIntent = new Intent(Intent.ACTION_PICK);
        mClearName.setVisibility(View.INVISIBLE);
        mStorageRef = FirebaseStorage.getInstance().getReference();
        mCurrentUser = Parcels.unwrap(getIntent().getParcelableExtra(User.class.getSimpleName()));

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
                mCurrentUser.setName(newName);
                mRef.child("users").child(mFbUser.getUid()).child("name").setValue(newName);

                if (mLocalImageUri != null) {
                    OnCompleteListener onCompleteListener = new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful()) {
                                downloadUri[0] = task.getResult(); // remote image uri
                                Log.d("task", task.getResult() + "");
                                Log.d("array", downloadUri[0] + "");
                                mCurrentUser.setProfPic(downloadUri[0].toString());

                                Log.d("arrayElement", downloadUri[0] + "");
                                mRefForUser.child("profPic").setValue(downloadUri[0].toString());
                                Intent intent = new Intent(EditProfileActivity.this, HomeActivity.class);
                                intent.putExtra(User.class.getSimpleName(), Parcels.wrap(mCurrentUser));
                                startActivity(intent);

                            }
                        }
                    };

                    Log.d("changeBtn", mCurrentUser.getProfPic() + "");
                    uploadUtil.inOnClick(v, mLocalImageUri, downloadUri, mStorageRef, urlTask, onCompleteListener);
                } else {
                    Intent intent = new Intent(EditProfileActivity.this, HomeActivity.class);
                    intent.putExtra(User.class.getSimpleName(), Parcels.wrap(mCurrentUser));
                    startActivity(intent);
                }
            }
        });

        mEditName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setState(mEditName, mClearName);
            }
        });

        mChangeProfPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadUtil = new UploadUtil(EditProfileActivity.this);
                uploadUtil.pickFromGallery(mIntent);
            }
        });
    }

    private void setState(EditText etField, ImageButton clearField) {
        if (etField.getText().toString().isEmpty()) {
            clearField.setVisibility(View.INVISIBLE);
        } else {
            clearField.setVisibility(View.VISIBLE);
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Result code is RESULT_OK only if the user selects an Image
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case GALLERY_REQUEST_CODE:
                    //data.getData returns the content URI for the selected Image
                    mLocalImageUri = data.getData();
                    mProfPic.setImageURI(mLocalImageUri);
                    break;
            }
        }
    }
}


