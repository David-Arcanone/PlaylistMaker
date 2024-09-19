package com.practicum.playlistmaker.presentation.search

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.creator.Creator
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.domain.api.SearchInteractor
import com.practicum.playlistmaker.domain.consumer.TracksConsumer
import com.practicum.playlistmaker.domain.models.SearchedTracks
import com.practicum.playlistmaker.domain.models.Track
import com.practicum.playlistmaker.presentation.player.PlayerActivity
import com.practicum.playlistmaker.utils.Utilities

class SearchActivity : AppCompatActivity() {
    //инициализация необходимых переменных
    private lateinit var myITunesInteractor: SearchInteractor
    private lateinit var inputEditText: EditText
    private var mainThreadHandler: Handler? = null
    private val trackAdapter = TrackAdapter()
    private val historyTrackAdapter = TrackAdapter()
    private val myTracks = ArrayList<Track>()
    private val myHistoryTracks = ArrayList<Track>()
    private var lastRequest = ""
    private var isSearchAllowed = true
    private val applicationContext = this

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        myITunesInteractor = Creator.provideGetSearchInteractor(
            applicationContext.getSharedPreferences(
                Utilities.PLAYLIST_SAVED_PREFERENCES,
                Context.MODE_PRIVATE
            )
        )
        val btBack = findViewById<Button>(R.id.bt_back)
        inputEditText = findViewById<EditText>(R.id.edit_text_search)
        val btClear = findViewById<ImageButton>(R.id.bt_clear)
        val recyclerView = findViewById<RecyclerView>(R.id.rv_tracks)
        val btRefresh = findViewById<Button>(R.id.bt_refresh)
        val searchErrorBox = findViewById<LinearLayout>(R.id.error_box)
        val searchErrorPic = findViewById<ImageView>(R.id.error_pic)
        val searchNotFoundPic = findViewById<ImageView>(R.id.not_found_pic)
        val searchErrorText = findViewById<TextView>(R.id.error_text)

        val progressBarDownloadMusic = findViewById<ProgressBar>(R.id.progressBar)
        val historyBox = findViewById<LinearLayout>(R.id.history_box)
        val historyRecyclerView = findViewById<RecyclerView>(R.id.rv_history_tracks)
        val btHistoryClear = findViewById<Button>(R.id.bt_clear_history)

        myHistoryTracks.addAll(myITunesInteractor.getSavedTrackHistory())
        mainThreadHandler = Handler(Looper.getMainLooper())
        trackAdapter.tracks = myTracks
        historyTrackAdapter.tracks = myHistoryTracks

        fun popupManager(currentMod: Int) {
            ///режимы: 0-очистка, 1-заглушка "нет совпадений", 2-заглушка "ошибка",
            when (currentMod) {
                CLEAR_WINDOW -> {//чистый экран
                    mainThreadHandler?.post {
                        progressBarDownloadMusic.isVisible = false
                        searchErrorBox.isVisible = false
                        historyBox.isVisible = false
                    }
                    trackAdapter.notifyDataSetChanged()
                }

                ERROR_NO_MATCHES -> {//нет сопадений
                    mainThreadHandler?.post {
                        progressBarDownloadMusic.isVisible = false
                        searchErrorBox.isVisible = true
                        historyBox.isVisible = false
                        btRefresh.isVisible = false
                        searchErrorPic.isVisible = false
                        searchNotFoundPic.isVisible = true
                        searchErrorText.setText(R.string.search_error_not_found)
                    }
                    trackAdapter.notifyDataSetChanged()
                }

                ERROR_CONNECTION -> {//ошибка связи
                    mainThreadHandler?.post {
                        progressBarDownloadMusic.isVisible = false
                        searchErrorBox.isVisible = true
                        historyBox.isVisible = false
                        btRefresh.isVisible = true
                        searchErrorPic.isVisible = true
                        searchNotFoundPic.isVisible = false
                        searchErrorText.setText(R.string.search_error_connection)
                    }
                    lastRequest = inputEditText.text.toString()
                    trackAdapter.notifyDataSetChanged()
                }

                LOADING_SEARCH -> {//загрузка поиска
                    mainThreadHandler?.post {
                        progressBarDownloadMusic.isVisible = true
                    }
                }

                FINISHED_LOAD -> {
                    mainThreadHandler?.post {
                        progressBarDownloadMusic.isVisible = false
                    }
                }
            }
        }

        // makeSearch запуск поисковых запросов
        fun makeSearch(text: String) {
            //очищаем поисковый список
            myTracks.clear()
            popupManager(CLEAR_WINDOW)
            popupManager(LOADING_SEARCH)
            myITunesInteractor.searchTracks(text,
                consumer = object : TracksConsumer<SearchedTracks> {
                    override fun consume(foundTracks: SearchedTracks) {
                        mainThreadHandler?.post {//проверим что к моменту завершения поиска актуальность запроса не пропала
                            if(inputEditText.text.toString()==text){
                                if (!foundTracks.isSucceded) {//неудачный ответ от сервера
                                    popupManager(ERROR_CONNECTION)
                                } else if (foundTracks.tracks.isNotEmpty()) {//удачное соединение и есть треки
                                    popupManager(FINISHED_LOAD)
                                    myTracks.addAll(foundTracks.tracks)
                                    trackAdapter.notifyDataSetChanged()
                                } else {//удачное соединение но нет совпадений
                                    popupManager(ERROR_NO_MATCHES)
                                }
                            } else{
                                popupManager(FINISHED_LOAD)//не изменяем, тк не актуально
                            }
                        }
                    }
                })
        }

