package com.practicum.playlistmaker.data.repository

import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.practicum.playlistmaker.data.dto.ITunesSearchRequest
import com.practicum.playlistmaker.data.dto.ITunesSearchResponse
import com.practicum.playlistmaker.data.network.NetworkClient
import com.practicum.playlistmaker.domain.api.SearchRepository
import com.practicum.playlistmaker.domain.models.SearchedTracks
import com.practicum.playlistmaker.domain.models.Track

class SearchRepositoryImpl (private val networkClient: NetworkClient, private val sharedPref: SharedPreferences):SearchRepository{
    companion object{
        private const val PLAYLIST_CURRENT_TRACK = "playlist_current_track"
        private const val SEARCH_HISTORY_KEY = "key_for_search_history"
        private const val EDIT_VALUE = "EDIT_VALUE"
        private const val EDIT_DEFAULT = ""
    }
    override fun searchTracks(expression: String): SearchedTracks {
        try{
            val response = networkClient.doRequest(ITunesSearchRequest(expression))

            when(response.resultCode){
                in 200..299 ->{//успешный запрос
                    val foundTracks=(response as ITunesSearchResponse).results
                    if(foundTracks.isNotEmpty()){//есть совпадения
                        return SearchedTracks(foundTracks.map {Track(
                            trackName =  it.trackName,
                            artistName = it.artistName,
                            trackTime = it.trackTime,
                            artWorkUrl100 = it.artWorkUrl100,
                            trackId = it.trackId,
                            country = it.country,
                            primaryGenreName = it.primaryGenreName,
                            collectionName = it.collectionName,
                            previewUrl = it.previewUrl,
                            coverImg= it.getCoverImg(),
                            releaseYear = it.getReleaseYear(),
                            trackLengthText = it.showTrackTime())},isSucceded = true)
                    }else{//нет совпадений
                        return SearchedTracks(emptyList(), isSucceded = true)
                    }
                }
                else-> {//ошибка
                    Log.d("error-type",response.resultCode.toString())
                    return SearchedTracks(emptyList(), isSucceded = false)
                }
            }
        }
             catch(e:Exception){//неуспешный запрос
                 Log.d("error-type","exception")
                 return SearchedTracks(emptyList(), isSucceded = false)
             }
    }
    override fun saveTrackHistory(newValue: ArrayList<Track>) {
        val json = Gson().toJson(newValue)
        sharedPref.edit()
            .putString(SEARCH_HISTORY_KEY, json)
            .apply()
    }

    override fun getSavedTrackHistory(): ArrayList<Track> {
        val trackType = object : TypeToken<ArrayList<Track>>() {}
        val json = sharedPref.getString(SEARCH_HISTORY_KEY, null) ?: return ArrayList()
        return Gson().fromJson(json, trackType)
    }
    override fun getSavedTrack(): Track? {
        val trackType = object : TypeToken<Track>() {}
        val json = sharedPref.getString(PLAYLIST_CURRENT_TRACK, null) ?: return null
        return Gson().fromJson(json, trackType)
    }

    override fun saveCurrentTrack(newValue: Track) {
        val json = Gson().toJson(newValue)
        sharedPref.edit()
            .putString(PLAYLIST_CURRENT_TRACK, json)
            .apply()
    }

    override fun getSavedInstanceEditTextValue(savedInstanceState: Bundle?): String {
        return savedInstanceState?.getString(EDIT_VALUE, EDIT_DEFAULT).toString()
    }
}