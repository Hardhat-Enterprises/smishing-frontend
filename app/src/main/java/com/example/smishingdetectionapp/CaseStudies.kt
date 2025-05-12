package com.example.smishingdetectionapp.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.example.smishingdetectionapp.R

class CaseStudiesActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_case_studies)

        // Back arrow in the top app bar
        supportActionBar?.title = "Case Studies"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Bank Scam Case
        val bankScamCard = findViewById<LinearLayout>(R.id.card_bank_scam)
        bankScamCard.setOnClickListener {
            openLink("https://www.news.com.au/finance/business/banking/melbourne-woman-loses-40k-house-deposit-after-sophisticated-scam-call/news-story/b7418b0eb10a8a6b64e8e2b61adff7df")
        }

        // Package Scam Case
        val packageScamCard = findViewById<LinearLayout>(R.id.card_package_scam)
        packageScamCard.setOnClickListener {
            openLink("https://au.finance.yahoo.com/news/warning-to-australia-post-customers-your-item-was-redirected-002117720.html")
        }

        // Tax Scam Case
        val taxScamCard = findViewById<LinearLayout>(R.id.card_tax_scam)
        taxScamCard.setOnClickListener {
            openLink("https://www.ato.gov.au/online-services/scams-cyber-safety-and-identity-protection/scam-alerts")
        }

        // Trends Button
        val trendsButton = findViewById<Button>(R.id.btn_trends)
        trendsButton.setOnClickListener {
            // TODO: Navigate to SmishingTrendsActivity
            // startActivity(Intent(this, SmishingTrendsActivity::class.java))
        }
    }

    private fun openLink(url: String) {
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(browserIntent)
    }

    // Handle back arrow
    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}
