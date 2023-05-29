package com.example.resturantproject.model

import android.os.Parcel
import android.os.Parcelable
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.GeoPoint

data class Restaurant(
    @DocumentId var id: String,
    var name: String,
    var image: String?,
    var about: String,
    var rate: Float,
    var location: GeoPoint?,
) : Parcelable {

    companion object {
        @JvmField
        val CREATOR = object : Parcelable.Creator<Restaurant> {
            override fun createFromParcel(parcel: Parcel) = Restaurant(parcel)
            override fun newArray(size: Int) = arrayOfNulls<Restaurant>(size)
        }
    }

    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readFloat() ?: 0.0f,
        GeoPoint(parcel.readDouble(), parcel.readDouble()),
    )

    constructor() : this("", "", "", "", 0.0f, GeoPoint(0.0, 0.0))

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(name)
        parcel.writeString(image)
        parcel.writeString(about)
        parcel.writeFloat(rate)
        parcel.writeDouble(location?.latitude ?: 0.0)
        parcel.writeDouble(location?.longitude ?: 0.0)
    }

    override fun describeContents() = 0

    fun locationString(): String {
        return "Lat: ${location?.latitude}, Long: ${location?.longitude}"
    }

}