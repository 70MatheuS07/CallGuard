package com.ufes.callguard.Class

import android.os.Parcel
import android.os.Parcelable

open class Contact() : Parcelable {
    var name: String = ""
    var number: String = ""


    constructor(name: String, number: String) : this() {
        this.name = name
        this.number = number
    }

    constructor(parcel: Parcel) : this() {
        name = parcel.readString().toString()
        number = parcel.readString().toString()
    }

    fun getContactName(): String = name
    fun setContactName(value: String) {
        name = value
    }

    fun getContactNumber(): String = number
    fun setContactNumber(value: String) {
        number = value
    }



    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeString(number)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Contact> {
        override fun createFromParcel(parcel: Parcel): Contact {
            return Contact(parcel)
        }

        override fun newArray(size: Int): Array<Contact?> {
            return arrayOfNulls(size)
        }
    }
}


