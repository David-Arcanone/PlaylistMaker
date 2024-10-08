package com.practicum.playlistmaker.search.ui

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.practicum.playlistmaker.search.domain.api.SearchInteractor
import com.practicum.playlistmaker.search.domain.consumer.TracksConsumer
import com.practicum.playlistmaker.search.domain.models.SearchState
import com.practicum.playlistmaker.search.domain.models.SearchedTracks
import com.practicum.playlistmaker.search.domain.models.Track

class SearchViewModel(val mySearchInteractor: SearchInteractor) : ViewModel() {
    private val mainThreadHandler = Handler(Looper.getMainLooper())
    private var searchLiveData =
        MutableLiveData<SearchState>(SearchState.Default)
    private var inputTextData = ""
    private var lastRequest = ""
    private var isSearchAllowed = true
    val mySearchRunnable = Runnable {
        if (inputTextData.isNotEmpty()) {
            makeSearch(inputTextData)
        }
    }

    init {
        val history = mySearchInteractor.getSavedTrackHistory()
        searchLiveData.postValue(SearchState.ReadyAndHistory(history))
    }

    fun makeSearch(searchText: String) {
        if (searchText.isNotEmpty()) {
            inputTextData = searchText
            lastRequest = searchText
            searchLiveData.postValue(SearchState.Loading)
            mySearchInteractor.searchTracks(
                searchText,
                consumer = object : TracksConsumer<SearchedTracks> {
                    override fun consume(foundTracks: SearchedTracks) {
                        //mainThreadHandler.post {
                        if (searchText == inputTextData) {//проверим что к моменту завершения поиска актуальность запроса не пропала
                            if (!foundTracks.isSucceded) {//неудачный ответ от сервера
                                showErrorConnection()
                            } else if (foundTracks.tracks.isNotEmpty()) {//удачное соединение и есть треки
                                searchLiveData.postValue(SearchState.FinishedSearch(foundTracks.tracks))
                            } else {//удачное соединение но нет совпадений
                                showNoMatch()
                            }
                        } else {
                            if (inputTextData.length > 0) {//пользователь передумал проводить запрос
                                searchLiveData.postValue(SearchState.Default)
                            } else {//пользователь удалил запрос, значит показываем историю
                                showHistory()
                            }
                        }
                    }
                })
        }
    }

    private fun clickDebounce(): Boolean {
        val current = isSearchAllowed
        if (isSearchAllowed) {
            isSearchAllowed = false
            mainThreadHandler.postDelayed(
                { isSearchAllowed = true },
                SEARCH_DEBOUNCE_DELAY
            )
        }
        return current
    }

    fun showHistory() {
        val currentHistoryTracks = mySearchInteractor.getSavedTrackHistory()
        searchLiveData.postValue(SearchState.ReadyAndHistory(currentHistoryTracks))
    }

    fun showClearHistory() {
        mySearchInteractor.saveTrackHistory(emptyList())
        searchLiveData.postValue(SearchState.ReadyAndHistory(emptyList()))
    }

    fun showClickOnTrack(newTrack: Track) {
        if (clickDebounce()) {
            mySearchInteractor.updateHistoryWithNewTrack(newTrack)
            mySearchInteractor.startPlayerIntent()
        }
    }

    fun showNoMatch() {
        searchLiveData.postValue(SearchState.NoMatch)
    }

    fun showErrorConnection() {
        searchLiveData.postValue(SearchState.NoConnection)
    }

    fun showInputTextChange(newText: String) {
        mainThreadHandler.removeCallbacks(mySearchRunnable)//убираем коллбаэк поиска
        if (newText.isNullOrEmpty()) {
            when (searchLiveData.value) {//удалили строку запроса
                is SearchState.Default, is SearchState.NoConnection, is SearchState.Loading, is SearchState.NoMatch, is SearchState.FinishedSearch -> {
                    searchLiveData.postValue(SearchState.ReadyAndHistory(mySearchInteractor.getSavedTrackHistory()))
                }

                else -> {
                    Unit
                }
            }
        } else {//меняем стейт если добавили текста, когда была история, в иных случаях лайвстейт менять не нужно
            if (searchLiveData.value is SearchState.ReadyAndHistory) searchLiveData.postValue(
                SearchState.Default
            )
            mainThreadHandler.postDelayed(
                mySearchRunnable,
                SEARCH_DEBOUNCE_DELAY
            )//запуск коллбаэк поиска с задержкой
        }
        inputTextData = newText
    }

    fun showRefreshSearch() {
        makeSearch(lastRequest)
    }


    fun getSearchLiveData(): LiveData<SearchState> = searchLiveData

    companion object {
        const val SEARCH_DEBOUNCE_DELAY = 2000L
    }

}