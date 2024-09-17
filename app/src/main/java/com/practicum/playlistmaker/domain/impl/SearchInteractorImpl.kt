package com.practicum.playlistmaker.domain.impl


import android.os.Bundle
import com.practicum.playlistmaker.domain.api.SearchInteractor
import com.practicum.playlistmaker.domain.api.SearchRepository
import com.practicum.playlistmaker.domain.consumer.TracksConsumer
import com.practicum.playlistmaker.domain.models.SearchedTracks
import com.practicum.playlistmaker.domain.models.Track
import java.util.concurrent.Executors

class SearchInteractorImpl(private val repository: SearchRepository) : SearchInteractor {
    private val executor = Executors.newCachedThreadPool()//для запуска нового потока

    override fun searchTracks(expression: String, consumer: TracksConsumer<SearchedTracks>) {
        executor.execute {//новый поток
            //вызываю обработку поиска через репозиторий, а результат в консьюмер
            consumer.consume(repository.searchTracks(expression))
        }
    }

    //работа с панелькой истории
    override fun getSavedTrackHistory(): ArrayList<Track> {
        return repository.getSavedTrackHistory()
    }

    override fun saveTrackHistory(newValue: ArrayList<Track>) {
        repository.saveTrackHistory(newValue)
    }

    override fun clearHistory() {
        repository.saveTrackHistory(ArrayList())//записываю пустую историю
    }

    override fun getSavedTrack(): Track? {
        return repository.getSavedTrack()
    }

    override fun saveCurrentTrack(newValue: Track) {
        repository.saveCurrentTrack(newValue)
    }

    override fun getSavedInstanceEditTextValue(savedInstanceState: Bundle?): String {
        return repository.getSavedInstanceEditTextValue(savedInstanceState)
    }

}