package com.example.dine_and_donate.HomeFragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.dine_and_donate.Activities.EventActivity;
import com.example.dine_and_donate.Activities.HomeActivity;
import com.example.dine_and_donate.Models.Event;
import com.example.dine_and_donate.Models.User;
import com.example.dine_and_donate.R;

import org.parceler.Parcels;

public class VoucherDetailFragment extends Fragment {

    private ImageView ivVoucher;
    private ImageView ivShare;
    private ImageView ivEdit;
    private Event mEvent;
    private Context mContext;
    private User mCurrentUserModel;

    public VoucherDetailFragment(Event event) {
        mEvent = event;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_voucher_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        HomeActivity homeActivity = (HomeActivity) getActivity();
        mCurrentUserModel = homeActivity.currentUser;

        mContext = view.getContext();
        ivVoucher = view.findViewById(R.id.voucher_image);
        ivShare = view.findViewById(R.id.ivShare);
        ivEdit = view.findViewById(R.id.ivEdit);

        if (!homeActivity.currentUser.isOrg) {
            ivEdit.setVisibility(View.GONE);
        } else {
            ivEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, EventActivity.class);
                    intent.putExtra(User.class.getSimpleName(), Parcels.wrap(mCurrentUserModel));
                    intent.putExtra(Event.class.getSimpleName(), Parcels.wrap(mEvent));
                    intent.putExtra("yelpId", mEvent.yelpID);
                    intent.putExtra("location", mEvent.locationString);
                    startActivity(intent);
                }
            });
        }

        Glide.with(view.getContext())
                // Todo: change this to real image, need to add imageUrl field in edit profile
                .load(mEvent.imageUrl)
                .into(ivVoucher);
    }
}
