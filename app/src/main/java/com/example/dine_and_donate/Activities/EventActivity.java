package com.example.dine_and_donate.Activities;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
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

import com.example.dine_and_donate.Models.Event;
import com.example.dine_and_donate.Models.User;
import com.example.dine_and_donate.R;
import com.example.dine_and_donate.UploadUtil;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class EventActivity extends AppCompatActivity {

    final private static int GALLERY_REQUEST_CODE = 100;

    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mRef;
    private StorageReference mStorageRef;
    private Event newEvent;

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
    private ImageView mVoucherImageView;
    private User mCurrUser;
    private Uri mSelectedImage;
    private FirebaseUser mFirebaseCurrentUser;
    private Map<String, String> mCreatedEvents;
    private UploadUtil uploadUtil;
    private Task<Uri> urlTask;
    private Intent mIntent;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_activity);

        mAuth = FirebaseAuth.getInstance();
        mFirebaseCurrentUser = mAuth.getCurrentUser();

        mDatabase = FirebaseDatabase.getInstance();
        mRef = mDatabase.getReference();
        mStorageRef = FirebaseStorage.getInstance().getReference();

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
        mVoucherImageView = findViewById(R.id.ivVoucherImage);

        mIntent = new Intent();
        mCurrUser = Parcels.unwrap(getIntent().getParcelableExtra(User.class.getSimpleName()));
        mCreatedEvents = mCurrUser.getSavedEventsIDs();

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

        //Todo: make time selection better
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
                uploadUtil = new UploadUtil(EventActivity.this);
                uploadUtil.pickFromGallery(mIntent);
            }
        });

        mBtnCreate.setOnClickListener(new View.OnClickListener() {

            final Uri[] downloadUri = new Uri[1];
            @Override
            public void onClick(View v) {
                uploadUtil.inOnClick(v, mSelectedImage, downloadUri, mStorageRef, urlTask);
            }
        });
    }

    private void writeEvent(Intent intent, String url, String location) {
        String orgId = mFirebaseCurrentUser.getUid();
        final String yelpId = intent.getStringExtra("yelpID");
        long eventDate = mCalendarView.getDate();
        long startTime = convert(eventDate, mStartHour.getSelectedItemPosition()+1, mStartMin.getSelectedItemPosition(), mStartHalf.getSelectedItem().equals("PM"));
        long endTime = convert(eventDate, mEndHour.getSelectedItemPosition()+1, mEndMin.getSelectedItemPosition(), mEndHalf.getSelectedItem().equals("PM"));
        String info = mEtEventInfo.getText().toString();
        newEvent = new Event(orgId, yelpId, location, startTime, endTime, info, url);
        mRef.child("events").child(yelpId).child(UUID.randomUUID().toString()).setValue(newEvent, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                //there is not error, can add event to database
                if (databaseError == null) {
                    mCreatedEvents.put(databaseReference.getKey(), yelpId);
                    mRef.child("users").child(mFirebaseCurrentUser.getUid()).child("Events").setValue(mCreatedEvents);
                }
                Intent intent = new Intent(EventActivity.this, HomeActivity.class);
                intent.putExtra(User.class.getSimpleName(), Parcels.wrap(mCurrUser));
                startActivity(intent);
            }
        });
        finish();
    }

    public void onActivityResult(int requestCode,int resultCode,Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        // Result code is RESULT_OK only if the user selects an Image
        if (resultCode == Activity.RESULT_OK)
            switch (requestCode){
                case GALLERY_REQUEST_CODE:
                    //data.getData returns the content URI for the selected Image
                    mSelectedImage = data.getData();
                    mVoucherImageView.setImageURI(mSelectedImage);
                    break;
            }
    }

    private long convert(long day, int hour, int min, boolean isPM) {
        return isPM ? (day + (2 * hour * 3600000) + (min * 60000)) : (day + (hour * 3600000) + (min * 60000));
    }
}