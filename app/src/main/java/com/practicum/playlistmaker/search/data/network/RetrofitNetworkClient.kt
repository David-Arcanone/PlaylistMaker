package com.practicum.playlistmaker.search.data.network

import com.practicum.playlistmaker.search.data.dto.ITunesSearchRequest
import com.practicum.playlistmaker.search.data.dto.Response
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RetrofitNetworkClient(
    private val iTunesApiService: ITunesApiService
) : NetworkClient {
    override suspend fun doRequest(dto: Any): Response {
        //проверяем выполняем ли мы поиск, в случае масштабирования добавляю еще обработок dto
        if (dto is ITunesSearchRequest) {
            return withContext(Dispatchers.IO) {
                try {
                    val resp = iTunesApiService.search(dto.expression)
                    resp.apply { resultCode = 200 }
                } catch (e: Throwable) {
                    Response().apply { resultCode = 500 }
                }
            }
        } else {//если неопознанный запрос, возвращаем код 400 - неверный запрос
            return Response().apply { resultCode = 400 }
        }
    }
}