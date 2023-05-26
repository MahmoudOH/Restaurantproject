package com.example.resturantproject.model

import android.os.Parcel
import android.os.Parcelable
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.GeoPoint
import java.util.Date

data class User(
    @DocumentId
    var id: String,
    var email: String,
    var password: String,
    var fullname: String?,
    var birthdate: Date?,
    var location: GeoPoint?,
    var img: String?
) : Parcelable {

    companion object {
        @JvmField
        val CREATOR = object : Parcelable.Creator<User> {
            override fun createFromParcel(parcel: Parcel) = User(parcel)
            override fun newArray(size: Int) = arrayOfNulls<User>(size)
        }
    }

    constructor() : this("", "", "", "", Date(), GeoPoint(0.0, 0.0), "")

    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString(),
        parcel.readSerializable() as? Date?,
        GeoPoint(parcel.readDouble(), parcel.readDouble()),
        parcel.readString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(email)
        parcel.writeString(password)
        parcel.writeString(fullname)
        parcel.writeSerializable(birthdate)
        parcel.writeDouble(location?.latitude ?: 0.0)
        parcel.writeDouble(location?.longitude ?: 0.0)
        parcel.writeString(img)
    }

    override fun describeContents() = 0

    fun locationString(): String {
        return "Lat: ${location?.latitude}, Long: ${location?.longitude}"
    }
}