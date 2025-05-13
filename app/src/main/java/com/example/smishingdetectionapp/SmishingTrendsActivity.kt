package com.example.smishingdetectionapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.smishingdetectionapp.R

class SmishingTrendsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_smishing_trends)

        // App bar title and back button
        supportActionBar?.title = "Smishing Trends"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}
