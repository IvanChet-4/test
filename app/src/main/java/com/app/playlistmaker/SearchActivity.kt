package com.app.playlistmaker

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.NetworkInfo
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.EditText
import android.view.View
import android.view.View.GONE
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.ArrayList

class SearchActivity : AppCompatActivity() {
    companion object {
        private val SEARCH_USER_INPUT = "SEARCH_USER_INPUT"
        const val API_URL = "https://itunes.apple.com"
    }

    var tracksList = ArrayList<Track>()

    private val retrofit = Retrofit.Builder()
        .baseUrl(API_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val iTunesService = retrofit.create(TunesApiForRequests::class.java)


    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        val inputEditText = findViewById<EditText>(R.id.inputEditText)
        val clearButton = findViewById<ImageView>(R.id.clearIcon)
        val arrowButton = findViewById<ImageView>(R.id.title_find_to_home)
        val refreshButton = findViewById<Button>(R.id.refresh_button)
        val recycler = findViewById<RecyclerView>(R.id.search_track)
        val notFoundIcon = findViewById<ImageView>(R.id.no_found_image)
        val notFoundText = findViewById<TextView>(R.id.no_found_text)
        val noInternetIcon = findViewById<ImageView>(R.id.no_internet_image)
        val noInternetText = findViewById<TextView>(R.id.no_internet_text)

        val trackAdapter = TrackAdapter(tracksList)

        recycler.layoutManager = LinearLayoutManager(this)
        recycler.adapter = trackAdapter


        clearButton.setOnClickListener {
            val keyboard = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            keyboard.hideSoftInputFromWindow(inputEditText.windowToken, 0)
            inputEditText.clearFocus()
            inputEditText.setText("")
        }

        arrowButton.setOnClickListener { finish() }

        val simpleTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // empty
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                clearButton.visibility = clearButtonVisibility(s)
            }

            override fun afterTextChanged(s: Editable?) { // empty
            }
        }
        inputEditText.addTextChangedListener(simpleTextWatcher)


        inputEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                if (inputEditText.text.isNotEmpty()) {
                    iTunesService.search(inputEditText.text.toString())
                        .enqueue(object : Callback<TracksSearchResponse> {

                            @SuppressLint("NotifyDataSetChanged")
                            override fun onResponse(
                                call: Call<TracksSearchResponse>,
                                response: Response<TracksSearchResponse>
                            ) {
                                if (response.code() == 200) {
                                    tracksList.clear()
                                    recycler.visibility = View.VISIBLE
                                    refreshButton.visibility = View.GONE
                                    if (response.body()?.results?.isNotEmpty() == true) {
                                        tracksList.addAll(response.body()?.results!!)
                                        trackAdapter.notifyDataSetChanged()
                                    }
                                    if (trackAdapter.tracks.isEmpty()) {
                                        notFoundIcon.visibility = View.VISIBLE
                                        notFoundText.visibility = View.VISIBLE
                                        noInternetIcon.visibility = View.GONE
                                        noInternetText.visibility = View.GONE
                                        refreshButton.visibility = View.GONE
                                        trackAdapter.notifyDataSetChanged()
                                    }
                                } else {
                                    noInternetIcon.visibility = View.VISIBLE
                                    noInternetText.visibility = View.VISIBLE
                                    refreshButton.visibility = View.VISIBLE
                                    notFoundIcon.visibility = View.GONE
                                    notFoundText.visibility = View.GONE
                                    recycler.visibility = View.GONE
                                    refreshButton.setOnClickListener { search(inputEditText) }
                                    trackAdapter.notifyDataSetChanged()
                                }
                            }

                            override fun onFailure(call: Call<TracksSearchResponse>, t: Throwable) {
                                noInternetIcon.visibility = View.VISIBLE
                                noInternetText.visibility = View.VISIBLE
                                refreshButton.visibility = View.VISIBLE
                                recycler.visibility = View.GONE
                                refreshButton.setOnClickListener { search(inputEditText) }
                            }
                        })
                }
                true
            }
            false
        }




