package com.practicum.playlistmaker

import android.content.Context
import android.util.TypedValue

object Utilities {
    val iTunesBaseUrl="https://itunes.apple.com"
    const val PLAYLIST_SAVED_PREFERENCES = "playlist_saved_preferences"
    const val NIGHT_THEME_KEY = "key_for_night_theme"
    const val SEARCH_HISTORY_KEY = "key_for_search_history"
    fun dpToPx(dp: Float, context: Context): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp,
            context.resources.displayMetrics).toInt()
    }
}