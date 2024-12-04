package com.practicum.playlistmaker.player.domain.models

sealed class PlayerMediaState(val isLikedFlag: Boolean) {
    object Default:PlayerMediaState(isLikedFlag = false)
    class Paused(val timeValue:String, val isLiked:Boolean):PlayerMediaState(isLiked)
    class Playing(val timeValue:String, val isLiked:Boolean):PlayerMediaState(isLiked)
    class Prepared(val timeValue:String, val isLiked:Boolean):PlayerMediaState(isLiked)
}