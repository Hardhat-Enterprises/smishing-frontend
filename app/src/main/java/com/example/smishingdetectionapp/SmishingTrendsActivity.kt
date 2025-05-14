package com.example.smishingdetectionapp.ui

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import com.example.smishingdetectionapp.R
import com.example.smishingdetectionapp.EducationActivity

class SmishingTrendsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_smishing_trends)

        // App bar setup (optional, not used if custom button exists)
        supportActionBar?.title = "Smishing Trends"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Custom back button logic
        val backButton = findViewById<ImageButton>(R.id.back_button_trends)
        backButton.setOnClickListener {
            val intent = Intent(this, CaseStudiesActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}
