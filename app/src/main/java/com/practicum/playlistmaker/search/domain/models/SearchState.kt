package com.practicum.playlistmaker.search.domain.models

sealed class SearchState {
    object Default :
        SearchState()//в самом начале до инициализации, и когда есть текст, но не нажали запрос

    class ReadyAndHistory(val historyTracks: List<Track>) :
        SearchState()//когда пустой инпут, может быть с историей

    object Loading : SearchState()
    object NoConnection : SearchState()
    object NoMatch : SearchState()
    class FinishedSearch(val searchedTracks: List<Track>) : SearchState()//результаты поиска

}