//        val recycler = findViewById<RecyclerView>(R.id.search_track)
//        val listTrack: ArrayList<Track> =
//            arrayListOf(Track("Smells Like Teen Spirit", "Nirvana", 5*60*1000+1000, "https://is5-ssl.mzstatic.com/image/thumb/Music115/v4/7b/58/c2/7b58c21a-2b51-2bb2-e59a-9bb9b96ad8c3/00602567924166.rgb.jpg/100x100bb.png"),
//                Track("Billie Jean", "Michael Jackson", 4*60*1000+35000, "https://is5-ssl.mzstatic.com/image/thumb/Music125/v4/3d/9d/38/3d9d3811-71f0-3a0e-1ada-3004e56ff852/827969428726.jpg/100x100bb.png"),
//                Track("Stayin' Alive", "Bee Gees", 4*60*1000+10000, "https://is4-ssl.mzstatic.com/image/thumb/Music115/v4/1f/80/1f/1f801fc1-8c0f-ea3e-d3e5-387c6619619e/16UMGIM86640.rgb.jpg/100x100bb.png"),
//                Track("Whole Lotta Love", "Led Zeppelin", 5*60*1000+33000, "https://is2-ssl.mzstatic.com/image/thumb/Music62/v4/7e/17/e3/7e17e33f-2efa-2a36-e916-7f808576cf6b/mzm.fyigqcbs.jpg/100x100bb.png"),
//                Track("Sweet Child O'Mine", "Guns N' Roses", 5*60*1000+3000, "https://is5-ssl.mzstatic.com/image/thumb/Music125/v4/a0/4d/c4/a04dc484-03cc-02aa-fa82-5334fcb4bc16/18UMGIM24878.rgb.jpg/100x100bb.jpg"))
//        recycler.layoutManager = LinearLayoutManager(this)
//        recycler.adapter = TrackAdapter(listTrack)
    }



    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        val inputEditText = findViewById<EditText>(R.id.inputEditText)
        super.onSaveInstanceState(outState)
        outState.putString(SEARCH_USER_INPUT, inputEditText.text.toString())
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        savedInstanceState.getString(SEARCH_USER_INPUT, "")
    }

    private fun clearButtonVisibility(s: CharSequence?): Int {
        return if (s.isNullOrEmpty()) {
            View.GONE
        } else {
            View.VISIBLE
        }
    }


    fun search(inputEditText: EditText) {
        tracksList.clear()

        val refreshButton = findViewById<Button>(R.id.refresh_button)
        val trackAdapter = TrackAdapter(tracksList)
        val notFoundImage: ImageView = findViewById(R.id.no_found_image)
        val notFoundText: TextView = findViewById(R.id.no_found_text)
        val noInternetImage: ImageView = findViewById(R.id.no_internet_image)
        val noInternetText: TextView = findViewById(R.id.no_internet_text)
        val recycler = findViewById<RecyclerView>(R.id.search_track)

        notFoundImage.visibility = View.GONE
        notFoundText.visibility = View.GONE
        noInternetImage.visibility = View.GONE
        noInternetText.visibility = View.GONE
        refreshButton.visibility = View.GONE

        iTunesService.search(inputEditText.text.toString())
            .enqueue(object : Callback<TracksSearchResponse> {
                @SuppressLint("NotifyDataSetChanged")
                override fun onResponse(
                    call: Call<TracksSearchResponse>,
                    response: Response<TracksSearchResponse>
                ) {
                    if (response.code() != 200) {
                        noInternetImage.visibility = View.VISIBLE
                        noInternetText.visibility = View.VISIBLE
                        refreshButton.visibility = View.VISIBLE
                        notFoundImage.visibility = View.GONE
                        notFoundText.visibility = View.GONE
                        recycler.visibility = View.GONE
                        refreshButton.setOnClickListener { search(inputEditText) }
                        //trackAdapter.notifyDataSetChanged()
                    }
                    if (response.code() == 200 && response.body()?.results?.isNotEmpty() == true) {
                        tracksList.clear()
                        recycler.visibility = View.VISIBLE
                        refreshButton.visibility = View.GONE
                        notFoundImage.visibility = View.GONE
                        notFoundText.visibility = View.GONE
                        noInternetImage.visibility = View.GONE
                        noInternetText.visibility = View.GONE
                        refreshButton.visibility = View.GONE
                        tracksList.addAll(response.body()?.results!!)
                        trackAdapter.notifyDataSetChanged()
                        }
                    if (response.code() == 200 && trackAdapter.tracks.isEmpty()) {
                            notFoundImage.visibility = View.VISIBLE
                            notFoundText.visibility = View.VISIBLE
                            recycler.visibility = View.GONE
                            noInternetImage.visibility = View.GONE
                            noInternetText.visibility = View.GONE
                            refreshButton.visibility = View.GONE
                            trackAdapter.notifyDataSetChanged()
                        }
                }

                override fun onFailure(call: Call<TracksSearchResponse>, t: Throwable) {
                    noInternetImage.visibility = View.VISIBLE
                    noInternetText.visibility = View.VISIBLE
                    refreshButton.visibility = View.VISIBLE
                    notFoundImage.visibility = View.GONE
                    notFoundText.visibility = View.GONE
                    recycler.visibility = View.GONE
                    refreshButton.setOnClickListener { search(inputEditText) }
                }
            })
    }
}
