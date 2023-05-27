package com.app.playlistmaker


data class SearchResponse(
    val resultCount: Int,
    val results: ArrayList<Track>
)