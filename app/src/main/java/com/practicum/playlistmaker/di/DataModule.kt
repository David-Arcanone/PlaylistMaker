package com.practicum.playlistmaker.di

import android.content.Context
import androidx.room.Room
import com.google.gson.Gson
import com.practicum.playlistmaker.medialibrary.data.db.AppPlaylistMakerDatabase
import com.practicum.playlistmaker.search.data.network.ITunesApiService
import com.practicum.playlistmaker.search.data.network.NetworkClient
import com.practicum.playlistmaker.search.data.network.RetrofitNetworkClient
import com.practicum.playlistmaker.utils.Utilities
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create

private val iTunesUrl = "https://itunes.apple.com"

val dataModule = module {

    single<ITunesApiService> {//для RetrofitNetworkClient
        Retrofit.Builder()
            .baseUrl(iTunesUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create<ITunesApiService>() //.create(ITunesApiService::class.java)
    }

    single<NetworkClient> { RetrofitNetworkClient(get()) }

    single {//для sharedPref
        androidContext()
            .getSharedPreferences(
                Utilities.PLAYLIST_SAVED_PREFERENCES,
                Context.MODE_PRIVATE
            )
    }

    factory { Gson() }//для Gson в записи на sharedPreferences

    single {//для базы данных
        Room.databaseBuilder(
            androidContext(),
            AppPlaylistMakerDatabase::class.java,
            "playlistMakerDatabase.db"
        )
            .build()
    }
}