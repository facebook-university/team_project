package com.example.dine_and_donate.Adapters;

import android.content.Context;
import android.text.Html;
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
import com.example.dine_and_donate.Activities.HomeActivity;
import com.example.dine_and_donate.Models.Event;
import com.example.dine_and_donate.Models.User;
import com.example.dine_and_donate.R;
import com.google.firebase.database.DataSnapshot;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;


public class EventListViewAdapter extends RecyclerView.Adapter<EventListViewAdapter.ViewHolder> {

    static Context context;
    private ArrayList<Event> mEvents = new ArrayList<>();
    private HashMap<String, JSONObject> mIdToRestaurant;
    private HashMap<String, User> mIdToOrg;

    public EventListViewAdapter(DataSnapshot allEvents, HashMap<String, JSONObject> mIdToRestaurant, HashMap<String, User> mIdToOrg, String id) {
        for (DataSnapshot ds : allEvents.getChildren()) {
            mEvents.addAll(Event.eventsHappeningAtRestaurant(ds, id));
        }
        this.mIdToRestaurant = mIdToRestaurant;
        this.mIdToOrg = mIdToOrg;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        final View view = inflater.inflate(R.layout.layout_event_list_view, parent, false);
        final ViewHolder viewHolder = new ViewHolder(view);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // todo: get location from event, pass to home activity
                HomeActivity homeActivity = (HomeActivity) context;
                homeActivity.setClickedOnID(viewHolder.event.yelpID);
                homeActivity.setExploreTab(null);
                homeActivity.setLoading(true);
            }
        });
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Event event = mEvents.get(position);
        holder.event = event;
        User org = mIdToOrg.get(event.orgId);
        JSONObject restaurant = mIdToRestaurant.get(event.yelpID);
        //Todo: create / find better default
        String profilePicUrl = org.getImageUrl() != null && !org.getImageUrl().equals("") ? org.imageUrl : "https://cdn2.iconfinder.com/data/icons/wedding-glyph-1/128/44-512.png";
        try {
            String orgName = org.name;
            String restaurantName = restaurant.getString("name");
            String htmlText = "<b>" + orgName + "</b>" + " is having a Dine and Donate event at " + "<b>" + restaurantName + "</b>";
            holder.tvEventText.setText(Html.fromHtml(htmlText));
            holder.tvDateTime.setText(dateFromMills(event.startTime));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Glide.with(context)
                .load(profilePicUrl)
                .transform(new MultiTransformation<>(new CenterCrop(), new CircleCrop()))
                .into(holder.ivOrgPic);
    }

    @Override
    public int getItemCount() {
        return mEvents.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvEventText;
        TextView tvDateTime;
        ImageView ivOrgPic;
        Event event;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivOrgPic = itemView.findViewById(R.id.ivOrgImage);
            tvEventText = itemView.findViewById(R.id.tvEventInfo);
            tvDateTime = itemView.findViewById(R.id.tvDateTime);
        }
    }

    private String dateFromMills(long millis) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(millis);

        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int hour = calendar.get(Calendar.HOUR);
        int minute = calendar.get(Calendar.MINUTE);
        String min = minute < 10 ? "0" + minute : "" + minute;
        String am = calendar.get(Calendar.AM_PM) == Calendar.AM ? "AM" : "PM";
        return month + "/" + day + " - " + hour + ":" + min + " " + am;
    }
}
