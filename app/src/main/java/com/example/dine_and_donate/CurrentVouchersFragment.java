package com.example.dine_and_donate;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import java.util.ArrayList;

public class CurrentVouchersFragment extends Fragment {

    private RecyclerView recyclerView;
    private ArrayList<String> mImageUrls = new ArrayList<>();
    private ArrayList<String> mNames = new ArrayList<>();

    //create view based on data in array lists, inflates the layout of the fragment
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.tab_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initBitmaps();
        recyclerView = view.findViewById(R.id.rv_vouchers);
        StaggeredRecyclerViewAdapter staggeredRecyclerViewAdapter = new StaggeredRecyclerViewAdapter(getActivity(), mNames, mImageUrls);
        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(staggeredGridLayoutManager);
        recyclerView.setAdapter(staggeredRecyclerViewAdapter);
    }

    //add images and descriptions to arrayLists
    private void initBitmaps() {
        for(int i = 0; i < 20; i++) {
            mImageUrls.add("https://i.redd.it/tpsnoz5bzo501.jpg");
        }

        for(int i = 0; i < 10; i++) {
            mNames.add("Tree");
            mNames.add("fjcutcnerdfdluvhbuegnecgvlkclbidjvnlvfubjrbeugtfdrtnikledvtbhguvuhrtjcvcfguekrfrihjfehbjllfdutbg");
        }
    }
}