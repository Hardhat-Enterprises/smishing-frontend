package com.example.smishingdetectionapp.news;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.example.smishingdetectionapp.R;
import com.example.smishingdetectionapp.NewsActivity;

public class NewsFragment extends Fragment {


    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        Intent intent = new Intent(getActivity(), NewsActivity.class);
        startActivity(intent);
        return inflater.inflate(R.layout.fragment_news, container, false);

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}