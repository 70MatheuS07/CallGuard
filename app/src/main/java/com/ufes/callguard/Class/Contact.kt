package com.ufes.callguard.Class

import android.os.Parcel
import android.os.Parcelable


class Contact() : Parcelable {
    private var id: String = ""
    private var name: String = ""
    private var number: String = ""
    private var type: Int = 0



    fun getId(): String = id
    fun setId(value: String) {
        id = value
    }

    fun getName(): String = name
    fun setName(value: String) {
        name = value
    }

    fun getNumber(): String = number
    fun setNumber(value: String) {
        number = value
    }

    fun getType(): Int = type
    fun setType(value: Int) {
        type = value
    }

    constructor(parcel: Parcel): this()
    {
        id = parcel.readString().toString()
        name = parcel.readString().toString()
        number = parcel.readString().toString()
        type = parcel.readInt()
    }

    constructor(id: String, name: String, number: String) : this() {
        this.id = id
        this.name = name
        this.number = number
    }

    constructor( type: Int) : this() {
        this.type = type
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(name)
        parcel.writeString(number)
        parcel.writeInt(type)
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



