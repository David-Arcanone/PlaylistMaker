package com.practicum.playlistmaker.player.domain.models

import com.practicum.playlistmaker.newPlaylist.domain.models.Playlist
import com.practicum.playlistmaker.search.domain.models.Track

sealed class PlayerInitializationState {
    object NotInitState:PlayerInitializationState()
    data class DoneInitState(val currentTrack: Track,val isLiked:Boolean):PlayerInitializationState()
    data class DoneInitStateBottomSheet(val currentTrack: Track,val isLiked:Boolean,val listOfPlaylists:List<Playlist>):PlayerInitializationState()
    object NoSaveFound:PlayerInitializationState()//может сработать если нет промо url для прослушивания, а она должна быть у всех музыкальных треков, т.е. скорее всего сохранилось с ошибкой
}