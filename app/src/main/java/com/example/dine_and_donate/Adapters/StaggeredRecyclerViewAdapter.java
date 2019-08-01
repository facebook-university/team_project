package com.example.dine_and_donate.Adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.dine_and_donate.Models.Event;
import com.example.dine_and_donate.R;

import java.util.ArrayList;

//bind the data to the view
public class StaggeredRecyclerViewAdapter extends RecyclerView.Adapter<StaggeredRecyclerViewAdapter.ViewHolder> {

    private ArrayList<Event> mEvents;
    private Context mContext;

    //constructor; takes in context, list of strings, list of URLs
    public StaggeredRecyclerViewAdapter(Context context, ArrayList<Event> events) {
        mContext = context;
        mEvents = events;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_grid_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    //assign everything to the widgets
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        Log.d("tag", "onBindViewHolder: called");

        Event event = mEvents.get(position);

        //dummy image
        RequestOptions requestOptions = new RequestOptions()
                .placeholder(R.drawable.ic_launcher_background);

        //populate recycler view for tab fragment
        Glide.with(mContext)
                .load(event.imageUrl)
                .apply(requestOptions)
                .into(holder.image);
        holder.name.setText(event.locationString.split("\\r?\\n")[0]);

        holder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext, "Clicked voucher", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    //return number of images present held by the adapter
    public int getItemCount() {
        return mEvents.size();
    }

    //describes an item view inside a recycler view
    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView name;

        //constructor
        public ViewHolder(View itemView) {
            super(itemView);
            this.image = itemView.findViewById(R.id.voucher_image);
            this.name = itemView.findViewById(R.id.name);
        }
    }
}