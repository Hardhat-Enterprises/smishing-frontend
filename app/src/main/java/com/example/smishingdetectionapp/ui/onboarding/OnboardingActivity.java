package com.example.smishingdetectionapp.ui.onboarding;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.example.smishingdetectionapp.R;
import com.example.smishingdetectionapp.ui.locale.LocaleHelper;
import com.localazy.android.Localazy;
import com.localazy.android.LocalazyLocale;
import com.tbuonomo.viewpagerdotsindicator.DotsIndicator;

import java.util.ArrayList;
import java.util.List;

public class OnboardingActivity extends AppCompatActivity{
    private LocaleHelper localeHelper;// Declare LocaleHelper
    private ViewPager2 viewPager;
    private Button skipButton;
    private Button nextButton;
    private OnBoardingSliderAdapter adapter;
    private DotsIndicator dotsIndicator; // Added this line

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding);

        // add language name 'En' dynamically to button text
        LocalazyLocale currentLLocale = Localazy.getCurrentLocalazyLocale();
        String currentLocaleLocalizedName = "English"; // fallback default
        if (currentLLocale != null && currentLLocale.getLocalizedName() != null) {
            currentLocaleLocalizedName = currentLLocale.getLocalizedName();
        }else{
            Log.d("error", "onCreate: localazy is not correctly. search this log text");
        }

        Button button = findViewById(R.id.btn_change_language);
        button.setText(currentLocaleLocalizedName.length() >= 2
                ? currentLocaleLocalizedName.substring(0, 2)
                : currentLocaleLocalizedName);

        // Initialize local helper
        localeHelper = new LocaleHelper(this);

        button.setOnClickListener(v -> {
            // View local changer
            localeHelper.showLanguageActivity();
        });

        viewPager = findViewById(R.id.viewPager);
        skipButton = findViewById(R.id.skipButton);
        nextButton = findViewById(R.id.nextButton);
        dotsIndicator = findViewById(R.id.dotsIndicator); // Initialize DotsIndicator

        List<OnBoardingSlide> slides = new ArrayList<>();
        // slides.add(new OnBoardingSlide(R.drawable.onboarding_screen_1, "Intelligent Scam Detection System", "Stay alert! Instantly identify and block suspicious messages before they reach you.."));
        slides.add(new OnBoardingSlide(R.drawable.onboarding_screen_1, getString(R.string.onboard_slide_1_title), getString(R.string.onboard_slide_1_desc)));
        // slides.add(new OnBoardingSlide(R.drawable.onboarding_screen_2, "Real-Time Cyber New Alerts", "Get the latest updates on scams, breaches, and cybersecurity trends—right in your pocket."));
        slides.add(new OnBoardingSlide(R.drawable.onboarding_screen_2, getString(R.string.onboard_slide_2_title), getString(R.string.onboard_slide_2_desc)));
        // slides.add(new OnBoardingSlide(R.drawable.onboarding_screen_3, "Safe browsing", "Protects you from malicious links and attachments. Adjust the sensitivity and criteria for spam detection."));
        slides.add(new OnBoardingSlide(R.drawable.onboarding_screen_3, getString(R.string.onboard_slide_3_title), getString(R.string.onboard_slide_3_desc)));

        adapter = new OnBoardingSliderAdapter(this, slides);
        viewPager.setAdapter(adapter);
        dotsIndicator.setViewPager2(viewPager); // Connect ViewPager2 with DotsIndicator

        skipButton.setOnClickListener(v -> finishIntroSlider());

        nextButton.setOnClickListener(v -> {
            if (viewPager.getCurrentItem() + 1 < adapter.getItemCount()) {
                viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
            } else {
                finishIntroSlider();
            }
        });

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                if (position == adapter.getItemCount() - 1) {
                    nextButton.setText(getString(R.string.lets_get_started));
                } else {
                    nextButton.setText(getString(R.string.next));
                }
            }
        });
    }

   private void finishIntroSlider() {
        // Start main app activity
        startActivity(new Intent(this, LoginCreateActivity.class));
        finish();
    }

}
