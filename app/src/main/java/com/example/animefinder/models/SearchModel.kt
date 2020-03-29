package com.example.animefinder.models

import com.apollographql.apollo.api.Input
import com.apollographql.apollo.api.toInput
import type.MediaStatus
import java.io.Serializable

data class SearchModel(
    var page: Int,
    var perPage: Int,
    var search: String?,
    var genre: String?,
    var isFromSearch: Boolean,
    var minimumEpisodes: Int?,
    var startDate: String?,
    var status: MediaStatus?
): Serializable