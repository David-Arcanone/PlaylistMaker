package com.practicum.playlistmaker.newPlaylist.data.repository

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import android.util.Log
import androidx.core.net.toUri
import com.practicum.playlistmaker.root.data.db.AppPlaylistMakerDatabase
import com.practicum.playlistmaker.newPlaylist.data.converters.PlaylistsDbConvertor
import com.practicum.playlistmaker.newPlaylist.data.db.entity.PlaylistEntity
import com.practicum.playlistmaker.newPlaylist.domain.db.NewPlaylistRepository
import com.practicum.playlistmaker.newPlaylist.domain.models.Playlist
import com.practicum.playlistmaker.utils.Utilities
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import java.io.File
import java.io.FileOutputStream

class NewPlaylistRepositoryImpl(
    private val myDatabase: AppPlaylistMakerDatabase,
    private val myPlaylistDbConvertor: PlaylistsDbConvertor,
    private val myContext: Context
) : NewPlaylistRepository {
    val myDatabaseDao = myDatabase.playlistsDao()
    override fun getAllPlaylists(): Flow<List<Playlist>> =
        myDatabaseDao.getAllPlaylists().map { convertFromPlaylistEntity(it) }

    override fun getListOfPlaylistsNames(): Flow<List<String>> = flow {
        val names = myDatabaseDao.getPlaylistsNames()
        emit(names)
    }

    override fun getPlaylist(name: String): Flow<Playlist?> = flow {
        val playlist = convertFromPlaylistEntity(myDatabaseDao.getPlaylist(name))
        if (playlist.isEmpty()) emit(null) else emit(playlist[0])

    }

    override suspend fun addPlaylist(playlist: Playlist) {
        myDatabaseDao.insertPlaylist(myPlaylistDbConvertor.map(playlist))
    }

    override suspend fun deletePlaylist(name: String) {
        myDatabaseDao.deleteFromPlaylists(name)
    }

    override suspend fun updatePlaylist(playlist: Playlist) {
        myDatabaseDao.updatePlaylist(myPlaylistDbConvertor.map(playlist))
    }

    override fun saveImgToPrivateStorage(newUri: Uri, newName: String): Uri? {
        val filePath = File(
            myContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES),
            Utilities.PIC_DIRECTORY
        )
        if (!filePath.exists()) {
            filePath.mkdirs()
        }
        val file = File(filePath, newName + ".jpg")
        // создаём входящий поток байтов из выбранной картинки
        val inputStream = myContext.contentResolver.openInputStream(newUri)
        // создаём исходящий поток байтов в созданный выше файл
        val outputStream = FileOutputStream(file)
        // записываем картинку с помощью BitmapFactory

        Log.d("MY_BITMAP", "BITMAP_FACTORY_FLAG-1")
        BitmapFactory
            .decodeStream(inputStream)
            .compress(Bitmap.CompressFormat.JPEG, 100, outputStream)

        Log.d("MY_BITMAP", "BITMAP_FACTORY_FLAG-2")

        return file.toUri()
    }

    override fun getUriOfImgFromPrivateStorage(name: String): Uri? {

        try {
            val filePath = File(
                myContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                Utilities.PIC_DIRECTORY
            )
            val uri = File(filePath, name + ".jpg").toUri()
            return uri
        } catch (e: Exception) {
            return null //нет картинки
        }

    }

    private fun convertFromPlaylistEntity(playlists: List<PlaylistEntity>): List<Playlist> {
        return playlists.map { currentPlaylist -> myPlaylistDbConvertor.map(currentPlaylist) }
    }

    companion object {
        const val PIC_DIRECTORY = "myalbum"
    }

}