package com.example.smishingdetectionapp.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.smishingdetectionapp.QuizesActivity;
import com.example.smishingdetectionapp.R;

public class EducationFragment extends Fragment {

    public EducationFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_education, container, false); // Keep your layout name here

        // Back button functionality
        ImageButton backButton = view.findViewById(R.id.education_back);
        backButton.setOnClickListener(v -> requireActivity().onBackPressed());

        // Setup WebView
        WebView youtubeWebView = view.findViewById(R.id.youtubeWebView);
        WebSettings webSettings = youtubeWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        youtubeWebView.setWebViewClient(new WebViewClient());
        youtubeWebView.loadUrl("https://www.youtube.com/embed/ZOZGQeG8avQ");

        // Quiz button functionality
        Button quizButton = view.findViewById(R.id.quiz_button);
        quizButton.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), QuizesActivity.class);
            startActivity(intent);
        });

        return view;
    }
}
