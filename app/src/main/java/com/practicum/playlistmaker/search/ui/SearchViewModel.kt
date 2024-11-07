package com.practicum.playlistmaker.search.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.search.domain.api.SearchInteractor
import com.practicum.playlistmaker.search.domain.models.SearchState
import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SearchViewModel(val mySearchInteractor: SearchInteractor) : ViewModel() {
    private var searchLiveData =
        MutableLiveData<SearchState>(SearchState.Default)
    private var inputTextData = ""
    private var lastRequest = ""
    private var isClickOnTrackAllowed = true
    private var searchJob: Job? = null
    private var clickDebounceJob: Job? = null

    init {
        val history = mySearchInteractor.getSavedTrackHistory()
        searchLiveData.postValue(SearchState.ReadyAndHistory(history))
    }

    fun makeSearch(searchText: String) {
        if (searchText.isNotEmpty()) {
            inputTextData = searchText
            lastRequest = searchText
            searchLiveData.postValue(SearchState.Loading)
            viewModelScope.launch {
                mySearchInteractor
                    .searchTracks(searchText)
                    .collect { pair ->
                        if (searchText == inputTextData) {//проверим что к моменту завершения поиска актуальность запроса не пропала
                            if (!pair.second) {//неудачный ответ от сервера
                                showErrorConnection()
                            } else if (pair.first.isNotEmpty()) {//удачное соединение и есть треки
                                searchLiveData.postValue(SearchState.FinishedSearch(pair.first))
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
            }
        }
    }

    fun showHistory() {
        val currentHistoryTracks = mySearchInteractor.getSavedTrackHistory()
        searchLiveData.postValue(SearchState.ReadyAndHistory(currentHistoryTracks))
    }

    fun showClearHistory() {
        mySearchInteractor.saveTrackHistory(emptyList())
        searchLiveData.postValue(SearchState.ReadyAndHistory(emptyList()))
    }

    fun showClickOnTrack(newTrack: Track, fragmentOpener: () -> Unit) {
        if (isClickOnTrackAllowed) {//клик разрешен
            isClickOnTrackAllowed = false
            //по условиям запрет на активацию по первому нажатию на трек и отменять старый Job не нужно
            clickDebounceJob = viewModelScope.launch {
                delay(SEARCH_DEBOUNCE_DELAY)
                isClickOnTrackAllowed = true
            }
            mySearchInteractor.updateHistoryWithNewTrack(newTrack)
            fragmentOpener()
        }
    }

    fun showNoMatch() {
        searchLiveData.postValue(SearchState.NoMatch)
    }

    fun showErrorConnection() {
        searchLiveData.postValue(SearchState.NoConnection)
    }

    fun showInputTextChange(newText: String) {
        searchJob?.cancel()//убираем старую корутину поиска
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
            searchJob = viewModelScope.launch {
                delay(SEARCH_DEBOUNCE_DELAY)
                makeSearch(inputTextData)
            }
            //запуск корутину поиска с задержкой
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