package com.practicum.playlistmaker.utils

import android.content.Context
import android.util.TypedValue

object Utilities {
    const val PLAYLIST_SAVED_PREFERENCES = "playlist_saved_preferences"
    const val NIGHT_THEME_KEY = "key_for_night_theme"
    const val PIC_DIRECTORY = "myalbum"
    fun getSecondsFromText_mm_ss(str:String): Int {
        //все клипы в формате mm:ss, тк поиск в itunes только по музыке, а не по всему (фильмы итд)
        val strArray=str.split(":")
        return strArray[0].toInt()*60+strArray[1].toInt()
    }
}