package com.example.dine_and_donate;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import java.util.ArrayList;

public class Tab1Fragment extends Fragment {


    View v;
    private RecyclerView recyclerView;
    private ArrayList<String> mImageUrls = new ArrayList<>();
    private ArrayList<String> mNames = new ArrayList<>();



    public Tab1Fragment() {

    }

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        v = inflater.inflate(R.layout.tab1_fragment, container, false);
        recyclerView = v.findViewById(R.id.rv_vouchers);
        StaggeredRecyclerViewAdapter staggeredRecyclerViewAdapter = new StaggeredRecyclerViewAdapter(this, mNames, mImageUrls);
        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(staggeredGridLayoutManager);
        recyclerView.setAdapter(staggeredRecyclerViewAdapter);
        return v;
    }

    public void setRecyclerView(RecyclerView rv) {

    }


    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mImageUrls = new ArrayList<>();
        mNames = new ArrayList<>();
        initBitmaps();

    }

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