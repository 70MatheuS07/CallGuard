package com.ufes.callguard.Model

import android.os.Parcel
import android.os.Parcelable

class UserModel() : Parcelable{
    public var id= ""
    public var name = ""
    public var phone = ""

    constructor(parcel: Parcel) : this() {
        id = parcel.readString().toString()
        name = parcel.readString().toString()
        phone = parcel.readString().toString()
    }

    constructor(id:String,name:String, number: String) : this() {
        this.id= id
        this.phone=number
        this.name=name

    }
    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(name)
        parcel.writeString(phone)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<UserModel> {
        override fun createFromParcel(parcel: Parcel): UserModel {
            return UserModel(parcel)
        }

        override fun newArray(size: Int): Array<UserModel?> {
            return arrayOfNulls(size)
        }
    }

}