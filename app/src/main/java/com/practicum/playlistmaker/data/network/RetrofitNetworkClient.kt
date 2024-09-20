package com.practicum.playlistmaker.data.network

import com.practicum.playlistmaker.data.dto.ITunesSearchRequest
import com.practicum.playlistmaker.data.dto.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create

class RetrofitNetworkClient:NetworkClient {
    private val iTunesUrl = "https://itunes.apple.com"
    private val retrofit= Retrofit.Builder()
        .baseUrl(iTunesUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    val iTunesApiService = retrofit.create<ITunesApiService>()

    override fun doRequest(dto: Any): Response {
        if(dto is ITunesSearchRequest){//проверяем выполняем ли мы поиск, в случае масштабирования добавляю еще обработок dto
            val resp = iTunesApiService.search(dto.expression).execute()
            val body = resp.body() ?: Response()
            return  body.apply { resultCode =resp.code() }
        } else{//если неопознанный запрос, возвращаем код 400 - неверный запрос
            return Response().apply { resultCode=400 }
        }
    }
}