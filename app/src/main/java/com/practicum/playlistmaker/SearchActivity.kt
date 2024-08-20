package com.practicum.playlistmaker

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
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create

class SearchActivity : AppCompatActivity() {
    private lateinit var inputEditText: EditText
    private var mainThreadHandler: Handler? = null

    private companion object {
        const val EDIT_VALUE = "EDIT_VALUE"
        const val EDIT_DEFAULT = ""
        const val SEARCH_DEBOUNCE_DELAY = 2000L
        const val CLEAR_WINDOW = 0
        const val ERROR_CONNECTION = 2
        const val ERROR_NO_MATCHES = 1
        const val LOADING_SEARCH = 3
        const val FINISHED_LOAD = 4
    }

    private val trackAdapter = TrackAdapter()
    private val historyTrackAdapter = TrackAdapter()
    private val myTracks = ArrayList<Track>()
    private val myHistoryTracks = ArrayList<Track>()
    private var lastRequest = ""
    private var isSearchAllowed = true


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        val myPref =
            getSharedPreferences(SharedPreferenceManager.PLAYLIST_SAVED_PREFERENCES, MODE_PRIVATE)
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
        myHistoryTracks.addAll(SharedPreferenceManager.getSavedTrackHistory(myPref))
        mainThreadHandler = Handler(Looper.getMainLooper())

        val retrofit = Retrofit.Builder()
            .baseUrl(Utilities.iTunesBaseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val iTunesApiService = retrofit.create<ITunesApiService>()
        trackAdapter.tracks = myTracks
        historyTrackAdapter.tracks = myHistoryTracks
        // popupManager помогает с работой заглушек
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
            synchronized(myTracks) {
                myTracks.clear()
                mainThreadHandler?.post {
                    popupManager(CLEAR_WINDOW)
                    popupManager(LOADING_SEARCH)
                }
                val newThread = Thread {
                    //делаем запрос
                    iTunesApiService
                        .search(text)
                        .enqueue(object : Callback<iTunesSearchResponse> {
                            override fun onResponse(
                                call: Call<iTunesSearchResponse>,
                                response: Response<iTunesSearchResponse>
                            ) {
                                mainThreadHandler?.post { popupManager(FINISHED_LOAD) }
                                if (response.code() == 200) {
                                    myTracks.clear()

                                    if (response.body()?.results?.isNotEmpty() == true) {
                                        //записаны данные и они не null
                                        myTracks.addAll(response.body()?.results!!)
                                        trackAdapter.notifyDataSetChanged()
                                    }
                                    if (myTracks.isEmpty()) {//нет совпадений
                                        mainThreadHandler?.post { popupManager(ERROR_NO_MATCHES) }
                                    } else {//есть совпадения ничего менять не надо
                                    }
                                } else {//произошла ошибка
                                    mainThreadHandler?.post { popupManager(ERROR_CONNECTION) }
                                }
                            }

                            override fun onFailure(call: Call<iTunesSearchResponse>, t: Throwable) {
                                //произошла ошибка
                                myTracks.clear()
                                mainThreadHandler?.post { popupManager(ERROR_CONNECTION) }
                            }
                        })
                }
                newThread.start()

            }
        }

        val mySearchRunnable = Runnable {
            if (inputEditText.text.isNotEmpty()) makeSearch(inputEditText.text.toString())
        }

        fun clickDebounce(): Boolean { //
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
            val backIntent = Intent(this, MainActivity::class.java)
            startActivity(backIntent)
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
            synchronized(myTracks) { myTracks.clear() }
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
                historyBox.isVisible =
                    (inputEditText.hasFocus() && s?.isEmpty() == true && !myHistoryTracks.isEmpty())
                btClear.isVisible = !s.isNullOrEmpty()
                //2 sec задержка перед запуском поиска
                mainThreadHandler?.removeCallbacks(mySearchRunnable)
                mainThreadHandler?.postDelayed(mySearchRunnable, SEARCH_DEBOUNCE_DELAY)
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
                hasFocus && inputEditText.text.isEmpty() && !myHistoryTracks.isEmpty()
        }
        // обработчик для клика по треку
        val mytrackClicker = { track: Track ->
            if (clickDebounce()) { //проверяем debounce чтобы случайно не открыть подряд два плеера
                val positionOfDublicateInHistory =
                    myHistoryTracks.indexOfFirst({ it.trackId == track.trackId })
                if (positionOfDublicateInHistory != -1) {
                    myHistoryTracks.removeAt(positionOfDublicateInHistory)
                } else if (myHistoryTracks.size >= 10) {
                    myHistoryTracks.removeAt(9)
                }
                myHistoryTracks.add(0, track)
                SharedPreferenceManager.saveTrackHistory(myPref, myHistoryTracks)
                SharedPreferenceManager.saveCurrentTrack(myPref, track)
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
            SharedPreferenceManager.saveTrackHistory(myPref, myHistoryTracks)
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
        val myText = savedInstanceState.getString(EDIT_VALUE, EDIT_DEFAULT).toString()
        inputEditText.setText(myText)
        val myPref =
            getSharedPreferences(SharedPreferenceManager.PLAYLIST_SAVED_PREFERENCES, MODE_PRIVATE)
        myHistoryTracks.clear()
        myHistoryTracks.addAll(SharedPreferenceManager.getSavedTrackHistory(myPref))
    }


}