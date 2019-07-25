package com.example.dine_and_donate;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.dine_and_donate.Activities.HomeActivity;
import com.example.dine_and_donate.Models.Event;
import com.example.dine_and_donate.Models.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class EventActivity extends AppCompatActivity {

    final private static int GALLERY_REQUEST_CODE = 100;

    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mRef;
    private StorageReference mStorageRef;

    private CalendarView mCalendarView;

    private AutoCompleteTextView mAcSearch;
    private Spinner mStartHour;
    private Spinner mStartMin;
    private Spinner mStartHalf;
    private Spinner mEndHour;
    private Spinner mEndMin;
    private Spinner mEndHalf;
    private EditText mEtEventInfo;
    private Button mBtnCreate;
    private Button mChooseImage;
    private ImageView ivVoucher;

    private User mCurrentUser;

    private Uri mSelectedImage;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_activity);

        mCurrentUser = Parcels.unwrap(getIntent().getParcelableExtra(User.class.getSimpleName()));


        mAuth = FirebaseAuth.getInstance();
        final FirebaseUser currentUser = mAuth.getCurrentUser();

        mDatabase = FirebaseDatabase.getInstance();
        mRef = mDatabase.getReference();

        mCalendarView = findViewById(R.id.cvChooseDate);

        mAcSearch = findViewById(R.id.acSearch);
        mStartHour = findViewById(R.id.startHour);
        mStartMin = findViewById(R.id.startMin);
        mStartHalf = findViewById(R.id.startHalf);
        mEndHour = findViewById(R.id.endHour);
        mEndMin = findViewById(R.id.endMin);
        mEndHalf = findViewById(R.id.endHalf);
        mEtEventInfo = findViewById(R.id.etEventInfo);
        mBtnCreate = findViewById(R.id.create_event);
        mChooseImage = findViewById(R.id.btnChoosePhoto);
        ivVoucher = findViewById(R.id.ivVoucherImage);

        final Intent intent = getIntent();
        final String location = intent.getStringExtra("location");
        mAcSearch.setText(location);

        List<String> hoursArray = new ArrayList<>();
        List<String> minsArray = new ArrayList<>();
        List<String> halfsArray = new ArrayList<>();

        halfsArray.add("AM");
        halfsArray.add("PM");

        for(int i = 0; i < 60; i++) {
            if(i < 13 && i != 0) {
                hoursArray.add(Integer.toString(i));
            }
            if(i < 10) {
                minsArray.add("0" + i);
            } else {
                minsArray.add(Integer.toString(i));
            }
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, hoursArray);
        mStartHour.setAdapter(adapter);
        mEndHour.setAdapter(adapter);

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, minsArray);
        mStartMin.setAdapter(adapter);
        mEndMin.setAdapter(adapter);

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, halfsArray);
        mStartHalf.setAdapter(adapter);
        mEndHalf.setAdapter(adapter);

        mChooseImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickFromGallery();
            }
        });

        mBtnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(mSelectedImage != null) {
                    StorageReference ref = mStorageRef.child("images/"+mSelectedImage.getLastPathSegment());
                    UploadTask uploadTask = ref.putFile(mSelectedImage);
                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            // Handle unsuccessful uploads
                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                        }
                    });
                }

                String orgId = currentUser.getUid();
                String yelpId = intent.getStringExtra("yelpID");
                long eventDate = mCalendarView.getDate();
                long startTime = convert(eventDate, mStartHour.getSelectedItemPosition()+1, mStartMin.getSelectedItemPosition(), mStartHalf.getSelectedItem().equals("PM"));
                long endTime = convert(eventDate, mEndHour.getSelectedItemPosition()+1, mEndMin.getSelectedItemPosition(), mEndHalf.getSelectedItem().equals("PM"));
                String info = mEtEventInfo.getText().toString();
                Event newEvent = new Event(orgId, location, yelpId, info, startTime, endTime);
                mRef.child("events").child(yelpId).child(UUID.randomUUID().toString()).setValue(newEvent);
                Intent newIntent = new Intent(EventActivity.this, HomeActivity.class);
                newIntent.putExtra("isOrg", intent.getBooleanExtra("isOrg", false));
                startActivity(newIntent);
            }
        });
    }


    public void onActivityResult(int requestCode,int resultCode,Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        // Result code is RESULT_OK only if the user selects an Image
        if (resultCode == Activity.RESULT_OK)
            switch (requestCode){
                case GALLERY_REQUEST_CODE:
                    //data.getData returns the content URI for the selected Image
                    mSelectedImage = data.getData();
                    ivVoucher.setImageURI(mSelectedImage);
                    break;
            }
    }

    private void pickFromGallery(){
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        String[] mimeTypes = {"image/jpeg", "image/png"};
        intent.putExtra(Intent.EXTRA_MIME_TYPES,mimeTypes);
        startActivityForResult(intent, GALLERY_REQUEST_CODE);
    }

    private long convert(long day, int hour, int min, boolean isPM) {
        return isPM ? (day + (2 * hour * 3600000) + (min * 60000)) : (day + (hour * 3600000) + (min * 60000));
    }
}