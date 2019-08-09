package com.example.dine_and_donate.Activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.dine_and_donate.Models.Event;
import com.example.dine_and_donate.Models.User;
import com.example.dine_and_donate.R;
import com.example.dine_and_donate.UploadUtil;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.parceler.Parcels;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class EventActivity extends AppCompatActivity {

    final private static int GALLERY_REQUEST_CODE = 100;
    final private static String[] months = {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};

    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mRef;
    private StorageReference mStorageRef;
    private Event newEvent;

    private CalendarView mCalendarView;
    private int mMonth;
    private int mDay;
    private int mYear;
    private TimePicker mStartTimePicker;
    private TimePicker mEndTimePicker;

    private TextView mTvLocation;
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
    private Event mEditEvent;
    private String mLocationString;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_activity);

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.top_bar);

        mAuth = FirebaseAuth.getInstance();
        mFirebaseCurrentUser = mAuth.getCurrentUser();

        mDatabase = FirebaseDatabase.getInstance();
        mRef = mDatabase.getReference();
        mStorageRef = FirebaseStorage.getInstance().getReference();

        mCalendarView = findViewById(R.id.cvChooseDate);
        mCalendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
                mMonth = month;
                mDay = dayOfMonth;
                mYear = year;
            }
        });

        mStartTimePicker = findViewById(R.id.startTimePicker);
        mStartTimePicker.setEnabled(true);
        mEndTimePicker = findViewById(R.id.endTimePicker);
        mEndTimePicker.setEnabled(true);

        mTvLocation = findViewById(R.id.tvLocation);
        mEtEventInfo = findViewById(R.id.etEventInfo);
        mBtnCreate = findViewById(R.id.create_event);
        mChooseImage = findViewById(R.id.btnChoosePhoto);
        mVoucherImageView = findViewById(R.id.ivVoucherImage);

        mCurrUser = Parcels.unwrap(getIntent().getParcelableExtra(User.class.getSimpleName()));
        mCreatedEvents = mCurrUser.getSavedEventsIDs();

        final Intent intent = getIntent();
        final String yelpId = intent.getStringExtra("yelpId");
        mEditEvent = Parcels.unwrap(intent.getParcelableExtra(Event.class.getSimpleName()));
        mLocationString = intent.getStringExtra("location");
        mTvLocation.setText(mLocationString);

        // setting defaults to what was previously saved
        if (mEditEvent != null) {
            mCalendarView.setDate(mEditEvent.startTime);
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(mEditEvent.startTime);
            mStartTimePicker.setHour(cal.get(Calendar.HOUR));
            mStartTimePicker.setMinute(cal.get(Calendar.MINUTE));
            cal.setTimeInMillis(mEditEvent.endTime);
            mEndTimePicker.setHour(cal.get(Calendar.HOUR));
            mEndTimePicker.setMinute(cal.get(Calendar.MINUTE));
            mTvLocation.setText(mEditEvent.locationString);
            mEtEventInfo.setText(mEditEvent.info);
            Glide.with(this)
                    .load(mEditEvent.imageUrl)
                    .into(mVoucherImageView);
        }

        mChooseImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickFromGallery();
            }
        });

        mBtnCreate.setOnClickListener(new View.OnClickListener() {
            final Uri[] downloadUri = new Uri[1];

            @Override
            public void onClick(View v) {
                if (mEditEvent != null) {
                    writeEvent(yelpId, mEditEvent.imageUrl);
                } else {
                    if (mSelectedImage == null) {
                        Bitmap bitmap = createVoucher();
                        mSelectedImage = getImageUri(getApplicationContext(), bitmap);
                    }
                    final StorageReference ref = mStorageRef.child("images/" + mSelectedImage.getLastPathSegment());
                    UploadTask uploadTask = ref.putFile(mSelectedImage);

                    Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                        @Override
                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                            if (!task.isSuccessful()) {
                                throw task.getException();
                            }
                            // Continue with the task to get the download URL
                            return ref.getDownloadUrl();
                        }
                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful()) {
                                downloadUri[0] = task.getResult();
                                String s = downloadUri[0].toString();
                                writeEvent(yelpId, s);
                            } else {
                                // Handle failures
                            }
                        }
                    });
                }
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void writeEvent(final String yelpId, String url) {
        String orgId = mFirebaseCurrentUser.getUid();
        Date startTime = dateFromPicker(mStartTimePicker);
        Date endTime = dateFromPicker(mEndTimePicker);
        if(startTime.after(endTime)) {
            Toast.makeText(this, "Start time must be before end time", Toast.LENGTH_LONG).show();
            return;
        }
        if(!startTime.after(new Date())) {
            Toast.makeText(this, "Date of event must must be after current date", Toast.LENGTH_SHORT).show();
            return;
        }
        String info = mEtEventInfo.getText().toString();

        String id = mEditEvent == null ? UUID.randomUUID().toString() : mEditEvent.eventId;
        newEvent = new Event(orgId, yelpId, mLocationString, startTime.getTime(), endTime.getTime(), info, url, id);
        mRef.child("events").child(yelpId).child(id).setValue(newEvent, new DatabaseReference.CompletionListener() {
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

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Result code is RESULT_OK only if the user selects an Image
        if (resultCode == Activity.RESULT_OK)
            switch (requestCode) {
                case GALLERY_REQUEST_CODE:
                    //data.getData returns the content URI for the selected Image
                    mSelectedImage = data.getData();
                    mVoucherImageView.setImageURI(mSelectedImage);
                    break;
            }
    }

    private void pickFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        String[] mimeTypes = {"image/jpeg", "image/png"};
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
        startActivityForResult(intent, GALLERY_REQUEST_CODE);
    }

    private Date dateFromPicker(TimePicker tp) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, mYear);
        cal.set(Calendar.MONTH, mMonth);
        cal.set(Calendar.DAY_OF_MONTH, mDay);
        cal.set(Calendar.HOUR_OF_DAY, tp.getCurrentHour());
        cal.set(Calendar.MINUTE, tp.getCurrentMinute());
        return cal.getTime();
    }

    private Bitmap createVoucher() {
        Bitmap src = BitmapFactory.decodeResource(getResources(), R.drawable.background1);
        Bitmap dest = Bitmap.createBitmap(src.getWidth(), src.getHeight(), Bitmap.Config.ARGB_8888);

        Canvas cs = new Canvas(dest);
        Paint tPaint = new Paint();
        tPaint.setTextSize(80);
        tPaint.setColor(getResources().getColor(R.color.brown));
        tPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        tPaint.setTextAlign(Paint.Align.CENTER);
        cs.drawBitmap(src, 0f, 0f, null);

        float x_coord = src.getWidth()/2;
        cs.drawText("Come support the Dine & Donate for", x_coord, src.getHeight() /3 , tPaint);
        cs.drawText(mCurrUser.name, x_coord, src.getHeight() / 3 + 200, tPaint);
        String[] location = mLocationString.split("\\r?\\n");
        cs.drawText("@ " + location[0], x_coord, src.getHeight() / 3 + 400, tPaint);
        tPaint.setTextSize(55);
        cs.drawText(location[1], x_coord, src.getHeight() / 3 + 600, tPaint);
        String month = months[mMonth];
        int day = mDay;
        Date start = dateFromPicker(mStartTimePicker);
        int startHour = start.getHours();
        String startTime = startHour > 12 ? (startHour % 12) + ":" + start.getMinutes() + "pm" : startHour + ":" + start.getMinutes() + "am";
        Date end = dateFromPicker(mEndTimePicker);
        int endHour = end.getHours();
        String endTime = endHour > 12 ? (endHour % 12) + ":" + end.getMinutes() + "pm" : endHour + ":" + end.getMinutes() + "am";
        cs.drawText(month + " " + day + ", " + startTime + " - " + endTime, x_coord, src.getHeight() / 3 + 800, tPaint);
        try {
            dest.compress(Bitmap.CompressFormat.JPEG, 100, new FileOutputStream(new File("/sdcard/ImageAfterAddingText.jpg")));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return dest;
    }

    private Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

}