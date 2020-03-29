package com.example.animefinder.models

import java.io.Serializable

data class AnimeListModel(
    var id: Int?,
    var name: String?,
    var imageUrl: String?
): Serializable