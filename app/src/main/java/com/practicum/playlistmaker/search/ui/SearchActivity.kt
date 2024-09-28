package com.practicum.playlistmaker.search.ui

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.ActivitySearchBinding
import com.practicum.playlistmaker.search.domain.models.Track
import com.practicum.playlistmaker.search.domain.models.SearchState

class SearchActivity : AppCompatActivity() {
    //инициализация необходимых переменных
    private val trackAdapter = TrackAdapter()
    private val historyTrackAdapter = TrackAdapter()
    private val myTracks = mutableListOf<Track>()
    private val myHistoryTracks = mutableListOf<Track>()
    private lateinit var myBinding: ActivitySearchBinding
    private val myViewModel by viewModels<SearchViewModel> { SearchViewModel.getSearchViewModel() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        myBinding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(myBinding.root)

        myViewModel.getSearchLiveData().observe(this) { searchState ->
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

        if (savedInstanceState != null) {
            val myText = savedInstanceState.getString(EDIT_VALUE, EDIT_DEFAULT).toString()
            myBinding.editTextSearch.setText(myText)
            myViewModel.showInputTextChange(myText)
        }

        //Кнопка назад
        myBinding.btBack.setOnClickListener {
            finish()//возвращаемся назад в меню
        }
        //Кнопка очистка поля ввода
        myBinding.btClear.setOnClickListener {
            myBinding.editTextSearch.setText("")
            myViewModel.showInputTextChange("")
            val view = this.currentFocus
            val inputMethodManager =
                getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
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
            myViewModel.showClickOnTrack(track)//добавление в архив
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


    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        val myText = myBinding.editTextSearch.text.toString()
        outState.putString(EDIT_VALUE, myText)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        val myText = savedInstanceState.getString(EDIT_VALUE, EDIT_DEFAULT).toString()
        myBinding.editTextSearch.setText(myText)
        myViewModel.showInputTextChange(myText)
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

    private companion object {
        const val EDIT_VALUE = "EDIT_VALUE"
        const val EDIT_DEFAULT = ""

    }
}
