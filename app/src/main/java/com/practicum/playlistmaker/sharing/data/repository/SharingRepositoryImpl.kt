package com.practicum.playlistmaker.sharing.data.repository

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.sharing.domain.api.SharingRepository

class SharingRepositoryImpl(private val context: Context):SharingRepository {
    override fun openShare() {
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.type = "text/plain"
        shareIntent.putExtra(Intent.EXTRA_TEXT, context.getString(R.string.share_application_message))
        shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(shareIntent)
    }

    override fun openSharePlaylist(playlistText: String, title: String, number: Int) {
        val newText=title+number+if(number==1)context.getString(R.string.track)else context.getString(R.string.tracks)+":"+playlistText
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.type = "text/plain"
        shareIntent.putExtra(Intent.EXTRA_TEXT, newText)
        shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
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
        techSupportIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(techSupportIntent)
    }

    override fun openUserAgreement() {
        val userAgreementIntent =
            Intent(Intent.ACTION_VIEW, Uri.parse(context.getString(R.string.user_agreement_url)))
        userAgreementIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(userAgreementIntent)
    }
}