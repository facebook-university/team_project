package com.example.dine_and_donate;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.dine_and_donate.Models.Notification;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;


public class NotificationsAdapter extends RecyclerView.Adapter<NotificationsAdapter.ViewHolder> {

    private List<Notification> mNotifications;
    Context context;

    //pass in posts array in the constructor
    public NotificationsAdapter(List<Notification> notifications) {
        mNotifications = notifications;
    }

    //for each row, inflate the layout and cache references into ViewHolder (pass them into ViewHolder class)
    //only invoked when new row needs to be created
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View feedView = inflater.inflate(R.layout.item_notification, parent, false);
        ViewHolder mHolder = new ViewHolder((feedView));
        return mHolder;
    }


    //bind the values based on the position of the element, called as a user scrolls down
    public void onBindViewHolder(ViewHolder holder, int position) {
        //get data according to position
        Notification mNotification= mNotifications.get(position);


        FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
        FirebaseUser mFbUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference mRef = mDatabase.getReference(); //need an instance of database reference
        DatabaseReference mRefForUser = mRef.child("users").child(mFbUser.getUid());

        //populate view according to this data
        holder.mOrgName.setText(mFbUser.getDisplayName());
        Log.d("name", "" + holder.mOrgName.getText());
//        holder.mDate.setText(mEvent.getDescription());
//        holder.mPartner.setText(mEvent.getRelativeTimeAgo());
//        holder.mOrgName.setText(mEvent.getRelativeTimeAgo());

//        ParseFile image = post.getImage();
//        if(image != null) {
//            String imageUrl = image.getUrl();
//            //Log.d("[debug]", "imageUrl = " + imageUrl);
//            Glide.with(context).load(imageUrl).into(holder.ivImage);
//            //show image view
//            holder.ivImage.isShown();
//
//        } else {
//            //hide image view
//            //holder.ivImage.set
//        }
    }

    @Override
    //must return the size; if it is 0, nothing will render on the screen
    public int getItemCount() {
        Log.d("adapter", "size " + mNotifications.size());
        return mNotifications.size();
    }

    //create ViewHolder pattern that will contain all the fine view by ID lookups
    //inner class
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public ImageView mRestaurantPic;
        public TextView mEventName;
        public TextView mDate;
        public TextView mPartner;
        public TextView mOrgName;

        //constructor takes in an inflated layout
        public ViewHolder(View itemView) {
            //call the superclass
            super(itemView);

            //perform findViewById lookups
            mRestaurantPic = itemView.findViewById(R.id.restaurant_iv);
            mEventName = itemView.findViewById(R.id.event_name_tv);
            mDate = itemView.findViewById(R.id.date_tv);
            mPartner = itemView.findViewById(R.id.partnered_with_tv);
            mOrgName = itemView.findViewById(R.id.org_name_tv);
            mRestaurantPic.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Notification mNotification = mNotifications.get(getAdapterPosition());
            //Intent intent = new Intent(context, HomeFragment.class);
//            intent.putExtra("details", post);
//            context.startActivity(intent);
        }
    }

    // Clean all elements of the recycler
    public void clear() {
        mNotifications.clear();
        notifyDataSetChanged();
    }

    // Add a list of items -- change to type used
    public void addAll(List<Notification> list) {
        mNotifications.addAll(list);
        notifyDataSetChanged();
    }
}
