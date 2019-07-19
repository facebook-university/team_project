package com.example.dine_and_donate;

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


    private ArrayList<String> mNames = new ArrayList<>();
    private ArrayList<String> mImageUrls = new ArrayList<>();
    private ArrayList<String> mNamesTwo = new ArrayList<>();
    private ArrayList<String> mImageUrlsTwo = new ArrayList<>();
    private Tab1Fragment tab1;
    private Tab2Fragment tab2;
    private boolean isTabOne;


    //constructor with Tab1Fragment
    public StaggeredRecyclerViewAdapter(Tab1Fragment tab1, ArrayList<String> mNames, ArrayList<String> mImageUrls) {
        this.mNames = mNames;
        this.mImageUrls = mImageUrls;
        this.tab1 = tab1;
        isTabOne = true;
    }

    //constructor with Tab2Fragment
    public StaggeredRecyclerViewAdapter(Tab2Fragment tab2, ArrayList<String> mNamesTwo, ArrayList<String> mImageUrlsTwo) {
        this.mNamesTwo = mNamesTwo;
        this.mImageUrlsTwo = mImageUrlsTwo;
        this.tab2 = tab2;
        isTabOne = false;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_grid_item, parent,false);
        ViewHolder holder = new ViewHolder(view);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        Log.d("tag", "onBindViewHolder: called");

        RequestOptions requestOptions = new RequestOptions()
            .placeholder(R.drawable.ic_launcher_background);

        if(isTabOne) {
            //populate recycler view for first tab fragment
            Glide.with(tab1)
                    .load(mImageUrls.get(position))
                    .apply(requestOptions)
                    .into(holder.image);
            holder.name.setText(mNames.get(position));
        } else {
            //populate recycler view for second tab fragment
            Glide.with(tab2)
                .load(mImageUrlsTwo.get(position))
                .apply(requestOptions)
                .into(holder.image);
            holder.name.setText(mNamesTwo.get(position));
        }
    }

    @Override
    //return number of images present held by the adapter
    public int getItemCount() {
        if(isTabOne) {
            return mImageUrls.size();
        }
        return mImageUrlsTwo.size();
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