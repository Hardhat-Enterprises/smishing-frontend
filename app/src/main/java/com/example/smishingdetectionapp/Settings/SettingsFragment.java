package com.example.smishingdetectionapp.Settings;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.smishingdetectionapp.R;
import com.example.smishingdetectionapp.SettingsActivity;


public class SettingsFragment extends Fragment {

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Intent intent = new Intent(getActivity(), SettingsActivity.class);
        startActivity(intent);

        return inflater.inflate(R.layout.fragment_settings, container, false);


    }
}