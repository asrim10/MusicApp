package com.example.musicapp.model

import android.os.Parcel
import android.os.Parcelable

data class SongModel(
    var songId: String = "",
    var title: String,
    var artist: String,
    var imageUrl: String,
    var audioUrl : String
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?:"",
        parcel.readString() ?:"",
        parcel.readString() ?:"",
        parcel.readString() ?:"",
        parcel.readString() ?:"",
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(songId)
        parcel.writeString(title)
        parcel.writeString(artist)
        parcel.writeString(imageUrl)
        parcel.writeString(audioUrl)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<SongModel> {
        override fun createFromParcel(parcel: Parcel): SongModel {
            return SongModel(parcel)
        }

        override fun newArray(size: Int): Array<SongModel?> {
            return arrayOfNulls(size)
        }
    }
}
