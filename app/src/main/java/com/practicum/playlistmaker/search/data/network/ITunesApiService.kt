package com.practicum.playlistmaker.search.data.network

import com.practicum.playlistmaker.search.data.dto.ITunesSearchResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ITunesApiService {
    @GET("/search?entity=song")//поиск по музыке
    suspend fun search(
        @Query("term", encoded = false) text: String
    ): ITunesSearchResponse
}