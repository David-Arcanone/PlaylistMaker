package com.practicum.playlistmaker.utils

import android.content.Context
import android.util.TypedValue

object Utilities {
    const val PLAYLIST_SAVED_PREFERENCES = "playlist_saved_preferences"
    const val NIGHT_THEME_KEY = "key_for_night_theme"
    fun dpToPx(dp: Float, context: Context): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp,
            context.resources.displayMetrics
        ).toInt()
    }
}