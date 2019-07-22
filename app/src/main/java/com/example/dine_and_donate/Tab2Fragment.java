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

public class Tab2Fragment extends Fragment {

    View v;
    private RecyclerView recyclerView;
    private ArrayList<String> mImageUrls = new ArrayList<>();
    private ArrayList<String> mNames = new ArrayList<>();

    //default constructor
    public Tab2Fragment() {
        mImageUrls = new ArrayList<>();
        mNames = new ArrayList<>();
    }

    //inflates layout of fragment
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.tab2_fragment, container, false);
        recyclerView = v.findViewById(R.id.rv_past_vouchers);
        StaggeredRecyclerViewAdapter staggeredRecyclerViewAdapter = new StaggeredRecyclerViewAdapter(this, mNames, mImageUrls);
        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(staggeredGridLayoutManager);
        recyclerView.setAdapter(staggeredRecyclerViewAdapter);
        return v;
    }

    //initial creation of fragment
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initBitmaps();
    }

    //add images and descriptions to respective arrayLists
    private void initBitmaps() {
        for(int i = 0; i < 20; i++) {
            mImageUrls.add("https://i.redd.it/tpsnoz5bzo501.jpg");
        }

        for(int i = 0; i < 10; i++) {
            mNames.add("udblrjtkbtecidtijijhbfitduvgdnvbtlujberlvuubtdbkfhdfihgudjbvnbbjhgejvdefhcturgucfnenhjdfffijrkiftbenjrjijhugvfujncndrftftglgelkc");
            mNames.add("dogs");
        }
    }
}