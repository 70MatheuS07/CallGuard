package com.ufes.callguard.Class

import android.os.Parcel
import android.os.Parcelable


//ideia nova, vetor de 4 posições de inteiros que representam os tipos de reports
class ContactReport() : Contact(), Parcelable {
    var type: MutableList<Int> = mutableListOf(0, 0, 0, 0)

    constructor(name: String, number: String, type: MutableList<Int>) : this() {
        this.name = name
        this.number = number
        this.type = type
    }

    constructor(parcel: Parcel) : this() {
        readFromParcel(parcel)
    }

    private fun readFromParcel(parcel: Parcel) {
        super.name = parcel.readString().toString()
        super.number = parcel.readString().toString()
        type = mutableListOf(parcel.readInt(), parcel.readInt(), parcel.readInt(), parcel.readInt())
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        super.writeToParcel(parcel, flags)
        parcel.writeInt(type[0])
        parcel.writeInt(type[1])
        parcel.writeInt(type[2])
        parcel.writeInt(type[3])
    }

    override fun describeContents(): Int {
        return 0
    }

    fun toHashMap(): HashMap<String, Any> {
        return hashMapOf(
            "name" to name,
            "number" to number,
            "type" to type // Use List instead of Array
        )
    }

    companion object CREATOR : Parcelable.Creator<ContactReport> {
        override fun createFromParcel(parcel: Parcel): ContactReport {
            return ContactReport(parcel)
        }

        override fun newArray(size: Int): Array<ContactReport?> {
            return arrayOfNulls(size)
        }
    }
}