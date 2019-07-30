package com.example.dine_and_donate;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;

//bind the data to the view
public class StaggeredRecyclerViewAdapter extends RecyclerView.Adapter<StaggeredRecyclerViewAdapter.ViewHolder> {

    private ArrayList<String> mDescriptions;
    private ArrayList<String> mImages;
    private Context mContext;

    //constructor; takes in context, list of strings, list of URLs
    public StaggeredRecyclerViewAdapter(Context context, ArrayList<String> descriptions, ArrayList<String> imageUrls) {
        this.mDescriptions = descriptions;
        this.mImages = imageUrls;
        mContext = context;
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

        //dummy image
        RequestOptions requestOptions = new RequestOptions()
                .placeholder(R.drawable.ic_launcher_background);

        //pouplate recycler view for tab fragment
        Glide.with(mContext)
                .load(mImages.get(position))
                .apply(requestOptions)
                .into(holder.image);
        holder.name.setText(mDescriptions.get(position));
    }

    @Override
    //return number of images present held by the adapter
    public int getItemCount() {
        return mImages.size();
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