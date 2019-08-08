package com.example.dine_and_donate;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.example.dine_and_donate.Activities.HomeActivity;

import java.util.ArrayList;
import java.util.HashMap;

public class SearchDialogFragment extends DialogFragment {

    public static final String ORGS = "orgNames";
    public HomeActivity homeActivity;

    public static SearchDialogFragment newInstance(ArrayList<String> orgNames) {
        SearchDialogFragment frag = new SearchDialogFragment();
        Bundle args = new Bundle();
        args.putStringArrayList(ORGS, orgNames);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        ArrayList<String> orgNames = getArguments().getStringArrayList(ORGS);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.search, null);

        Button searchOrgsButton = view.findViewById(R.id.search_btn);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(),
                android.R.layout.simple_dropdown_item_1line, orgNames);
        final AutoCompleteTextView searchQuery = (AutoCompleteTextView)
                view.findViewById(R.id.autoCompleteSearchOrg);
        searchQuery.setAdapter(adapter);

        setCancelable(true);

        builder.setView(view);
        final Dialog dialog = builder.create();
        homeActivity = ((HomeActivity) getContext());

        searchOrgsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String query = searchQuery.getText().toString();
                searchQuery.setCursorVisible(false);
                dialog.dismiss();
                homeActivity.setExploreTab(query);
            }
        });

        return dialog;
    }
}