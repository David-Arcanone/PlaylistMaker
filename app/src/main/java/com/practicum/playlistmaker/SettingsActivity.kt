package com.practicum.playlistmaker

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.google.android.material.switchmaterial.SwitchMaterial

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        val btBack = findViewById<Button>(R.id.bt_back)
        btBack.setOnClickListener {
            val backIntent = Intent(this, MainActivity::class.java)
            startActivity(backIntent)
        }
        val btShare = findViewById<Button>(R.id.bt_share)
        btShare.setOnClickListener {
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.type = "text/plain"
            shareIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.share_application_message))
            startActivity(shareIntent)
        }
        val btSupport = findViewById<Button>(R.id.bt_support)
        btSupport.setOnClickListener {
            val techSupportIntent = Intent(Intent.ACTION_SENDTO)
            techSupportIntent.data = Uri.parse("mailto:")
            techSupportIntent.putExtra(
                Intent.EXTRA_EMAIL,
                arrayOf(getString(R.string.tech_support_email))
            )
            techSupportIntent.putExtra(
                Intent.EXTRA_SUBJECT,
                getString(R.string.tech_support_email_title)
            )
            techSupportIntent.putExtra(
                Intent.EXTRA_TEXT,
                getString(R.string.tech_support_email_message)
            )
            startActivity(techSupportIntent)
        }
        val btUserAgreement = findViewById<Button>(R.id.bt_user_agreement)
        btUserAgreement.setOnClickListener {
            val userAgreementIntent =
                Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.user_agreement_url)))
            startActivity(userAgreementIntent)
        }
        val themeSwitcher = findViewById<SwitchMaterial>(R.id.themeSwitcher)
        val myPref =
            getSharedPreferences(SharedPreferenceManager.PLAYLIST_SAVED_PREFERENCES, MODE_PRIVATE)
        val savedNightTheme = SharedPreferenceManager.getSavedNightTheme(myPref)
        themeSwitcher.isChecked = savedNightTheme
        themeSwitcher.setOnCheckedChangeListener { switcher, checked ->
            (applicationContext as App).switchTheme(checked)
        }
    }
}