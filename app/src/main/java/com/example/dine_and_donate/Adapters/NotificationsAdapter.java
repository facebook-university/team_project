package com.example.dine_and_donate.Adapters;

import android.content.Context;
import android.text.Html;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.example.dine_and_donate.Activities.HomeActivity;
import com.example.dine_and_donate.Models.Notification;
import com.example.dine_and_donate.Models.User;
import com.example.dine_and_donate.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class NotificationsAdapter extends RecyclerView.Adapter<NotificationsAdapter.ViewHolder> {

    private List<Notification> mNotifications;
    private Context mContext;
    private DatabaseReference mRef;
    private DatabaseReference mEventsRef;
    private DatabaseReference mNotificationsRef;
    private FirebaseDatabase mDatabase;
    public FirebaseUser currentUser;
    private HashMap<String, User> mIdToOrg;

    //pass in notifications array in the constructor
    public NotificationsAdapter(List<Notification> notifications, HashMap<String, User> idToOrg) {
        mNotifications = notifications;
        this.mIdToOrg = idToOrg;
    }

    //for each row, inflate the layout and cache references into ViewHolder (pass them into ViewHolder class)
    //only invoked when new row needs to be created
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mDatabase = FirebaseDatabase.getInstance();
        mRef = mDatabase.getReference(); //need an instance of database reference
        mContext = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(mContext);

        final View view = inflater.inflate(R.layout.item_notification, parent, false);
        final NotificationsAdapter.ViewHolder viewHolder = new NotificationsAdapter.ViewHolder(view);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // todo: get location from event, pass to home activity
                HomeActivity homeActivity = (HomeActivity) mContext;
                homeActivity.setClickedOnID(viewHolder.notification.getYelpId());
                homeActivity.setExploreTab(null);
                homeActivity.setLoading(true);
            }
        });
        return viewHolder;
    }

    //bind the values based on the position of the element, called as a user scrolls down
    public void onBindViewHolder(final ViewHolder holder, int position) {
        //get data according to position
        Notification notification = mNotifications.get(position);
        String eventId = notification.getEventId();
        String yelpId = notification.getYelpId();
        holder.notification = notification;
        mNotificationsRef = mRef.child("events").child(yelpId).child(eventId);
        mNotificationsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User org = mIdToOrg.get(dataSnapshot.child("orgId").getValue().toString());
                String restName = dataSnapshot.child("locationString").getValue().toString().split("\\r?\\n")[0];
                String formattedInfo = "<b>" + org.name + "</b>" + " organized an event at " + "<b>" + restName + "</b>" + "!";
                holder.mPartner.setText(Html.fromHtml(formattedInfo));

                RequestOptions requestOptions = new RequestOptions()
                        .placeholder(R.drawable.instagram_user_outline_24);

                String profilePicUrl = org.getImageUrl() != null && !org.getImageUrl().equals("") ? org.imageUrl : "https://cdn2.iconfinder.com/data/icons/wedding-glyph-1/128/44-512.png";

                Glide.with(mContext)
                        .load(profilePicUrl)
                        .transform(new MultiTransformation<>(new CenterCrop(), new CircleCrop()))
                        .apply(requestOptions)
                        .into(holder.mOrgPic);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        CharSequence relativeDate =
                DateUtils.getRelativeTimeSpanString(Long.parseLong(notification.getCreatedAt()),
                        System.currentTimeMillis(),
                        DateUtils.MINUTE_IN_MILLIS,
                        DateUtils.FORMAT_ABBREV_TIME);

        holder.mStartDate.setText(relativeDate);
    }


    @Override
    //return size of list
    public int getItemCount() {
        return mNotifications.size();
    }

    //create ViewHolder pattern that will contain all the find view by ID lookups
    public class ViewHolder extends RecyclerView.ViewHolder  {
        public ImageView mOrgPic;
        public TextView mStartDate;
        public TextView mPartner;
        public Notification notification;
        public androidx.constraintlayout.widget.ConstraintLayout mItem;

        //constructor takes in an inflated layout
        public ViewHolder(View itemView) {
            super(itemView);
            mOrgPic = itemView.findViewById(R.id.org_iv);
            mStartDate = itemView.findViewById(R.id.date_tv);
            mPartner = itemView.findViewById(R.id.partnered_with_tv);
        }
    }
}