package com.practicum.playlistmaker.search.data.repository

import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.practicum.playlistmaker.search.data.dto.ITunesSearchRequest
import com.practicum.playlistmaker.search.data.dto.ITunesSearchResponse
import com.practicum.playlistmaker.search.data.network.NetworkClient
import com.practicum.playlistmaker.search.domain.api.SearchRepository
import com.practicum.playlistmaker.search.domain.models.SearchedTracks
import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow


class SearchRepositoryImpl(
    private val networkClient: NetworkClient,
    private val sharedPref: SharedPreferences,
    private val myGson: Gson
) :
    SearchRepository {
    companion object {
        private const val PLAYLIST_CURRENT_TRACK = "playlist_current_track"
        private const val SEARCH_HISTORY_KEY = "key_for_search_history"
    }

    override fun searchTracks(expression: String): Flow<SearchedTracks> = flow{
        val response = networkClient.doRequest(ITunesSearchRequest(expression))
        when(response.resultCode){
            in 200..299 ->{
                val foundTracks = (response as ITunesSearchResponse).results
                if (foundTracks.isNotEmpty()) {//есть совпадения
                    emit(SearchedTracks(foundTracks.map {
                        Track(
                            trackName = it.trackName,
                            artistName = it.artistName,
                            trackTime = it.trackTime,
                            artWorkUrl100 = it.artWorkUrl100,
                            trackId = it.trackId,
                            country = it.country,
                            primaryGenreName = it.primaryGenreName,
                            collectionName = it.collectionName,
                            previewUrl = it.previewUrl,
                            coverImg = it.getCoverImg(),
                            releaseYear = it.getReleaseYear(),
                            trackLengthText = it.showTrackTime()
                        )
                    }, isSucceded = true))
                } else {//нет совпадений
                    emit(SearchedTracks(emptyList(), isSucceded = true))
                }
            }
            else -> {
                emit(SearchedTracks(emptyList(), isSucceded = false))
            }
        }
    }

    override fun saveTrackHistory(newValue: List<Track>) {
        val json = myGson.toJson(newValue)
        sharedPref.edit()
            .putString(SEARCH_HISTORY_KEY, json)
            .apply()
    }

    override fun getSavedTrackHistory(): List<Track> {
        val trackType = object : TypeToken<List<Track>>() {}
        val json = sharedPref.getString(SEARCH_HISTORY_KEY, null) ?: return emptyList()
        return myGson.fromJson(json, trackType)
    }

    override fun getSavedTrack(): Track? {
        val trackType = object : TypeToken<Track>() {}
        val json = sharedPref.getString(PLAYLIST_CURRENT_TRACK, null) ?: return null
        return myGson.fromJson(json, trackType)
    }

    override fun saveCurrentTrack(newValue: Track) {
        val json = myGson.toJson(newValue)
        sharedPref.edit()
            .putString(PLAYLIST_CURRENT_TRACK, json)
            .apply()
    }
}