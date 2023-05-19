package com.example.resturantproject.model

import com.google.firebase.firestore.DocumentId
import java.util.*


data class Meal(
    @DocumentId
    var id: String,
    var name: String,
    var image: String?,
    var details: String,
    var price: Double,
    var rate: Float,
) {
    constructor() : this("","","", "", 0.0, 0.0f)
}