package com.practicum.playlistmaker.newPlaylist.ui

import android.net.Uri
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
        name: String,
        description: String,
        onFinishCallback: () -> Unit,
    ) {
        viewModelScope.launch {
            val prevState = newPlaylistLiveData.value

            val uriOfImgToSave = if (prevState?.pictureUri != null) {
                myPlaylistInteractor.savePictureToStorage(prevState.pictureUri, name)
            } else null
            if (name.isNotEmpty()) {
                myPlaylistInteractor.saveNewPlaylist(
                    newName = name,
                    newDescription = description,
                    newPicture = uriOfImgToSave,
                )
            }
            clearAll()
            onFinishCallback()


        }
    }

    fun newPicture(newPic: Uri) {
        val prevState = newPlaylistLiveData.value
        newPlaylistLiveData.postValue(
            NewPlaylistState(
                pictureUri = newPic,
                prevState?.isVerified ?: false,
            )
        )

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