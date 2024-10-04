package com.practicum.playlistmaker.utils

import android.content.Context
import android.content.SharedPreferences
import android.media.MediaPlayer
import com.practicum.playlistmaker.search.data.repository.SearchRepositoryImpl
import com.practicum.playlistmaker.search.data.network.RetrofitNetworkClient
import com.practicum.playlistmaker.player.data.repository.PlayerRepositoryImpl
import com.practicum.playlistmaker.settings.data.repository.SettingsRepositoryImpl
import com.practicum.playlistmaker.search.domain.api.SearchInteractor
import com.practicum.playlistmaker.search.domain.api.SearchRepository
import com.practicum.playlistmaker.player.domain.api.PlayerInteractor
import com.practicum.playlistmaker.player.domain.api.PlayerRepository
import com.practicum.playlistmaker.settings.domain.api.SettingsInteractor
import com.practicum.playlistmaker.settings.domain.api.SettingsRepository
import com.practicum.playlistmaker.search.domain.impl.SearchInteractorImpl
import com.practicum.playlistmaker.player.domain.impl.PlayerInteractorImpl
import com.practicum.playlistmaker.settings.domain.impl.SettingsInteractorImpl
import com.practicum.playlistmaker.sharing.data.repository.SharingRepositoryImpl
import com.practicum.playlistmaker.sharing.domain.api.SharingInteractor
import com.practicum.playlistmaker.sharing.domain.api.SharingRepository
import com.practicum.playlistmaker.sharing.domain.impl.SharingInteractorImpl
/*
object Creator {
    private var contextIsNotSet = true
    private lateinit var context: Context
    private lateinit var sharedPref:SharedPreferences
    fun setContext(newContext: Context) {
        if (contextIsNotSet) {//возможно лишнее
            contextIsNotSet = false
            context = newContext
            sharedPref = context.getSharedPreferences(
                Utilities.PLAYLIST_SAVED_PREFERENCES,
                Context.MODE_PRIVATE
            )
        }
    }

    private fun provideGetSearchRepository(): SearchRepository {
        return SearchRepositoryImpl(RetrofitNetworkClient(), context)
    }

    fun provideGetSearchInteractor(): SearchInteractor {
        return SearchInteractorImpl(provideGetSearchRepository())
    }

    private fun provideGetSettingsRepository(): SettingsRepository {
        return SettingsRepositoryImpl(context)
    }

    fun provideGetSettingsInteractor(): SettingsInteractor {
        return SettingsInteractorImpl(provideGetSettingsRepository())
    }

    private fun provideGetPlayerRepository(
        mediaPlayer: MediaPlayer
    ): PlayerRepository {
        return PlayerRepositoryImpl(sharedPref, mediaPlayer)
    }

    fun provideGetPlayerInteractor(): PlayerInteractor {
        return PlayerInteractorImpl(provideGetPlayerRepository(MediaPlayer()))
    }
    private fun provideGetSharingRepository():SharingRepository{
        return SharingRepositoryImpl(context)
    }
    fun provideGetSharingInteractor():SharingInteractor{
        return SharingInteractorImpl(provideGetSharingRepository())
    }
}

*/
