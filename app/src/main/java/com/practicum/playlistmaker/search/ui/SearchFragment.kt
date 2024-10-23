package com.practicum.playlistmaker.search.ui

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentSearchBinding
import com.practicum.playlistmaker.search.domain.models.Track
import com.practicum.playlistmaker.search.domain.models.SearchState
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchFragment : Fragment() {
    //инициализация необходимых переменных
    private val trackAdapter = TrackAdapter()
    private val historyTrackAdapter = TrackAdapter()
    private val myTracks = mutableListOf<Track>()
    private val myHistoryTracks = mutableListOf<Track>()
    private lateinit var myBinding: FragmentSearchBinding
    private val myViewModel by viewModel<SearchViewModel>()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        myBinding = FragmentSearchBinding.inflate(inflater, container, false)
        return myBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //аргументов нет но если добавлю, буду сдесь доставать
        myViewModel.getSearchLiveData().observe(viewLifecycleOwner) { searchState ->
            when (searchState) {
                is SearchState.Default -> {
                    renderDefault()
                }

                is SearchState.Loading -> {
                    renderLoading()
                }

                is SearchState.NoMatch -> {
                    renderNoMatchError()
                }

                is SearchState.NoConnection -> {
                    renderConnectionError()
                }

                is SearchState.ReadyAndHistory -> {
                    renderHistory(searchState.historyTracks)
                }

                is SearchState.FinishedSearch -> {
                    renderFinishedSearch(searchState.searchedTracks)
                }
            }
        }
        trackAdapter.tracks = myTracks
        historyTrackAdapter.tracks = myHistoryTracks

        //Кнопка очистка поля ввода
        myBinding.btClear.setOnClickListener {
            myBinding.editTextSearch.setText("")
            myViewModel.showInputTextChange("")
            val view = activity?.currentFocus
            val inputMethodManager = this.requireContext()
                .getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            if (view != null) {
                inputMethodManager?.hideSoftInputFromWindow(view.windowToken, 0)
            }
            myBinding.editTextSearch.clearFocus()
        }
        //кнопка обновить запрос (в случае если проблемы с загрузкой)
        myBinding.btRefresh.setOnClickListener {
            myViewModel.showRefreshSearch()//Кнопка обновить запускает последний запрос
        }
        /*логика поля ввода поиска*/
        val myTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // пока небыло задания
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val isEmptyText = s?.isNullOrEmpty() == true
                myBinding.btClear.isVisible = !isEmptyText
                myViewModel.showInputTextChange(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {
                // пока небыло задания
            }
        }
        myBinding.editTextSearch.addTextChangedListener(myTextWatcher)
        myBinding.editTextSearch.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                //Запуск Поискового Запроса сразу
                if (myBinding.editTextSearch.text.isNotEmpty()) myViewModel.makeSearch(myBinding.editTextSearch.text.toString())
                true
            }
            false
        }
        myBinding.editTextSearch.setOnFocusChangeListener { _, hasFocus ->
            myBinding.historyBox.isVisible =
                hasFocus && myBinding.editTextSearch.text.isEmpty() && myHistoryTracks.isNotEmpty()
        }
        // обработчик для клика по треку
        val mytrackClicker = { track: Track ->
            myViewModel.showClickOnTrack(newTrack = track,//добавление в архив
                fragmentOpener = {
                    findNavController().navigate(R.id.action_searchFragment_to_playerFragment)
                })
        }
        //RecyclerView для результатов поиска
        myBinding.rvTracks.adapter = trackAdapter
        trackAdapter.setOnClickListener(mytrackClicker)

        //historyRecyclerView
        myBinding.rvHistoryTracks.adapter = historyTrackAdapter
        historyTrackAdapter.setOnClickListener(mytrackClicker)

        //кнопка очистки трека
        myBinding.btClearHistory.setOnClickListener {
            myViewModel.showClearHistory()
        }
        myBinding.editTextSearch.requestFocus()

    }

    private fun renderLoading() {
        myBinding.progressBar.isVisible = true
        myBinding.errorBox.isVisible = false
        myBinding.historyBox.isVisible = false
        myBinding.rvTracks.isVisible = false//скрыть результаты если начинается поиск
    }

    private fun renderConnectionError() {
        myBinding.progressBar.isVisible = false
        myBinding.errorBox.isVisible = true
        myBinding.historyBox.isVisible = false
        myBinding.btRefresh.isVisible = true
        myBinding.errorPic.isVisible = true
        myBinding.notFoundPic.isVisible = false
        myBinding.errorText.setText(R.string.search_error_connection)
        myBinding.rvTracks.isVisible = false
        trackAdapter.notifyDataSetChanged()
    }

    private fun renderNoMatchError() {
        myBinding.progressBar.isVisible = false
        myBinding.errorBox.isVisible = true
        myBinding.historyBox.isVisible = false
        myBinding.btRefresh.isVisible = false
        myBinding.errorPic.isVisible = false
        myBinding.notFoundPic.isVisible = true
        myBinding.errorText.setText(R.string.search_error_not_found)
        myBinding.rvTracks.isVisible = false
        trackAdapter.notifyDataSetChanged()
    }

    private fun renderHistory(historyTracks: List<Track>) {
        myBinding.progressBar.isVisible = false
        myBinding.errorBox.isVisible = false
        myBinding.rvTracks.isVisible = false
        if (historyTracks.isNotEmpty() && myBinding.editTextSearch.hasFocus()) {//возможны случаи когда пустая или без фокуса
            myBinding.historyBox.isVisible = true
        } else {
            myBinding.historyBox.isVisible = false
        }
        myHistoryTracks.clear()
        myHistoryTracks.addAll(historyTracks)
        historyTrackAdapter.notifyDataSetChanged()//вне if, тк фокус может меняться
    }

    private fun renderFinishedSearch(resultTracks: List<Track>) {
        myBinding.progressBar.isVisible = false
        myBinding.historyBox.isVisible = false
        myBinding.errorBox.isVisible = false
        myBinding.rvTracks.isVisible = true
        myTracks.clear()
        myTracks.addAll(resultTracks)
        trackAdapter.notifyDataSetChanged()
    }

    private fun renderDefault() {//пустой экран
        myBinding.progressBar.isVisible = false
        myBinding.historyBox.isVisible = false
        myBinding.errorBox.isVisible = false
        myBinding.rvTracks.isVisible = false
    }

    companion object {
        const val EDIT_VALUE = "EDIT_VALUE"
        const val EDIT_DEFAULT = ""
    }
}
