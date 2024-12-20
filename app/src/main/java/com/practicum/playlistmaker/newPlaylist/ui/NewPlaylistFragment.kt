package com.practicum.playlistmaker.newPlaylist.ui

import android.Manifest
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentNewPlaylistBinding
import com.practicum.playlistmaker.newPlaylist.domain.models.NewPlaylistState
import com.practicum.playlistmaker.utils.AndroidUtilities
import com.practicum.playlistmaker.utils.Utilities
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File
import java.io.FileOutputStream

class NewPlaylistFragment : Fragment() {
    private var numberOfBlocks: Int = 0
    private lateinit var myBinding: FragmentNewPlaylistBinding
    private val myViewModel by viewModel<NewPlaylistViewModel>()
    private var customPicFlag = false
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

        myViewModel.getStateLiveData().observe(viewLifecycleOwner) { initState ->
            when (initState) {
                is NewPlaylistState -> {
                    myBinding.btCreate.isEnabled =
                        if (initState.isVerified) true else false //разрешение на создание
                    if (initState.pictureUri != null) {
                        renderNewPicture(initState.pictureUri)//картинка
                        customPicFlag = true
                    } else customPicFlag = false
                }

                else -> {}
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
            if (myBinding.editNewName.text.toString()
                    .isNotEmpty() || myBinding.editNewDescription.text.toString()
                    .isNotEmpty() || customPicFlag
            ) {
                MaterialAlertDialogBuilder(currentContext).setTitle(R.string.finalize_playlist_creation)
                    .setNegativeButton(R.string.no) { dialog, which -> // «Нет»
                        //ничего не предпринимать
                    }
                    .setPositiveButton(R.string.yes) { dialog, which -> // «Да»
                        myViewModel.clearAll()
                        findNavController().navigateUp()
                    }
                    .show()
            } else {
                myViewModel.clearAll()
                findNavController().navigateUp()
            }
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
        myViewModel.createNewPlaylistClick(
            name = myBinding.editNewName.text.toString(),
            description = myBinding.editNewDescription.text.toString(),
            onFinishCallback = { findNavController().navigateUp() },
            onConflictCallback = {
                Toast.makeText(
                    context,
                    R.string.playlist_with_such_name_already_exists,
                    Toast.LENGTH_SHORT
                ).show()
            },
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

    }

}