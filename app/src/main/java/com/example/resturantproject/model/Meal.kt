package com.example.resturantproject.model

import android.os.Parcel
import android.os.Parcelable
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore


data class Meal(
    @DocumentId
    var id: String,
    var name: String,
    var image: String?,
    var details: String,
    var price: Double,
    var rate: Float,
    var restaurant: DocumentReference
) : Parcelable {
    companion object {
        @JvmField
        val CREATOR = object : Parcelable.Creator<Meal> {
            override fun createFromParcel(parcel: Parcel) = Meal(parcel)
            override fun newArray(size: Int) = arrayOfNulls<Meal>(size)
        }
    }

    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readDouble(),
        parcel.readFloat(),
        FirebaseFirestore.getInstance().document(parcel.readString() ?: ""),
    )

    constructor() : this("", "", "", "", 0.0, 0.0f, FirebaseFirestore.getInstance().document(""))
    constructor(
        id: String,
        name: String,
        image: String?,
        details: String,
        price: Double,
        rate: Float,
        restaurantId: String
    ) : this(
        id,
        name,
        image,
        details,
        price,
        rate,
        FirebaseFirestore.getInstance().document("restaurants/$restaurantId")
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(name)
        parcel.writeString(image)
        parcel.writeString(details)
        parcel.writeDouble(price)
        parcel.writeFloat(rate)
        parcel.writeString(restaurant.path)
    }

    override fun describeContents() = 0


}