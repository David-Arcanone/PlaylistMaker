package com.practicum.playlistmaker.search.data.dto

class ITunesSearchResponse (val searchType: String,
                             val expression: String,
                             val results: Array<TrackDto>): Response()