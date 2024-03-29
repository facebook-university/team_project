package com.example.dine_and_donate.HomeFragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.dine_and_donate.Activities.HomeActivity;
import com.example.dine_and_donate.Adapters.NotificationsAdapter;
import com.example.dine_and_donate.Models.Notification;
import com.example.dine_and_donate.Models.User;
import com.example.dine_and_donate.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class NotificationsFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private NotificationsAdapter mNotificationsAdapter;
    private List<Notification> mNotificationList;
    private FirebaseUser mFbUser;
    private DatabaseReference mRef;
    private DatabaseReference mNotificationsRef;
    private FirebaseDatabase mDatabase;

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
        HomeActivity homeActivity = (HomeActivity) getContext();
        mNotificationsAdapter = new NotificationsAdapter(mNotificationList, homeActivity.getIdToOrg());
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mRecyclerView.setAdapter(mNotificationsAdapter);

        loadTopNotifications(0);
    }

    private void loadTopNotifications(int page) {
        mDatabase = FirebaseDatabase.getInstance();
        mFbUser = FirebaseAuth.getInstance().getCurrentUser();
        mRef = mDatabase.getReference();

        mNotificationsRef = mRef.child("users").child(mFbUser.getUid()).child("Notifications");
        mNotificationsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //look through all notifications of that user
                for (DataSnapshot notificationsDs : dataSnapshot.getChildren()) {
                    Notification notification = notificationsDs.getValue(Notification.class);
                    mNotificationList.add(0, notification);
                    mNotificationsAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}