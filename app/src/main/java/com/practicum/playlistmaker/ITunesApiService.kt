package com.practicum.playlistmaker

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ITunesApiService {
    @GET("/search")
    fun search(
        @Query("term", encoded = false) text: String
    ):Call<iTunesSearchResponse>
}