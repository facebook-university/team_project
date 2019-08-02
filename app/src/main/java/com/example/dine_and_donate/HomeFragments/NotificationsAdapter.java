package com.example.dine_and_donate.HomeFragments;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dine_and_donate.Models.Notification;
import com.example.dine_and_donate.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class NotificationsAdapter extends RecyclerView.Adapter<NotificationsAdapter.ViewHolder> {

    private List<Notification> mNotifications;
    private Context mContext;
    private DatabaseReference mRef;
    private DatabaseReference mNotificationsRef;
    private FirebaseDatabase mDatabase;

    //pass in notifications array in the constructor
    public NotificationsAdapter(List<Notification> notifications) {
        mNotifications = notifications;
    }

    //for each row, inflate the layout and cache references into ViewHolder (pass them into ViewHolder class)
    //only invoked when new row needs to be created
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(mContext);
        mDatabase = FirebaseDatabase.getInstance();
        mRef = mDatabase.getReference();

        View feedView = inflater.inflate(R.layout.item_notification, parent, false);
        ViewHolder holder = new ViewHolder((feedView));
        return holder;
    }

    //bind the values based on the position of the element, called as a user scrolls down
    public void onBindViewHolder(final ViewHolder holder, int position) {
        //get data according to position
        Notification notification = mNotifications.get(position);
        String eventId = notification.getEventId();
        String yelpId = notification.getYelpId();
        mNotificationsRef = mRef.child("events").child(yelpId).child(eventId);
        mNotificationsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                long millis = Long.parseLong(dataSnapshot.child("startTime").getValue().toString());
                DateFormat simple = new SimpleDateFormat("dd MMM HH:mm");
                Date result = new Date(millis);
                String formattedDate = "<b>" + simple.format(result) + "</b>";
                holder.mStartDate.setText(Html.fromHtml(formattedDate));
                final String[] org = {""};
                String formattedInfo = "<b>" + org[0] + "</b>" + " organized an event at " + "<b>" + getRestaurantName(dataSnapshot.child("locationString").getValue().toString()) +"</b> "  + "!";
                holder.mPartner.setText(Html.fromHtml(formattedInfo));
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        holder.mNotifiedAt.setText("2 m ago");
        //holder.mNotifiedAt.setText(DateUtils.getRelativeTimeSpanString(notification.getCreatedAt(), System.currentTimeMillis(),DateUtils.M).toString());
    }

    private String getRestaurantName(String entireLocation) {
        int positionOfNewLine = entireLocation.indexOf("\n");
        String restName = "";
        if (positionOfNewLine >= 0) {
            restName = entireLocation.substring(0, positionOfNewLine);
        }
        return restName;
    }

    @Override
    //return size of list
    public int getItemCount() {
        return mNotifications.size();
    }

    //create ViewHolder pattern that will contain all the find view by ID lookups
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public ImageView mOrgPic;
        public TextView mStartDate;
        public TextView mPartner;
        public TextView mNotifiedAt;
        public androidx.constraintlayout.widget.ConstraintLayout mItem;

        //constructor takes in an inflated layout
        public ViewHolder(View itemView) {
            super(itemView);
            mOrgPic = itemView.findViewById(R.id.org_iv);
            mStartDate = itemView.findViewById(R.id.date_tv);
            mPartner = itemView.findViewById(R.id.partnered_with_tv);
            mItem =  itemView.findViewById(R.id.notification_layout);
            mNotifiedAt = itemView.findViewById(R.id.notified_at_tv);
            mItem.setOnClickListener(this);
        }

        @Override
        //TODO not finished
        public void onClick(View v) {
            MapFragment mMapFragment = new MapFragment();
            ProfileFragment mProfileFragment = new ProfileFragment();
            AppCompatActivity activity = (AppCompatActivity) v.getContext();
//            Fragment fragment = (getIntent().getStringExtra("defaultFragment") != null) ? mMapFragment : mProfileFragment;
            Fragment fragment = new MapFragment();
            activity.getSupportFragmentManager().beginTransaction().replace(R.id.bottom_navigation, fragment).addToBackStack(null).commit();
        }
    }
}