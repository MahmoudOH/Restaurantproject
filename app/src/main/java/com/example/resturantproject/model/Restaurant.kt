package com.example.resturantproject.model

import com.google.firebase.firestore.DocumentId

data class Restaurant(
    @DocumentId
    var id: String,
    var name: String,
    var image: String?,
    var about: String,
    var rate: Float,
) {
    constructor() : this("", "", "", "", 0.0f)
}