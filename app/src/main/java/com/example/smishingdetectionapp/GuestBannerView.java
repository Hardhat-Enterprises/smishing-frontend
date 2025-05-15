package com.example.smishingdetectionapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.example.smishingdetectionapp.ui.login.LoginActivity;

public class GuestBannerView extends LinearLayout {

    public GuestBannerView(Context context) {
        super(context);
        init(context);
    }

    public GuestBannerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.view_guest_banner, this, true);
        setOrientation(VERTICAL);

        SharedPreferences prefs = context.getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
        boolean isGuest = prefs.getBoolean("isGuest", false);

        if (isGuest) {
            setVisibility(VISIBLE);

            TextView guestText = findViewById(R.id.guest_message);
            if (guestText != null) {
                String message = "You are in Guest Mode – Sign in for full access";
                SpannableString spannable = new SpannableString(message);

                int start = message.indexOf("Sign in");
                int end = start + "Sign in".length();

                ClickableSpan signInSpan = new ClickableSpan() {
                    @Override
                    public void onClick(View widget) {
                        Intent intent = new Intent(context, LoginActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        context.startActivity(intent);
                    }

                    @Override
                    public void updateDrawState(TextPaint ds) {
                        super.updateDrawState(ds);
                        ds.setColor(ContextCompat.getColor(context, R.color.navy_blue));
                        ds.setUnderlineText(true);
                        ds.setFakeBoldText(true);
                    }
                };

                spannable.setSpan(signInSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                guestText.setText(spannable);
                guestText.setMovementMethod(LinkMovementMethod.getInstance());
                guestText.setHighlightColor(Color.TRANSPARENT);
            }
        } else {
            setVisibility(GONE);
        }
    }
}
