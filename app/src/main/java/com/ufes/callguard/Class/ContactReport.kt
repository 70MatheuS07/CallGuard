package com.ufes.callguard.Class

import android.os.Parcel
import android.os.Parcelable


/**
 * Subclasse de Contato responsável por representar um contato reportado
 * @property type Lista de 4 tipos de report possíveis para o contato
 *  type[0]=Venda/promo, type[1]=Golpe, type[2]=Atendimento ao cliente, type[3]=Outros
 */
class ContactReport() : Contact(), Parcelable {
    var type: MutableList<Int> = mutableListOf(0, 0, 0, 0)

    /**
     * Construtor da classe ContactReport
     * @param name Nome do contato reportado
     * @param number Número do contato reportado
     * @param type Lista de 4 tipos de report possíveis para o contato
     */
    constructor(name: String, number: String, type: MutableList<Int>) : this() {
        this.name = name
        this.number = number
        this.type = type
    }

    /**
     *
     */
    /**
     * Construtor que cria uma instância de [ContactReport] a partir de um [Parcel].
     *
     * @param parcel O parcel do qual o objeto será criado.
     */
    constructor(parcel: Parcel) : this() {
        readFromParcel(parcel)
    }

    /**
     * Lê os dados de um [Parcel] e os atribui às propriedades da classe.
     *
     * @param parcel O parcel do qual os dados serão lidos.
     */
    private fun readFromParcel(parcel: Parcel) {
        super.name = parcel.readString().toString()
        super.number = parcel.readString().toString()
        type = mutableListOf(parcel.readInt(), parcel.readInt(), parcel.readInt(), parcel.readInt())
    }

    /**
     * Escreve o objeto [ContactReport] para um [Parcel].
     *
     * @param parcel O parcel onde o objeto será escrito.
     * @param flags Sinais adicionais sobre como o objeto deve ser escrito.
     */
    override fun writeToParcel(parcel: Parcel, flags: Int) {
        super.writeToParcel(parcel, flags)
        parcel.writeInt(type[0])
        parcel.writeInt(type[1])
        parcel.writeInt(type[2])
        parcel.writeInt(type[3])
    }

    /**
     * Retorna uma descrição do conteúdo do objeto [ContactReport].
     *
     * @return Um valor inteiro indicando o tipo de objetos especiais contidos
     *         por este Parcelable.
     */
    override fun describeContents(): Int {
        return 0
    }

    /**
     * Converte o objeto [ContactReport] para um [HashMap].
     *
     * @return Um HashMap com os dados do objeto.
     */
    fun toHashMap(): HashMap<String, Any> {
        return hashMapOf(
            "name" to name,
            "number" to number,
            "type" to type // Use List instead of Array
        )
    }

    /**
     * Classe responsável por criar instâncias de [ContactReport] a partir de um [Parcel].
     */
    companion object CREATOR : Parcelable.Creator<ContactReport> {

        /**
         * Cria um novo objeto [ContactReport] a partir de um [Parcel].
         *
         * @param parcel O parcel do qual o objeto será criado.
         * @return Uma nova instância de [ContactReport].
         */
        override fun createFromParcel(parcel: Parcel): ContactReport {
            return ContactReport(parcel)
        }

        /**
         * Cria um novo array de objetos [ContactReport].
         *
         * @param size O tamanho do array a ser criado.
         * @return Um array de objetos [ContactReport], inicializado com nulls.
         */
        override fun newArray(size: Int): Array<ContactReport?> {
            return arrayOfNulls(size)
        }
    }
}