package com.example.dine_and_donate.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.dine_and_donate.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class RestaurantListViewAdapter extends RecyclerView.Adapter<RestaurantListViewAdapter.ViewHolder> {

    public static JSONArray mRestaurants;
    static Context context;

    public RestaurantListViewAdapter(JSONArray array) { mRestaurants = array; }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(R.layout.layout_restaurant_list_view, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        try {
            JSONObject restaurant = mRestaurants.getJSONObject(position);
            holder.tvName.setText(restaurant.getString("name"));
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
        public TextView tvInfo;
        public ImageView ivPicture;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvName = itemView.findViewById(R.id.tvName);
            tvInfo = itemView.findViewById(R.id.tvInfo);
            ivPicture = itemView.findViewById(R.id.ivRestaurantPhoto);
        }
    }
}
