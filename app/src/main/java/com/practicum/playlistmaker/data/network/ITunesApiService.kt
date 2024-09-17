package com.practicum.playlistmaker.data.network

import com.practicum.playlistmaker.data.dto.ITunesSearchResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ITunesApiService {
    @GET("/search?entity=song")//поиск по музыке
    fun search(
        @Query("term", encoded = false) text: String
    ): Call<ITunesSearchResponse>
}