        val mySearchRunnable = Runnable {
            if (inputEditText.text.isNotEmpty()) makeSearch(inputEditText.text.toString())
        }

        fun clickDebounce(): Boolean {
            val current = isSearchAllowed
            if (isSearchAllowed) {
                isSearchAllowed = false
                mainThreadHandler?.postDelayed({ isSearchAllowed = true }, SEARCH_DEBOUNCE_DELAY)
            }
            return current
        }
        if (savedInstanceState != null) {
            val myText = savedInstanceState.getString(EDIT_VALUE, EDIT_DEFAULT).toString()
            inputEditText.setText(myText)
        }
        //Кнопка назад
        btBack.setOnClickListener {
            finish()//возвращаемся назад в меню
        }
        //Кнопка очистка поля ввода
        btClear.setOnClickListener {
            inputEditText.setText("")
            val view = this.currentFocus
            val inputMethodManager =
                getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            if (view != null) {
                inputMethodManager?.hideSoftInputFromWindow(view.windowToken, 0)
            }
            inputEditText.clearFocus()
            mainThreadHandler?.removeCallbacks(mySearchRunnable)
            myTracks.clear()
            trackAdapter.notifyDataSetChanged()
        }
        //кнопка обновить запрос (в случае если проблемы с загрузкой)
        btRefresh.setOnClickListener {
            //Кнопка обновить запускает последний запрос
            if (lastRequest.isNotEmpty()) makeSearch(lastRequest)
        }
        /*логика поля ввода поиска*/
        val myTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // пока небыло задания
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val isEmptyText= s?.isNullOrEmpty()==true
                historyBox.isVisible =
                    (inputEditText.hasFocus() && isEmptyText && myHistoryTracks.isNotEmpty())
                btClear.isVisible = !isEmptyText
                //2 sec задержка перед запуском поиска
                mainThreadHandler?.removeCallbacks(mySearchRunnable)
                if(isEmptyText){//очистим результаты аналогично кнопке очистить, но без изменения фокуса
                    progressBarDownloadMusic.isVisible=false
                    myTracks.clear()
                    trackAdapter.notifyDataSetChanged()
                } else{
                    mainThreadHandler?.postDelayed(mySearchRunnable, SEARCH_DEBOUNCE_DELAY)
                }
            }

            override fun afterTextChanged(s: Editable?) {
                // пока небыло задания
            }
        }
        inputEditText.addTextChangedListener(myTextWatcher)
        inputEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                //Запуск Поискового Запроса сразу
                if (inputEditText.text.isNotEmpty()) makeSearch(inputEditText.text.toString())
                true
            }
            false
        }
        inputEditText.setOnFocusChangeListener { _, hasFocus ->
            historyBox.isVisible =
                hasFocus && inputEditText.text.isEmpty() && myHistoryTracks.isNotEmpty()
        }
        // обработчик для клика по треку
        val mytrackClicker = { track: Track ->
            if (clickDebounce()) { //проверяем debounce чтобы случайно не открыть подряд два плеера
                val positionOfDublicateInHistory =
                    myHistoryTracks.indexOfFirst{ it.trackId == track.trackId }
                if (positionOfDublicateInHistory != -1) {
                    myHistoryTracks.removeAt(positionOfDublicateInHistory)
                } else if (myHistoryTracks.size >= 10) {
                    myHistoryTracks.removeAt(9)
                }
                myHistoryTracks.add(0, track)
                myITunesInteractor.saveTrackHistory(myHistoryTracks)
                myITunesInteractor.saveCurrentTrack(track)
                historyTrackAdapter.notifyDataSetChanged()
                val playerIntent = Intent(this, PlayerActivity::class.java)
                startActivity(playerIntent)
            }
        }

        //RecyclerView для результатов поиска
        recyclerView.adapter = trackAdapter
        trackAdapter.setOnClickListener(mytrackClicker)

        //historyRecyclerView
        historyRecyclerView.adapter = historyTrackAdapter
        historyTrackAdapter.setOnClickListener(mytrackClicker)

        //кнопка очистки трека
        btHistoryClear.setOnClickListener {
            myHistoryTracks.clear()
            myITunesInteractor.saveTrackHistory(myHistoryTracks)
            historyTrackAdapter.notifyDataSetChanged()
            historyBox.isVisible = false
        }
        inputEditText.requestFocus()

    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        val myText = inputEditText.text.toString()
        outState.putString(EDIT_VALUE, myText)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        //проверю что интерактор инициализирован
        if (myITunesInteractor == null) myITunesInteractor =
            Creator.provideGetSearchInteractor(
                applicationContext.getSharedPreferences(
                    Utilities.PLAYLIST_SAVED_PREFERENCES,
                    Context.MODE_PRIVATE
                )
            )
        val myText = savedInstanceState.getString(EDIT_VALUE,EDIT_DEFAULT).toString()
        inputEditText.setText(myText)
        myHistoryTracks.clear()
        myHistoryTracks.addAll(myITunesInteractor.getSavedTrackHistory())
    }
    private companion object {
        const val SEARCH_DEBOUNCE_DELAY = 2000L
        const val CLEAR_WINDOW = 0
        const val ERROR_CONNECTION = 2
        const val ERROR_NO_MATCHES = 1
        const val LOADING_SEARCH = 3
        const val FINISHED_LOAD = 4
        const val EDIT_VALUE = "EDIT_VALUE"
        const val EDIT_DEFAULT = ""

    }
}