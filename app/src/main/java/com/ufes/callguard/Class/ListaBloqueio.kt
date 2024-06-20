package com.ufes.callguard.Class


import android.os.Parcel
import android.os.Parcelable
class ListaBloqueio() : Parcelable{

    private var id : String = ""
    private var listaBlock : MutableList<Contact> = mutableListOf()

    //getters e setters

    fun getId(): String {
        return id
    }
    fun setId(id: String) {
        this.id = id
    }

    fun getListaBlock(): MutableList<Contact> {
        return listaBlock
    }
    fun setListaBlock(listaBlock: MutableList<Contact>) {
        this.listaBlock = listaBlock
    }

    constructor(id: String, listaBlock: MutableList<Contact>) : this() {
        this.id = id
        this.listaBlock = listaBlock
    }

    constructor(parcel: Parcel) : this() {
        id = parcel.readString().toString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ListaBloqueio> {
        override fun createFromParcel(parcel: Parcel): ListaBloqueio {
            return ListaBloqueio(parcel)
        }

        override fun newArray(size: Int): Array<ListaBloqueio?> {
            return arrayOfNulls(size)
        }
    }


}