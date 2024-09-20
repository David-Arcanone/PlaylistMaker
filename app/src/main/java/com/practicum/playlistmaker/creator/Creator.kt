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
import com.practicum.playlistmaker.utils.Utilities

object Creator {
    private var contextIsNotSet = true
    private lateinit var context: Context
    private lateinit var sharedPref:SharedPreferences
    fun setContext(newContext: Context) {
        if (contextIsNotSet) {//возможно лишнее
            contextIsNotSet = false
            context = newContext
            sharedPref= context.getSharedPreferences(
                Utilities.PLAYLIST_SAVED_PREFERENCES,
                Context.MODE_PRIVATE
            )
        }
    }

    private fun provideGetSearchRepository(): SearchRepository {
        return SearchRepositoryImpl(RetrofitNetworkClient(), sharedPref)
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
}


