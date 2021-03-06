package com.yuriysurzhikov.lab2.model

import android.net.Uri
import android.os.Parcel
import android.os.Parcelable

data class EmailObject(
    var address: String?,
    var subject: String?,
    var body: String?,
    var imageUri: Uri?
) : Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readParcelable(Uri::class.java.classLoader)
    )

    constructor() : this(
        null,
        null,
        null,
        null
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(address)
        parcel.writeString(subject)
        parcel.writeString(body)
        parcel.writeParcelable(imageUri, flags)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<EmailObject> {
        override fun createFromParcel(parcel: Parcel): EmailObject {
            return EmailObject(parcel)
        }

        override fun newArray(size: Int): Array<EmailObject?> {
            return arrayOfNulls(size)
        }
    }

}