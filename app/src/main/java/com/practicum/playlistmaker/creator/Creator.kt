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
    private fun provideGetSearchRepository(sharedPref: SharedPreferences):SearchRepository{
        return SearchRepositoryImpl(RetrofitNetworkClient(), sharedPref)
    }
    fun provideGetSearchInteractor(sharedPref: SharedPreferences): SearchInteractor {
        return SearchInteractorImpl(provideGetSearchRepository(sharedPref))
    }
    private fun provideGetSettingsRepository(context: Context):SettingsRepository{
        return SettingsRepositoryImpl(context)
    }

    fun provideGetSettingsInteractor(context: Context):SettingsInteractor{
        return SettingsInteractorImpl(provideGetSettingsRepository(context))
    }
    private fun provideGetPlayerRepository(sharedPref: SharedPreferences,mediaPlayer: MediaPlayer):PlayerRepository{
        return PlayerRepositoryImpl(sharedPref,mediaPlayer)
    }

    fun provideGetPlayerInteractor(sharedPref: SharedPreferences):PlayerInteractor{
        return PlayerInteractorImpl(provideGetPlayerRepository(sharedPref, MediaPlayer()))
    }
}


