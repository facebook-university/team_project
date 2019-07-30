package com.example.dine_and_donate.HomeFragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dine_and_donate.Models.Notification;
import com.example.dine_and_donate.NotificationsAdapter;
import com.example.dine_and_donate.R;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class NotificationsFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private NotificationsAdapter mNotificationsAdapter;
    private List<Notification> mNotificationList;

    private FirebaseUser mFbUser;
    private DatabaseReference mRef;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_notifications, container, false);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mRecyclerView = view.findViewById(R.id.notifications_rv);
        mNotificationList = new ArrayList<>();
        //mRecyclerView.setHasFixedSize(true);
        mNotificationsAdapter = new NotificationsAdapter(mNotificationList);
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mRecyclerView.setAdapter(mNotificationsAdapter);
        mNotificationList = new ArrayList<>();
        mNotificationsAdapter = new NotificationsAdapter(mNotificationList);

        mNotificationList.clear();
        Collections.reverse(mNotificationList);
        mNotificationsAdapter.notifyDataSetChanged();
    }
}