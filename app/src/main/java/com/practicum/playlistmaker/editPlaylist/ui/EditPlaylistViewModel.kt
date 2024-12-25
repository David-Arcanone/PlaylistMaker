package com.practicum.playlistmaker.editPlaylist.ui

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.editPlaylist.domain.models.EditPlaylistState
import com.practicum.playlistmaker.newPlaylist.domain.db.NewPlaylistInteractor
import com.practicum.playlistmaker.newPlaylist.domain.models.Playlist
import kotlinx.coroutines.launch

class EditPlaylistViewModel(
    private val myPlaylistInteractor: NewPlaylistInteractor,
    private val myPlaylistId:Int
) : ViewModel() {
    //трек на экране
    private var editPlaylistLiveData =
        MutableLiveData<EditPlaylistState>(EditPlaylistState.NotInitState)
    private lateinit var oldPlaylist:Playlist

    init {
        viewModelScope.launch {
            myPlaylistInteractor.getPlaylist(myPlaylistId).collect{
                playlist->
                if(playlist!=null){
                    oldPlaylist=playlist
                    editPlaylistLiveData.postValue(EditPlaylistState.DoneInitState(playlist))
                } else{
                    editPlaylistLiveData.postValue(EditPlaylistState.NoSaveFound)
                }
            }
        }
    }
    fun requestPlaylistEditing(){
        editPlaylistLiveData.postValue(EditPlaylistState.DoneInitStateEditing(oldPlaylist.imgSrc,true))
    }

    fun getStateLiveData(): LiveData<EditPlaylistState> = editPlaylistLiveData

    fun showInputNameChange(newText: String) {
        val prevState = editPlaylistLiveData.value
        if(prevState is EditPlaylistState.DoneInitStateEditing){
            editPlaylistLiveData.postValue(
                EditPlaylistState.DoneInitStateEditing(
                    prevState?.pictureUri,
                    newText.trim().isNotEmpty(),
                )
            )
        }
    }

    fun savePlaylistClick(
        name: String,
        description: String,
        onFinishCallback: () -> Unit,
    ) {
        viewModelScope.launch {
            val prevState = editPlaylistLiveData.value
            if(prevState is EditPlaylistState.DoneInitStateEditing){
                val oldPic=oldPlaylist.imgSrc
                val uriOfImgToSave = if (prevState?.pictureUri != null && prevState?.pictureUri !=oldPic) {
                    if(oldPic!=null){//если надо удаляю предыдущую картинку из хранилища
                        myPlaylistInteractor.deletePictureFromStorage(oldPic)
                    }
                    myPlaylistInteractor.savePictureToStorage(prevState.pictureUri, name)
                } else oldPlaylist.imgSrc
                if (name.isNotEmpty()) {
                    myPlaylistInteractor.updatePlaylist(Playlist(
                        id=myPlaylistId,
                        playlistName = name,
                        playlistDescription = description,
                        imgSrc = uriOfImgToSave,
                        listOfTracks = oldPlaylist.listOfTracks,
                        totalSeconds = oldPlaylist.totalSeconds
                    ))
                }
                clearAll()
                onFinishCallback()
            }
        }
    }

    fun newPicture(newPic: Uri) {
        val prevState = editPlaylistLiveData.value
        if(prevState is EditPlaylistState.DoneInitStateEditing)
        editPlaylistLiveData.postValue(
            EditPlaylistState.DoneInitStateEditing(
                pictureUri = newPic,
                prevState.isVerified,
            )
        )
    }

    fun clearAll() {
    }

}