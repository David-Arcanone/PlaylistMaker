package com.practicum.playlistmaker.player.domain.models

import com.practicum.playlistmaker.search.domain.models.Track

sealed class PlayerInitializationState {
    object NotInitState:PlayerInitializationState()
    data class DoneInitState(val currentTrack: Track):PlayerInitializationState()
    data class DoneInitStateLiked(val currentTrack: Track):PlayerInitializationState()
    object NoSaveFound:PlayerInitializationState()//может сработать если нет промо url для прослушивания, а она должна быть у всех музыкальных треков, т.е. скорее всего сохранилось с ошибкой
}