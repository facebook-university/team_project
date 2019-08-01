package com.example.dine_and_donate.HomeFragments;

import android.media.Image;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.example.dine_and_donate.Activities.HomeActivity;
import com.example.dine_and_donate.R;

public class VoucherDetailFragment extends Fragment {

    ImageView ivVoucher;
    ImageView ivShare;
    ImageView ivEdit;
    String ImageUrl;

    public VoucherDetailFragment(String imageUrl) {
        ImageUrl = imageUrl;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_voucher_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ivVoucher = view.findViewById(R.id.voucher_image);
        ivShare = view.findViewById(R.id.ivShare);
        ivEdit = view.findViewById(R.id.ivEdit);

        HomeActivity homeActivity = (HomeActivity) getActivity();
        if(!homeActivity.currentUser.isOrg) {
            ivEdit.setVisibility(View.GONE);
        }

        Glide.with(view.getContext())
                // Todo: change this to real image, need to add imageUrl field in edit profile
                .load(ImageUrl)
                .into(ivVoucher);

    }
}
