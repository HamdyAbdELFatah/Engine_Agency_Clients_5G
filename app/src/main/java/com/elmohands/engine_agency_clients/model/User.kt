package com.elmohands.engine_agency_clients.model

import android.os.Parcel
import android.os.Parcelable

class User(
    val userId: String,
    val image: String,
    val userName: String
): Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString()
    ) {
    }

    constructor() : this("", "", "")

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel?, p1: Int) {
        dest?.writeString(userId)
        dest?.writeString(image)
        dest?.writeString(userName)

    }

    companion object CREATOR : Parcelable.Creator<User> {
        override fun createFromParcel(parcel: Parcel): User {
            return User(parcel)
        }

        override fun newArray(size: Int): Array<User?> {
            return arrayOfNulls(size)
        }
    }
}
