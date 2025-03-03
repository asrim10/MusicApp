package com.example.musicapp.model

import android.os.Parcel
import android.os.Parcelable

data class PlaylistModel(
    var playlistId: String = "",
    var playlistName: String = "",
    var playlistDesc: String = "",
    var playlistImageUrl: String = "",
    var songs: List<String>? = null
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.createStringArrayList()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(playlistId)
        parcel.writeString(playlistName)
        parcel.writeString(playlistDesc)
        parcel.writeString(playlistImageUrl)
        parcel.writeStringList(songs)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<PlaylistModel> {
        override fun createFromParcel(parcel: Parcel): PlaylistModel {
            return PlaylistModel(parcel)
        }

        override fun newArray(size: Int): Array<PlaylistModel?> {
            return arrayOfNulls(size)
        }
    }
}
