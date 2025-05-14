package com.example.smishingdetectionapp.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.example.smishingdetectionapp.R
import com.example.smishingdetectionapp.SmishingTrendsActivity

class CaseStudiesActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_case_studies)

        // Back arrow in the top app bar
        supportActionBar?.title = "Case Studies"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Reference to cards
        val bankScamCard = findViewById<LinearLayout>(R.id.card_bank_scam)
        val packageScamCard = findViewById<LinearLayout>(R.id.card_package_scam)
        val taxScamCard = findViewById<LinearLayout>(R.id.card_tax_scam)

        // Load animation
        val cardAnim = AnimationUtils.loadAnimation(this, R.anim.fade_slide_up)

        // Start animation
        bankScamCard.startAnimation(cardAnim)
        packageScamCard.startAnimation(cardAnim)
        taxScamCard.startAnimation(cardAnim)

        // OnClick listeners for each card
        bankScamCard.setOnClickListener {
            openLink("https://www.news.com.au/finance/business/banking/melbourne-woman-loses-40k-house-deposit-after-sophisticated-scam-call/news-story/b7418b0eb10a8a6b64e8e2b61adff7df")
        }

        packageScamCard.setOnClickListener {
            openLink("https://au.finance.yahoo.com/news/warning-to-australia-post-customers-your-item-was-redirected-002117720.html")
        }

        taxScamCard.setOnClickListener {
            openLink("https://www.ato.gov.au/online-services/scams-cyber-safety-and-identity-protection/scam-alerts")
        }

        // Trends Button
        val trendsButton = findViewById<Button>(R.id.btn_trends)
        trendsButton.setOnClickListener {
            val intent = Intent(this, SmishingTrendsActivity::class.java)
            startActivity(intent)
        }
    }

    // 🔧 Move this outside onCreate!
    private fun openLink(url: String) {
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(browserIntent)
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}
