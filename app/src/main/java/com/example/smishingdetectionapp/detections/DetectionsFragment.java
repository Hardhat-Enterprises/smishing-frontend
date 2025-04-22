package com.example.smishingdetectionapp.detections;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.smishingdetectionapp.R;


public class DetectionsFragment extends Fragment {



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Intent intent = new Intent(getActivity(), DetectionsActivity.class);
        startActivity(intent);

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_detections, container, false);

    }
}