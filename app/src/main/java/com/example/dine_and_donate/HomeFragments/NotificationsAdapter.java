package com.example.dine_and_donate.HomeFragments;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.dine_and_donate.Models.Notifications;
import com.example.dine_and_donate.R;

import java.util.List;

public class NotificationsAdapter extends RecyclerView.Adapter<NotificationsAdapter.ViewHolder> {

    private List<Notifications> mNotifications;
    private Context mContext;

    //pass in posts array in the constructor
    public NotificationsAdapter(List<Notifications> notifications) {
        mNotifications = notifications;
    }

    //for each row, inflate the layout and cache references into ViewHolder (pass them into ViewHolder class)
    //only invoked when new row needs to be created
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(mContext);

        View feedView = inflater.inflate(R.layout.item_notification, parent, false);
        ViewHolder holder = new ViewHolder((feedView));
        return holder;
    }

    //bind the values based on the position of the element, called as a user scrolls down
    public void onBindViewHolder(ViewHolder holder, int position) {
        //get data according to position
        Notifications notification= mNotifications.get(position);

        //populate view according to this data
//        holder.tvUsername.setText(post.getUser().getUsername());
//        holder.tvBody.setText(post.getDescription());
//        holder.date.setText(post.getRelativeTimeAgo());

//        ParseFile image = post.getImage();
//        if(image != null) {
//            String imageUrl = image.getUrl();
//            //Log.d("[debug]", "imageUrl = " + imageUrl);
//            Glide.with(context).load(imageUrl).into(holder.ivImage);
//            //show image view
//            holder.ivImage.isShown(); } else {
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
        public ImageView mOrgPic;
        public TextView mEventName;
        public TextView mStartDate;
        public TextView mPartner;
        public TextView mOrgName;

        //constructor takes in an inflated layout
        public ViewHolder(View itemView) {
            //call the superclass
            super(itemView);

            //perform findViewById lookups
            mOrgPic = (ImageView) itemView.findViewById(R.id.org_iv);
            mEventName = (TextView) itemView.findViewById(R.id.event_name_tv);
            mStartDate = (TextView) itemView.findViewById(R.id.date_tv);
            mPartner = (TextView) itemView.findViewById(R.id.partnered_with_tv);
            mOrgName = (TextView) itemView.findViewById(R.id.org_name_tv);
        }

        @Override
        //in case we want to make the notification clickable
        public void onClick(View v) {

        }
    }

    // Clean all elements of the recycler
    public void clear() {
        mNotifications.clear();
        notifyDataSetChanged();
    }

    // Add a list of items -- change to type used
    public void addAll(List<Notifications> list) {
        mNotifications.addAll(list);
        notifyDataSetChanged();
    }
}