package com.example.dine_and_donate.Adapters;

import android.content.Context;
import android.os.Build;
import android.transition.Fade;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.dine_and_donate.Activities.HomeActivity;
import com.example.dine_and_donate.HomeFragments.VoucherDetailFragment;
import com.example.dine_and_donate.Models.Event;
import com.example.dine_and_donate.R;
import com.example.dine_and_donate.Transitions.VoucherTransition;

import java.util.ArrayList;
import java.util.Random;

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
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        Log.d("tag", "onBindViewHolder: called");

        final Event event = mEvents.get(position);
        holder.event = event;

        //dummy image
        RequestOptions requestOptions = new RequestOptions()
                .placeholder(R.drawable.voucher_background);

        //populate recycler view for tab fragment
        Glide.with(mContext)
                .load(event.imageUrl)
                .centerCrop()
                .apply(requestOptions)
                .into(holder.image);

        holder.image.isShown();

        holder.name.setText(event.locationString.split("\\r?\\n")[0]);
        holder.image.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
                HomeActivity homeActivity = (HomeActivity) mContext;

                VoucherDetailFragment voucherFragment = new VoucherDetailFragment(event);

                voucherFragment.setSharedElementEnterTransition(new VoucherTransition());
                voucherFragment.setEnterTransition(new Fade());
                voucherFragment.setExitTransition(new Fade());
                voucherFragment.setSharedElementReturnTransition(new VoucherTransition());

                homeActivity.
                        getSupportFragmentManager().beginTransaction()
                        .replace(R.id.flContainer, voucherFragment)
                        .addSharedElement(holder.image, "voucher_image")
                        .addToBackStack(null)
                        .commit();
            }
        });
    }

    @Override
    //return number of images present held by the adapter
    public int getItemCount() {
        return mEvents.size();
    }

    //describes an item view inside a recycler view
    class ViewHolder extends RecyclerView.ViewHolder {
        CardView voucherView;
        Event event;
        ImageView image;
        TextView name;

        //constructor
        public ViewHolder(final View itemView) {
            super(itemView);
            this.image = itemView.findViewById(R.id.voucher_image);
            this.name = itemView.findViewById(R.id.name);
            this.voucherView = itemView.findViewById(R.id.voucher_cv);
        }
    }
}