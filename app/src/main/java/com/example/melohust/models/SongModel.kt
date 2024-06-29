package com.example.melohust.models

import com.google.firebase.Timestamp

data class SongModel(
    val id : String,
    val title : String,
    val subtitle : String,
    val url : String,
    val coverUrl : String,
    var createdTime : Timestamp = Timestamp.now()
) {
    constructor() : this("", "", "", "","")
}
