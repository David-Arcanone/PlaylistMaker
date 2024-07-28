package com.practicum.playlistmaker

import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object SharedPreferenceManager {
    fun getSavedTrackHistory(sharedPref: SharedPreferences):ArrayList<Track>{
        val trackType=object: TypeToken<ArrayList<Track>>(){}
        val json =sharedPref.getString(Utilities.SEARCH_HISTORY_KEY,null) ?: return ArrayList()
        return Gson().fromJson(json,trackType)
    }
    fun saveTrackHistory(sharedPref: SharedPreferences,newValue:ArrayList<Track>){
        val json =Gson().toJson(newValue)
        sharedPref.edit()
            .putString(Utilities.SEARCH_HISTORY_KEY,json)
            .apply()
    }
    fun getSavedNightTheme(sharedPref:SharedPreferences)=sharedPref.getBoolean(Utilities.NIGHT_THEME_KEY,false)
    fun saveNightTheme(sharedPref:SharedPreferences,newValue:Boolean){
        sharedPref.edit()
            .putBoolean(Utilities.NIGHT_THEME_KEY,newValue)
            .apply()
    }
}