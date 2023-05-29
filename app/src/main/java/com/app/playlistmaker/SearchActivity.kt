package com.app.playlistmaker

import android.annotation.SuppressLint
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.ArrayList

class SearchActivity : AppCompatActivity() {

    private lateinit var inputEditText: EditText
    private lateinit var clearButton: ImageView
    private lateinit var arrowButton: ImageView
    private lateinit var refreshButton: Button
    private lateinit var recycler: RecyclerView
    private lateinit var notFoundImage: ImageView
    private lateinit var notFoundText: TextView
    private lateinit var noInternetImage: ImageView
    private lateinit var noInternetText: TextView

    companion object {
        private val SEARCH_USER_INPUT = "SEARCH_USER_INPUT"
        const val API_URL = "https://itunes.apple.com"
    }

    var tracksList = ArrayList<Track>()

    private val retrofit = Retrofit.Builder()
        .baseUrl(API_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val iTunesService = retrofit.create(iTunesApiForRequests::class.java)

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        inputEditText = findViewById(R.id.inputEditText)
        clearButton = findViewById(R.id.clearIcon)
        arrowButton = findViewById(R.id.title_find_to_home)
        refreshButton = findViewById(R.id.refresh_button)
        recycler = findViewById(R.id.search_track)
        notFoundImage = findViewById(R.id.not_found_image)
        notFoundText = findViewById(R.id.not_found_text)
        noInternetImage = findViewById(R.id.no_internet_image)
        noInternetText = findViewById(R.id.no_internet_text)

        val trackAdapter = TrackAdapter(tracksList)
        recycler.layoutManager = LinearLayoutManager(this)
        recycler.adapter = trackAdapter

        inputEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                if (inputEditText.text.isNotEmpty()) {
                    search(inputEditText.text.toString())
                }
            }
            false
        }

        clearButton.setOnClickListener {
            val keyboard = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            keyboard.hideSoftInputFromWindow(inputEditText.windowToken, 0)
            inputEditText.clearFocus()
            inputEditText.setText("")
            tracksList.clear()
            recycler.removeAllViewsInLayout()
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



//        inputEditText.setOnEditorActionListener { _, actionId, _ ->
//            if (actionId == EditorInfo.IME_ACTION_DONE) {
//                if (inputEditText.text.isNotEmpty()) {
//                    iTunesService.search(inputEditText.text.toString())
//                        .enqueue(object : Callback<TracksSearchResponse> {
//
//                            @SuppressLint("NotifyDataSetChanged")
//                            override fun onResponse(
//                                call: Call<TracksSearchResponse>,
//                                response: Response<TracksSearchResponse>
//                            ) {
//                                if (response.code() == 200 && response.body()?.results?.isNotEmpty() == true) {
//                                    tracksList.clear()
//                                    recycler.visibility = View.VISIBLE
//                                    refreshButton.visibility = View.GONE
//                                    notFoundImage.visibility = View.GONE
//                                    notFoundText.visibility = View.GONE
//                                    noInternetImage.visibility = View.GONE
//                                    noInternetText.visibility = View.GONE
//                                    refreshButton.visibility = View.GONE
//                                    tracksList.addAll(response.body()?.results!!)
//                                    trackAdapter.notifyDataSetChanged()
//                                } else if (response.code() == 200 && trackAdapter.tracks.isEmpty()) {
//                                    notFoundImage.visibility = View.VISIBLE
//                                    notFoundText.visibility = View.VISIBLE
//                                    recycler.visibility = View.GONE
//                                    noInternetImage.visibility = View.GONE
//                                    noInternetText.visibility = View.GONE
//                                    refreshButton.visibility = View.GONE
//                                    trackAdapter.notifyDataSetChanged()
//                                }
//                                else {
//                                    noInternetImage.visibility = View.VISIBLE
//                                    noInternetText.visibility = View.VISIBLE
//                                    refreshButton.visibility = View.VISIBLE
//                                    notFoundImage.visibility = View.GONE
//                                    notFoundText.visibility = View.GONE
//                                    recycler.visibility = View.GONE
//                                    refreshButton.setOnClickListener { search(inputEditText.text.toString()) }
//                                    trackAdapter.notifyDataSetChanged()
//                                }
//                            }
//
//                            override fun onFailure(call: Call<TracksSearchResponse>, t: Throwable) {
//                                noInternetImage.visibility = View.VISIBLE
//                                noInternetText.visibility = View.VISIBLE
//                                refreshButton.visibility = View.VISIBLE
//                                notFoundImage.visibility = View.GONE
//                                notFoundText.visibility = View.GONE
//                                recycler.visibility = View.GONE
//                                refreshButton.setOnClickListener { search(inputEditText.text.toString()) }
//                            }
//                        })
//                }
//                true
//            }
//            false
//        }
//
//        true

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

    private fun search(searchQuery: String) {
        tracksList.clear()
        val trackAdapter = TrackAdapter(tracksList)

        notFoundImage.visibility = View.GONE
        notFoundText.visibility = View.GONE
        noInternetImage.visibility = View.GONE
        noInternetText.visibility = View.GONE
        refreshButton.visibility = View.GONE

        iTunesService.search(searchQuery).enqueue(object : Callback<TracksSearchResponse> {
                override fun onResponse(
                    call: Call<TracksSearchResponse>,
                    response: Response<TracksSearchResponse>
                ) {
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
                    } else if (response.code() == 200 && trackAdapter.tracks.isEmpty()) {
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
                    refreshButton.setOnClickListener { search(searchQuery) }
                }
            })
    }
}