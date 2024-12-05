package com.practicum.playlistmaker.di

import android.media.MediaPlayer
import com.practicum.playlistmaker.medialibrary.data.converters.MusicFavoriteDbConvertor
import com.practicum.playlistmaker.medialibrary.data.repository.FavoritesHistoryRepositoryImpl
import com.practicum.playlistmaker.medialibrary.domain.db.FavoritesHistoryRepository
import com.practicum.playlistmaker.player.data.repository.PlayerRepositoryImpl
import com.practicum.playlistmaker.player.domain.api.PlayerRepository
import com.practicum.playlistmaker.search.data.repository.SearchRepositoryImpl
import com.practicum.playlistmaker.search.domain.api.SearchRepository
import com.practicum.playlistmaker.settings.data.repository.SettingsRepositoryImpl
import com.practicum.playlistmaker.settings.domain.api.SettingsRepository
import com.practicum.playlistmaker.sharing.data.repository.SharingRepositoryImpl
import com.practicum.playlistmaker.sharing.domain.api.SharingRepository
import org.koin.dsl.module

val repositoryModule = module {

    factory<PlayerRepository> {
        PlayerRepositoryImpl(
            sharedPref = get(),
            mediaPlayer = get(),
            myGson = get(),
            //myDatabase = get(),
            //myMusicDbConvertor = get()
        )
    }

    factory<MediaPlayer> {
        MediaPlayer()
    }

    single<SearchRepository> {
        SearchRepositoryImpl(
            networkClient = get(),
            sharedPref = get(),
            myGson = get()
        )
    }

    single<SettingsRepository> { SettingsRepositoryImpl(context = get(), sharedPref = get()) }

    single<SharingRepository> { SharingRepositoryImpl(context = get()) }

    factory { MusicFavoriteDbConvertor() }

    single<FavoritesHistoryRepository> { FavoritesHistoryRepositoryImpl(get(), get(), get()) }
}