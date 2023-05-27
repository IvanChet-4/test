package com.app.playlistmaker

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
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

enum class ResponseState{
    ACCESS,
    NOT_FOUND,
    ERRORS
}

class SearchActivity : AppCompatActivity() {
    companion object {
        private val SEARCH_USER_INPUT = "SEARCH_USER_INPUT"
        const val API_URL = "https://itunes.apple.com"
        const val QUERY = "query"
        const val TRACKS = "tracks"
        const val PREFS = "prefs"
        const val TRACKS_LIST = "tracks_list"
        const val RESPONSE_STATE = "response_state"
    }

    var tracksList = ArrayList<Track>()

    private lateinit var inputEditText: EditText
    private lateinit var clearButton: ImageView
    private lateinit var errorsLayout: LinearLayout
    private lateinit var noInternetText: TextView
    private lateinit var noInternetIcon: ImageView
    private lateinit var notFoundText: TextView
    private lateinit var notFoundIcon: ImageView
    private lateinit var refreshButton: Button
    private lateinit var recycler: RecyclerView
    private lateinit var trackAdapter: TrackAdapter

    private val retrofit = Retrofit.Builder()
        .baseUrl(API_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val itunesService = retrofit.create(TunesApiForRequests::class.java)


    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        val inputEditText = findViewById<EditText>(R.id.inputEditText)
        val clearButton = findViewById<ImageView>(R.id.clearIcon)
        val arrowButton = findViewById<ImageView>(R.id.title_find_to_home)
        errorsLayout = findViewById(R.id.errors_layout)
        notFoundIcon = findViewById(R.id.no_found_image)
        notFoundText = findViewById(R.id.no_found_text)
        noInternetIcon = findViewById(R.id.no_internet_image)
        noInternetText = findViewById(R.id.no_internet_text)
        refreshButton = findViewById(R.id.refresh_button)

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
                 //   search(inputEditText.text.toString())
                }
            }
            false
        }

        val sharedPref = getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        val searchQuery = sharedPref.getString(QUERY, "")
        val json = sharedPref.getString(TRACKS_LIST, ResponseState.ACCESS.name)
        val state = ResponseState.valueOf(sharedPref.getString(RESPONSE_STATE, ResponseState.ACCESS.name)?: ResponseState.ACCESS.name)
        inputEditText.setText(searchQuery)





        val recycler = findViewById<RecyclerView>(R.id.search_track)
        val listTrack: ArrayList<Track> =
            arrayListOf(Track("Smells Like Teen Spirit", "Nirvana", 5*60*1000+1000, "https://is5-ssl.mzstatic.com/image/thumb/Music115/v4/7b/58/c2/7b58c21a-2b51-2bb2-e59a-9bb9b96ad8c3/00602567924166.rgb.jpg/100x100bb.png"),
                Track("Billie Jean", "Michael Jackson", 4*60*1000+35000, "https://is5-ssl.mzstatic.com/image/thumb/Music125/v4/3d/9d/38/3d9d3811-71f0-3a0e-1ada-3004e56ff852/827969428726.jpg/100x100bb.png"),
                Track("Stayin' Alive", "Bee Gees", 4*60*1000+10000, "https://is4-ssl.mzstatic.com/image/thumb/Music115/v4/1f/80/1f/1f801fc1-8c0f-ea3e-d3e5-387c6619619e/16UMGIM86640.rgb.jpg/100x100bb.png"),
                Track("Whole Lotta Love", "Led Zeppelin", 5*60*1000+33000, "https://is2-ssl.mzstatic.com/image/thumb/Music62/v4/7e/17/e3/7e17e33f-2efa-2a36-e916-7f808576cf6b/mzm.fyigqcbs.jpg/100x100bb.png"),
                Track("Sweet Child O'Mine", "Guns N' Roses", 5*60*1000+3000, "https://is5-ssl.mzstatic.com/image/thumb/Music125/v4/a0/4d/c4/a04dc484-03cc-02aa-fa82-5334fcb4bc16/18UMGIM24878.rgb.jpg/100x100bb.jpg"))
        recycler.layoutManager = LinearLayoutManager(this)
        recycler.adapter = TrackAdapter(listTrack)
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

    @RequiresApi(Build.VERSION_CODES.M)
    fun isOnline(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val capabilities =
            connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
        if (capabilities != null) {
            if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                Log.i("Internet", "NetworkCapabilities.TRANSPORT_CELLULAR")
                return true
            } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                Log.i("Internet", "NetworkCapabilities.TRANSPORT_WIFI")
                return true
            } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
                Log.i("Internet", "NetworkCapabilities.TRANSPORT_ETHERNET")
                return true
            }
        }
        return false
    }



//    private fun search() {
//        forecaService.getLocations("Bearer $token", queryInput.text.toString())
//            .enqueue(object : Callback<TracksSearchResponse> {
//                override fun onResponse(
//                    call: Call<LocationsResponse>,
//                    response: Response<LocationsResponse>
//                ) {
//                    when (response.code()) {
//                        200 -> {
//                            if (response.body()?.locations?.isNotEmpty() == true) {
//                                locations.clear()
//                                locations.addAll(response.body()?.locations!!)
//                                adapter.notifyDataSetChanged()
//                                showMessage("", "")
//                            } else {
//                                showMessage(getString(R.string.nothing_found), "")
//                            }
//
//                        }
//
//                        401 -> authenticate()
//                        else -> showMessage(
//                            getString(R.string.something_went_wrong),
//                            response.code().toString()
//                        )
//                    }
//
//                }
//
//                override fun onFailure(call: Call<LocationsResponse>, t: Throwable) {
//                    showMessage(getString(R.string.something_went_wrong), t.message.toString())
//                }
//
//            })
//    }

}
