package com.practicum.playlistmaker.data.repository

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.domain.api.SettingsRepository
import com.practicum.playlistmaker.utils.Utilities

class SettingsRepositoryImpl (private val context: Context): SettingsRepository {
    private val sharedPref=context.getSharedPreferences(Utilities.PLAYLIST_SAVED_PREFERENCES,AppCompatActivity.MODE_PRIVATE)
    companion object{
        private const val NIGHT_THEME_KEY = "key_for_night_theme"
    }

    override fun getSavedNightTheme() =
        sharedPref.getBoolean(NIGHT_THEME_KEY, false)

    override fun saveNightTheme(newValue: Boolean) {
        sharedPref.edit()
            .putBoolean(NIGHT_THEME_KEY, newValue)
            .apply()
    }

    override fun openShare() {
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.type = "text/plain"
        shareIntent.putExtra(Intent.EXTRA_TEXT, context.getString(R.string.share_application_message))
        context.startActivity(shareIntent)
    }

    override fun openSupport() {
        val techSupportIntent = Intent(Intent.ACTION_SENDTO)
        techSupportIntent.data = Uri.parse("mailto:")
        techSupportIntent.putExtra(
            Intent.EXTRA_EMAIL,
            arrayOf(context.getString(R.string.tech_support_email))
        )
        techSupportIntent.putExtra(
            Intent.EXTRA_SUBJECT,
            context.getString(R.string.tech_support_email_title)
        )
        techSupportIntent.putExtra(
            Intent.EXTRA_TEXT,
            context.getString(R.string.tech_support_email_message)
        )
        context.startActivity(techSupportIntent)
    }

    override fun openUserAgreement() {
        val userAgreementIntent =
            Intent(Intent.ACTION_VIEW, Uri.parse(context.getString(R.string.user_agreement_url)))
        context.startActivity(userAgreementIntent)
    }
}