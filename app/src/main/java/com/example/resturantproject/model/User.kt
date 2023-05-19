package com.example.resturantproject.model

import com.google.firebase.firestore.DocumentId
import java.util.Date

data class User(
    @DocumentId
    var id: String,
    var email: String,
    var password: String,
    var fullname: String?,
    var birthdate: Date?,
    var img: String?
){
    constructor() : this("","","", "", Date(), "")
}