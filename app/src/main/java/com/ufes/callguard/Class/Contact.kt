package com.ufes.callguard.Class

import android.os.Parcel
import android.os.Parcelable

/**
 * Classe responsável por representar um contato.
 * @property name Nome do contato.
 * @property number Número do contato.
 */
open class Contact() : Parcelable {
    var name: String = ""
    var number: String = ""

    /**
     * Construtor da classe.
     * @param name Nome do contato.
     * @param number Número do contato.
     */
    constructor(name: String, number: String) : this() {
        this.name = name
        this.number = number
    }

    /**
     * Construtor da classe a partir de um parcel.
     * @param parcel Parcela a partir da qual os dados serão lidos.
     */
    constructor(parcel: Parcel) : this() {
        name = parcel.readString().toString()
        number = parcel.readString().toString()
    }

    /**
     * Retorna o nome do contato.
     * @return Nome do contato.
     */
    fun getContactName(): String = name

    /**
     * Atribui o nome ao contato.
     * @param value Nome do contato.
     */
    fun setContactName(value: String) {
        name = value
    }

    /**
     * Retorna o número do contato.
     * @return Número do contato.
     */
    fun getContactNumber(): String = number

    /**
     * Atribui o número ao contato.
     * @param value Número do contato.
     */
    fun setContactNumber(value: String) {
        number = value
    }



    /**
     * Escreve o objeto [Contact] para um [Parcel].
     *
     * @param parcel O parcel onde o objeto será escrito.
     * @param flags Sinais adicionais sobre como o objeto deve ser escrito.
     */
    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeString(number)
    }

    /**
     * Retorna uma descrição do conteúdo do objeto [Contact].
     *
     * @return Um valor inteiro indicando o tipo de objetos especiais contidos
     *         por este Parcelable.
     */
    override fun describeContents(): Int {
        return 0
    }

    /**
     * Classe responsável por criar instâncias de [Contact] a partir de um [Parcel].
     */
    companion object CREATOR : Parcelable.Creator<Contact> {

        /**
         * Cria um novo objeto [Contact] a partir de um [Parcel].
         *
         * @param parcel O parcel do qual o objeto será criado.
         * @return Uma nova instância de [Contact].
         */
        override fun createFromParcel(parcel: Parcel): Contact {
            return Contact(parcel)
        }

        /**
         * Cria um novo array de objetos [Contact].
         *
         * @param size O tamanho do array a ser criado.
         * @return Um array de objetos [Contact], inicializado com nulls.
         */
        override fun newArray(size: Int): Array<Contact?> {
            return arrayOfNulls(size)
        }
    }
}


