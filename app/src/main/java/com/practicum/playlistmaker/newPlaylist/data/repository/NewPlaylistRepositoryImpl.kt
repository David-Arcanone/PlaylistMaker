package com.practicum.playlistmaker.newPlaylist.data.repository

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import androidx.core.net.toUri
import com.practicum.playlistmaker.root.data.db.AppPlaylistMakerDatabase
import com.practicum.playlistmaker.newPlaylist.data.converters.PlaylistsDbConvertor
import com.practicum.playlistmaker.newPlaylist.data.db.entity.PlaylistEntity
import com.practicum.playlistmaker.newPlaylist.domain.db.NewPlaylistRepository
import com.practicum.playlistmaker.newPlaylist.domain.models.Playlist
import com.practicum.playlistmaker.utils.Utilities
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.File
import java.io.FileOutputStream

class NewPlaylistRepositoryImpl(
    private val myDatabase: AppPlaylistMakerDatabase,
    private val myPlaylistDbConvertor: PlaylistsDbConvertor,
    private val myContext: Context
) : NewPlaylistRepository {
    val myDatabaseDao = myDatabase.playlistsDao()
    override fun getAllPlaylists(): Flow<List<Playlist>> = flow{
        val playlists=myDatabaseDao.getAllPlaylists().map {
                currentPlaylist -> myPlaylistDbConvertor.map(currentPlaylist)
        }
        emit(playlists)
    }


    override fun getListOfPlaylistsIds(): Flow<List<Int>> = flow {
        val ids = myDatabaseDao.getPlaylistsIds()
        emit(ids)
    }

    override fun getPlaylist(chousenId:Int): Flow<Playlist?> = flow {
        val playlist = convertFromPlaylistEntity(myDatabaseDao.getPlaylist(chousenId))
        if (playlist.isEmpty()) emit(null) else emit(playlist[0])
    }

    override suspend fun addPlaylist(
        newName: String,
        newDescription: String?,
        newPic: Uri?,
        listOfTracks: List<Int>) {

        myDatabaseDao.insertPlaylist(PlaylistEntity(
            playlistName = newName,
            playlistDescription = newDescription,
            playlistPicture = newPic.toString(),
            listOfTracks = myPlaylistDbConvertor.map(listOfTracks),
            totalSeconds = 0
        ))
    }

    override suspend fun deletePlaylist(redundantId:Int) {
        myDatabaseDao.deleteFromPlaylists(redundantId)
    }

    override suspend fun updatePlaylist(playlist: Playlist) {
        myDatabaseDao.updatePlaylist(myPlaylistDbConvertor.map(playlist))
    }

    override fun saveImgToPrivateStorage(newUri: Uri, newName: String): Uri {
        val filePath = File(
            myContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES),
            Utilities.PIC_DIRECTORY
        )
        if (!filePath.exists()) {
            filePath.mkdirs()
        }
        val salt=System.currentTimeMillis().toString()
        val file = File(filePath, newName + salt+".jpg")
        // создаём входящий поток байтов из выбранной картинки
        val inputStream = myContext.contentResolver.openInputStream(newUri)
        // создаём исходящий поток байтов в созданный выше файл
        val outputStream = FileOutputStream(file)
        // записываем картинку с помощью BitmapFactory
        BitmapFactory
            .decodeStream(inputStream)
            .compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
        return file.toUri()
    }
    override fun deleteImgFromPrivateStorage(oldUri: Uri) {
        val filePath = File(
            myContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES),
            Utilities.PIC_DIRECTORY
        )
        if (!filePath.exists()) {
            filePath.mkdirs()
        }
        val fileToUpdate=File(oldUri.toString())
        val results=fileToUpdate.delete()
        if(results){
            //Log.d("MY_DEL","SUCCEDED")//успешное удаление
        }else{
            //Log.d("MY_DEL","FAILED")//неуспешное удаление
        }
    }

    private fun convertFromPlaylistEntity(playlists: List<PlaylistEntity>): List<Playlist> {
        return playlists.map { currentPlaylist -> myPlaylistDbConvertor.map(currentPlaylist) }
    }

    companion object {
        const val PIC_DIRECTORY = "myalbum"
    }

}