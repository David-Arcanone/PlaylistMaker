package com.practicum.playlistmaker.data.dto

import com.practicum.playlistmaker.domain.models.Track



class ITunesSearchResponse (val searchType: String,
                             val expression: String,
                             val results: Array<TrackDto>):Response()