package com.ufes.callguard.Class

import android.os.Parcel
import android.os.Parcelable

class UserModel() : Parcelable {
    private var id = ""
    private var name = ""
    private var phone = ""

    private var blockList: MutableList<Contact> = mutableListOf()

    constructor(parcel: Parcel) : this() {
        id = parcel.readString().toString()
        name = parcel.readString().toString()
        phone = parcel.readString().toString()
    }

    fun getId(): String {
        return id
    }

    fun setId(id: String) {
        this.id = id
    }

    fun getName(): String {
        return name
    }

    fun setName(name: String) {
        this.name = name
    }

    fun getPhone(): String {
        return phone
    }

    fun setPhone(phone: String) {
        this.phone = phone
    }

    fun getBlockList(): MutableList<Contact> {
        return blockList
    }

    fun setBlockList(blockList: MutableList<Contact>) {
        this.blockList = blockList
    }

    constructor(id: String, name: String, phone: String) : this() {
        this.id = id
        this.name = name
        this.phone = phone
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