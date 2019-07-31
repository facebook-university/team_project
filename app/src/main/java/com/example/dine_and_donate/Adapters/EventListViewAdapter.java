package com.example.dine_and_donate.Adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.example.dine_and_donate.Models.Event;
import com.example.dine_and_donate.Models.User;
import com.example.dine_and_donate.R;
import com.google.firebase.database.DataSnapshot;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;


public class EventListViewAdapter extends RecyclerView.Adapter<EventListViewAdapter.ViewHolder> {

    static Context context;
    private ArrayList<Event> mEvents = new ArrayList<>();
    private HashMap<String, JSONObject> mIdToRestaurant;
    private HashMap<String, User> mIdToOrg;

    public EventListViewAdapter(DataSnapshot allEvents, HashMap<String, JSONObject> mIdToRestaurant, HashMap<String, User> mIdToOrg) {
        for(DataSnapshot ds : allEvents.getChildren()) {
            mEvents.addAll(Event.eventsHappeningAtRestaurant(ds));
        }
        this.mIdToRestaurant = mIdToRestaurant;
        this.mIdToOrg = mIdToOrg;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(R.layout.layout_event_list_view, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Event event = mEvents.get(position);
        holder.tvOrgName.setText(mIdToOrg.get(event.orgId).name);
        holder.tvOrgInfo.setText(event.info);
        try {
            holder.tvRestaurantName.setText(mIdToRestaurant.get(event.yelpID).getString("name"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Glide.with(context)
                // Todo: change this to real image, need to add imageUrl field in edit profile
                .load("https://firebasestorage.googleapis.com/v0/b/dine-and-donate.appspot.com/o/images%2F158765210?alt=media&token=be40174f-ed03-4299-8431-410b036a9037")
                .transform(new MultiTransformation<>(new CenterCrop(), new CircleCrop()))
                .into(holder.ivOrgPic);
    }

    @Override
    public int getItemCount() { return mEvents.size(); }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvRestaurantName;
        TextView tvOrgName;
        TextView tvOrgInfo;
        ImageView ivOrgPic;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvRestaurantName = itemView.findViewById(R.id.tvRestaurantName);
            ivOrgPic = itemView.findViewById(R.id.ivOrgImage);
            tvOrgName = itemView.findViewById(R.id.tvOrgName);
            tvOrgInfo = itemView.findViewById(R.id.tvEventInfo);
        }
    }

}
