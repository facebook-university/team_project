package com.example.dine_and_donate.HomeFragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.dine_and_donate.Activities.HomeActivity;
import com.example.dine_and_donate.R;

public class VoucherDetailFragment extends Fragment {

    ImageView ivVoucher;
    ImageView ivShare;
    ImageView ivEdit;
    String imageUrl;

    public VoucherDetailFragment(String imageUrl) {
        this.imageUrl = imageUrl;
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
                .load(imageUrl)
                .into(ivVoucher);

    }
}
