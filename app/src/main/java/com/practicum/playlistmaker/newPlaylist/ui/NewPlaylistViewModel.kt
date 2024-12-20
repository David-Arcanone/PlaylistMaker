package com.practicum.playlistmaker.newPlaylist.ui

import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.newPlaylist.domain.db.NewPlaylistInteractor
import com.practicum.playlistmaker.newPlaylist.domain.models.NewPlaylistState
import kotlinx.coroutines.launch

class NewPlaylistViewModel(
    private val myPlaylistInteractor: NewPlaylistInteractor,
) : ViewModel() {
    //трек на экране
    private var newPlaylistLiveData =
        MutableLiveData<NewPlaylistState>(NewPlaylistState(null, false))

    init {
    }

    fun getStateLiveData(): LiveData<NewPlaylistState> = newPlaylistLiveData

    fun showInputNameChange(newText: String) {
        val prevState = newPlaylistLiveData.value
        newPlaylistLiveData.postValue(
            NewPlaylistState(
                prevState?.pictureUri,
                newText.trim().isNotEmpty(),
            )
        )
    }

    fun createNewPlaylistClick(
        name:String ,
        description:String,
        onFinishCallback: () -> Unit,
        onConflictCallback: () -> Unit,
    ) {
        viewModelScope.launch {
            val prevState = newPlaylistLiveData.value
            myPlaylistInteractor.getListOfPlaylistsName().collect { listOfNames ->
                if (listOfNames.contains(name)) {
                    onConflictCallback()
                } else {
                    val uriOfImgToSave = if (prevState?.pictureUri != null) {
                        Log.d("MY_URI","из стейта " + prevState?.pictureUri.toString() +"")//todo-

                        myPlaylistInteractor.savePictureToStorage(prevState.pictureUri, name)
                        myPlaylistInteractor.getUriOfPictureFromStorage(name)
                    } else null
                    Log.d("MY_URI","из функции " + uriOfImgToSave.toString() +"")//todo-

                    if (name.isNotEmpty()) {
                        myPlaylistInteractor.saveNewPlaylist(
                            newName = name,
                            newDescription = description,
                            newPicture = uriOfImgToSave,
                            listOfTracks = emptyList(),
                        )
                    }
                    clearAll()
                    onFinishCallback()
                }
            }
        }
    }

    fun newPicture(newPic: Uri) {
        val prevState = newPlaylistLiveData.value
        newPlaylistLiveData.postValue(NewPlaylistState(
            pictureUri = newPic,
            prevState?.isVerified ?: false,
        ))

    }

    fun clearAll() {
        newPlaylistLiveData.postValue(NewPlaylistState(null, false))
    }

    override fun onCleared() {
        super.onCleared()
    }


    companion object {
        private const val ZERO_TEXT = ""
    }
}