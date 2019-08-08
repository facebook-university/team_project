package com.example.dine_and_donate.Adapters;

import android.content.Context;
import android.location.Location;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.dine_and_donate.Activities.HomeActivity;
import com.example.dine_and_donate.HomeFragments.MapFragment;
import com.example.dine_and_donate.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class RestaurantListViewAdapter extends RecyclerView.Adapter<RestaurantListViewAdapter.ViewHolder> {

    public static JSONArray mRestaurants;
    private Location mCurrentLocation;
    static Context context;

    public RestaurantListViewAdapter(JSONArray array, Location location) {
        mRestaurants = array;
        mCurrentLocation = location;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(R.layout.layout_restaurant_list_view, parent, false);
        final ViewHolder viewHolder = new ViewHolder(view);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // todo: get location from event, pass to home activity
                HomeActivity homeActivity = (HomeActivity) context;
                homeActivity.setClickedOnID(viewHolder.yelpId);
                homeActivity.setExploreTab();
                homeActivity.setLoading(true);
            }
        });
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        try {
            JSONObject restaurant = mRestaurants.getJSONObject(position);
            holder.yelpId = restaurant.getString("id");
            holder.tvName.setText(restaurant.getString("name"));
            holder.ratingBar.setRating((float) Double.parseDouble(restaurant.getString("rating")));
            JSONObject coordinates = restaurant.getJSONObject("coordinates");
            String restLatitude = coordinates.getString("latitude");
            String restLongitude = coordinates.getString("longitude");

            holder.tvDistance.setText(MapFragment.distance(mCurrentLocation.getLatitude(),
                    mCurrentLocation.getLongitude(),
                    Double.parseDouble(restLatitude),
                    Double.parseDouble(restLongitude)) + " miles away");

            holder.tvInfo.setText(restaurant.getJSONArray("categories").getJSONObject(0).getString("title"));
            Glide.with(context)
                    .load(restaurant.getString("image_url"))
                    .into(holder.ivPicture);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() { return mRestaurants.length(); }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView tvName;
        public TextView tvDistance;
        public ImageView ivPicture;
        public TextView tvInfo;
        public RatingBar ratingBar;
        public String yelpId;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvRestaurantName);
            tvDistance = itemView.findViewById(R.id.tvDistance);
            tvInfo = itemView.findViewById(R.id.tvDescription);
            ivPicture = itemView.findViewById(R.id.ivRestaurant);
            ratingBar = itemView.findViewById(R.id.ratingBar);
        }
    }
}
