package com.example.dine_and_donate;

import android.content.Intent;
import android.os.Bundle;
import android.util.Half;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.dine_and_donate.Models.Event;
import com.example.dine_and_donate.Models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class EventActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    CalendarView calendarView;

    AutoCompleteTextView acSearch;
    Spinner startHour;
    Spinner startMin;
    Spinner startHalf;
    Spinner endHour;
    Spinner endMin;
    Spinner endHalf;

    EditText etEventInfo;
    Button btnCreate;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_activity);

        mAuth = FirebaseAuth.getInstance();
        final FirebaseUser currentUser = mAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference();


        calendarView = findViewById(R.id.calendarView4);

        acSearch = findViewById(R.id.acSearch);
        startHour = findViewById(R.id.startHour);
        startMin = findViewById(R.id.startMin);
        startHalf = findViewById(R.id.startHalf);
        endHour = findViewById(R.id.endHour);
        endMin = findViewById(R.id.endMin);
        endHalf = findViewById(R.id.endHalf);
        etEventInfo = findViewById(R.id.etEventInfo);
        btnCreate = findViewById(R.id.create_event);

        final Intent intent = getIntent();
        final String location = intent.getStringExtra("location");
        acSearch.setText(location);


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
        startHour.setAdapter(adapter);
        endHour.setAdapter(adapter);

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, minsArray);
        startMin.setAdapter(adapter);
        endMin.setAdapter(adapter);

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, halfsArray);
        startHalf.setAdapter(adapter);
        endHalf.setAdapter(adapter);

        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String orgId = currentUser.getUid();
                double locationLong = intent.getDoubleExtra("longitude", 0);
                double locationLat = intent.getDoubleExtra("latitude", 0);
                long eventDate = calendarView.getDate();
                long startTime = convert(eventDate, startHour.getSelectedItemPosition()+1, startMin.getSelectedItemPosition(), startHalf.getSelectedItem().equals("PM"));
                long endTime = convert(eventDate, endHour.getSelectedItemPosition()+1, endMin.getSelectedItemPosition(), endHalf.getSelectedItem().equals("PM"));
                String info = etEventInfo.getText().toString();
                Event newEvent = new Event(orgId, locationLong, locationLat, location, startTime, endTime, info);
                mDatabase.child("events").child(UUID.randomUUID().toString()).setValue(newEvent);
                Intent newIntent = new Intent(EventActivity.this, MapActivity.class);
                startActivity(newIntent);
            }
        });
    }

    private long convert(long day, int hour, int min, boolean isPM) {
        return isPM ? 2*(day + (hour * 3600000) + (min * 60000)) : (day + (hour * 3600000) + (min * 60000));
    }



}
