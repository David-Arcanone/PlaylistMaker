package com.practicum.playlistmaker.player.domain.models

sealed class PlayerMediaState() {
    object Default:PlayerMediaState()
    class Paused(val timeValue:String):PlayerMediaState()
    class Playing(val timeValue:String):PlayerMediaState()
    class Prepared(val timeValue:String):PlayerMediaState()
}