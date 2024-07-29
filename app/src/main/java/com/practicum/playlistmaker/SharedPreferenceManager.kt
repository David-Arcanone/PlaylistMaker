package com.practicum.playlistmaker

import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object SharedPreferenceManager {
    const val PLAYLIST_SAVED_PREFERENCES = "playlist_saved_preferences"
    private const val PLAYLIST_CURRENT_TRACK = "playlist_current_track"
    private const val NIGHT_THEME_KEY = "key_for_night_theme"
    private const val SEARCH_HISTORY_KEY = "key_for_search_history"
    /*private var mySharedPreferences:SharedPreferences?=null

    fun setMySharedPreferences(sharedPref: SharedPreferences){
        mySharedPreferences=sharedPref}*/
    fun getSavedTrackHistory(sharedPref: SharedPreferences):ArrayList<Track>{
        val trackType=object: TypeToken<ArrayList<Track>>(){}
        val json =sharedPref.getString(SEARCH_HISTORY_KEY,null) ?: return ArrayList()
        return Gson().fromJson(json,trackType)
    }
    fun saveTrackHistory(sharedPref: SharedPreferences,newValue:ArrayList<Track>){
        val json =Gson().toJson(newValue)
        sharedPref.edit()
            .putString(SEARCH_HISTORY_KEY,json)
            .apply()
    }
    fun getSavedNightTheme(sharedPref:SharedPreferences)=sharedPref.getBoolean(NIGHT_THEME_KEY,false)
    fun saveNightTheme(sharedPref:SharedPreferences,newValue:Boolean){
        sharedPref.edit()
            .putBoolean(NIGHT_THEME_KEY,newValue)
            .apply()
    }
    fun getSavedTrack(sharedPref: SharedPreferences):Track?{
        val trackType=object: TypeToken<Track>(){}
        val json =sharedPref.getString(PLAYLIST_CURRENT_TRACK,null) ?: return null
        return Gson().fromJson(json,trackType)
    }
    fun saveCurrentTrack(sharedPref: SharedPreferences,newValue:Track){
        val json =Gson().toJson(newValue)
        sharedPref.edit()
            .putString(PLAYLIST_CURRENT_TRACK,json)
            .apply()
    }
}