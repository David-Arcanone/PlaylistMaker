package com.practicum.playlistmaker.creator

import android.content.Context
import android.content.SharedPreferences
import android.media.MediaPlayer
import com.practicum.playlistmaker.data.repository.SearchRepositoryImpl
import com.practicum.playlistmaker.data.network.RetrofitNetworkClient
import com.practicum.playlistmaker.data.repository.PlayerRepositoryImpl
import com.practicum.playlistmaker.data.repository.SettingsRepositoryImpl
import com.practicum.playlistmaker.domain.api.SearchInteractor
import com.practicum.playlistmaker.domain.api.SearchRepository
import com.practicum.playlistmaker.domain.api.PlayerInteractor
import com.practicum.playlistmaker.domain.api.PlayerRepository
import com.practicum.playlistmaker.domain.api.SettingsInteractor
import com.practicum.playlistmaker.domain.api.SettingsRepository
import com.practicum.playlistmaker.domain.impl.SearchInteractorImpl
import com.practicum.playlistmaker.domain.impl.PlayerInteractorImpl
import com.practicum.playlistmaker.domain.impl.SettingsInteractorImpl

object Creator {
    private var contextIsNotSet = true
    private lateinit var context: Context //если реализация подходит уберу sharedPref со всех функций внизу
    fun setContext(newContext: Context) {
        if (contextIsNotSet) {//возможно лишнее
            contextIsNotSet = false
            context = newContext
        }
    }

    private fun provideGetSearchRepository(sharedPref: SharedPreferences): SearchRepository {
        return SearchRepositoryImpl(RetrofitNetworkClient(), sharedPref)
    }

    fun provideGetSearchInteractor(sharedPref: SharedPreferences): SearchInteractor {
        return SearchInteractorImpl(provideGetSearchRepository(sharedPref))
    }

    private fun provideGetSettingsRepository(): SettingsRepository {
        return SettingsRepositoryImpl(context)
    }

    fun provideGetSettingsInteractor(): SettingsInteractor {
        return SettingsInteractorImpl(provideGetSettingsRepository())
    }

    private fun provideGetPlayerRepository(
        sharedPref: SharedPreferences,
        mediaPlayer: MediaPlayer
    ): PlayerRepository {
        return PlayerRepositoryImpl(sharedPref, mediaPlayer)
    }

    fun provideGetPlayerInteractor(sharedPref: SharedPreferences): PlayerInteractor {
        return PlayerInteractorImpl(provideGetPlayerRepository(sharedPref, MediaPlayer()))
    }
}


