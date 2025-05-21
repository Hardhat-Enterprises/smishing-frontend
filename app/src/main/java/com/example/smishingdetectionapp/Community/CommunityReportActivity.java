    package com.example.smishingdetectionapp.Community;

    import android.content.Intent;
    import android.os.Bundle;
    import android.util.Log;
    import android.widget.Button;
    import android.widget.EditText;
    import android.widget.ImageButton;
    import android.widget.Toast;

    import androidx.appcompat.app.AppCompatActivity;

    import com.example.smishingdetectionapp.NewsActivity;
    import com.example.smishingdetectionapp.R;
    import com.google.android.material.bottomnavigation.BottomNavigationView;
    import com.google.android.material.tabs.TabLayout;

    import com.example.smishingdetectionapp.MainActivity;
    import com.example.smishingdetectionapp.SettingsActivity;

    public class CommunityReportActivity extends AppCompatActivity {
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_community_report);

            final String source =
                    (getIntent().getStringExtra("source") == null
                            ? "home"
                            : getIntent().getStringExtra("source"));


            // TabLayout: add 3 tabs and select “Report”
            TabLayout tabLayout = findViewById(R.id.tabLayout);
            tabLayout.addTab(tabLayout.newTab().setText("Trending"));
            tabLayout.addTab(tabLayout.newTab().setText("Posts"));
            tabLayout.addTab(tabLayout.newTab().setText("Report"));
            tabLayout.getTabAt(2).select();

            tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                @Override public void onTabSelected(TabLayout.Tab tab) {
                    int pos = tab.getPosition();
                    if (pos == 0) {
                        // go back to trending
                        Intent i = new Intent(CommunityReportActivity.this, CommunityHomeActivity.class);
                        i.putExtra("source", source);
                        startActivity(i);
                        overridePendingTransition(0,0);
                        finish();
                    } else if (pos == 1) {
                        // go to posts
                        Intent i = new Intent(new Intent(CommunityReportActivity.this, CommunityPostActivity.class));
                        i.putExtra("source", source);
                        startActivity(i);
                        overridePendingTransition(0,0);
                        finish();
                    }
                    // pos == 2 → stay on report
                }
                @Override public void onTabUnselected(TabLayout.Tab tab) { }
                @Override public void onTabReselected(TabLayout.Tab tab) { }
            });

            ImageButton community_back = findViewById(R.id.community_back);
            if (community_back != null) {
                community_back.setOnClickListener(view ->  {
                    if ("settings".equals(source)) {
                        startActivity((new Intent(this, SettingsActivity.class)));
                        overridePendingTransition(0,0);
                        finish();
                    } else {
                        // Return to Home
                        startActivity(new Intent(this, MainActivity.class));
                        overridePendingTransition(0, 0);
                        finish();
                    }
                });
            } else {
                Log.e("CommunityReportActivity", "Back button is null");
            }


            // BottomNavigationView: identical to CommunityHomeActivity’s
            BottomNavigationView nav = findViewById(R.id.bottom_navigation);

            nav.setSelectedItemId(R.id.nav_report);

            nav.setOnItemSelectedListener(item -> {
                Intent intent;
                int id = item.getItemId();
                if      (id == R.id.nav_home)     intent = new Intent(this, MainActivity.class);
                else if (id == R.id.nav_news)     intent = new Intent(this, NewsActivity.class);
                else if (id == R.id.nav_settings) intent = new Intent(this, SettingsActivity.class);
                else return false;
                startActivity(intent);
                overridePendingTransition(0,0);
                finish();
                return true;
            });

            // Form logic: validate & toast
            EditText etPhone   = findViewById(R.id.etPhoneNumber);
            EditText etMessage = findViewById(R.id.etMessageContent);
            Button btnReport   = findViewById(R.id.btnReportProtect);

            btnReport.setOnClickListener(v -> {
                String phone = etPhone.getText().toString().trim();
                String msg   = etMessage.getText().toString().trim();
                if (phone.isEmpty() || msg.isEmpty()) {
                    Toast.makeText(this, "Please fill out both fields", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Report submitted. Thank you!", Toast.LENGTH_LONG).show();
                    // TODO: send to backend
                }
            });
        }
    }