package com.practicum.playlistmaker.editPlaylist.ui

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentNewPlaylistBinding
import com.practicum.playlistmaker.editPlaylist.domain.models.EditPlaylistState
import com.practicum.playlistmaker.utils.AndroidUtilities
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf


class EditPlaylistFragment : Fragment() {
    private var numberOfBlocks: Int = 0
    private lateinit var myBinding: FragmentNewPlaylistBinding
    private val myViewModel by viewModel<EditPlaylistViewModel>{
        parametersOf(requireArguments().getInt(ARGS_PLAYLIST_ID))
    }
    private val pickMedia =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            if (uri != null) {
                myViewModel.newPicture(uri)
            } else {
                Toast.makeText(context, R.string.you_didnt_pick_file, Toast.LENGTH_SHORT).show()
            }
        }
    private val requestPermissionLauncherMediaPick =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                numberOfBlocks = 0
                pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            } else {
                doIfNotHavePermission()
            }
        }
    private val requestPermissionLauncherSave =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                doIfHavePermissionToSave()

            } else {
                doIfNotHavePermission()
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        myBinding = FragmentNewPlaylistBinding.inflate(inflater, container, false)
        return myBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        myBinding.btBack.setText(R.string.edit_info)
        myBinding.btCreate.setText(R.string.save)
        myViewModel.getStateLiveData().observe(viewLifecycleOwner) { initState ->
            when (initState) {
                is EditPlaylistState.NotInitState->{//еще не подгрузили данные плейлиста
                }
                is EditPlaylistState.NoSaveFound->{//в базе нет плейлиста
                }
                is EditPlaylistState.DoneInitState->{//первичная инициализация для заполнения инфы
                    if (initState.currentPlaylist.imgSrc != null) {
                        renderNewPicture(initState.currentPlaylist.imgSrc)//картинка
                    }
                    myBinding.editNewName.setText(initState.currentPlaylist.playlistName)
                    myBinding.editNewDescription.setText(initState.currentPlaylist.playlistDescription)
                    myViewModel.requestPlaylistEditing()
                }
                is EditPlaylistState.DoneInitStateEditing->{
                    myBinding.btCreate.isEnabled =
                        if (initState.isVerified) true else false //разрешение на создание
                    if (initState.pictureUri != null) {
                        renderNewPicture(initState.pictureUri)//картинка
                    }
                }
            }
        }
        /*логика поля ввода*/
        val myNewNameTextWatcher = makeCustomTextWatcher { s ->
            myViewModel.showInputNameChange(s)
            renderEditText(s, myBinding.editNewName, myBinding.helpNewName)
        }
        val myNewDescriptionTextWatcher =
            makeCustomTextWatcher { s ->
                renderEditText(
                    s,
                    myBinding.editNewDescription,
                    myBinding.newDescHelp
                )
            }
        myBinding.editNewName.addTextChangedListener(myNewNameTextWatcher)
        myBinding.editNewDescription.addTextChangedListener(myNewDescriptionTextWatcher)
        //активность кнопок
        //копка назад
        val currentContext = this.requireActivity()
        fun showExitPromt() {
            myViewModel.clearAll()
            findNavController().navigateUp()
        }
        currentContext.onBackPressedDispatcher.addCallback(
            currentContext,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    showExitPromt()
                }
            })
        myBinding.btBack.setOnClickListener {
            showExitPromt()
        }
        //выбрать картинку
        myBinding.imageViewPlaylist.setOnClickListener {
            lifecycleScope.launch {
                requestPermissionLauncherMediaPick.launch(Manifest.permission.READ_MEDIA_IMAGES)
            }
        }
        //создать плейлист
        myBinding.btCreate.setOnClickListener {
            requestPermissionLauncherSave.launch(Manifest.permission.READ_MEDIA_IMAGES)
        }
    }

    fun makeCustomTextWatcher(callback: (s: String) -> Unit): TextWatcher {
        return object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                callback(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {
            }
        }
    }

    private fun renderNewPicture(picUri: Uri) {
        myBinding.imageViewPlaylist.setBackgroundResource(R.drawable.borders_for_new_picture_loaded)
        Glide.with(this.requireContext())
            .load(picUri)
            .placeholder(R.drawable.placeholder)
            .centerInside()
            .transform(
                RoundedCorners(
                    AndroidUtilities.dpToPx(
                        8f,
                        this.requireContext()
                    )
                )
            )
            .into(myBinding.imageViewPlaylist)
    }

    private fun doIfNotHavePermission() {
        numberOfBlocks += 1
        if (numberOfBlocks > 2) {
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.data = Uri.fromParts("package", context?.packageName, null)
            context?.startActivity(intent)
        }
    }

    private fun doIfHavePermissionToSave() {
        numberOfBlocks = 0
        myViewModel.savePlaylistClick(
            name = myBinding.editNewName.text.toString(),
            description = myBinding.editNewDescription.text.toString(),
            onFinishCallback = { findNavController().navigateUp() },
        )
    }

    private fun renderEditText(newValue: String, myEditText: EditText, hintView: TextView) {
        if (newValue.isNotEmpty()) {
            myEditText.setBackgroundResource(R.drawable.edit_border_active)
            hintView.isVisible = true
        } else {
            myEditText.setBackgroundResource(
                R.drawable.edit_border
            )
            hintView.isVisible = false
        }
    }
    companion object {
        private const val ARGS_PLAYLIST_ID = "playlist_id"
        fun createArgs(playlistId: Int): Bundle =
            bundleOf(ARGS_PLAYLIST_ID to playlistId)
    }
}