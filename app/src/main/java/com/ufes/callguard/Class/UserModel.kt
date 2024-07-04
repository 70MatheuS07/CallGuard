package com.ufes.callguard.Class

import android.os.Parcel
import android.os.Parcelable

/**
 * Classe responsável por representar o usuário do aplicativo.
 * @property id ID do usuário
 * @property name Nome do usuário
 * @property phone Número de telefone do usuário
 * @property blockList Lista de contatos bloqueados do usuário
 * @property amigoList Lista de amigos do usuário
 * @property highReports Indica se o usuário quer bloquear números com mais de 100 reports
 */
class UserModel() : Parcelable {
    private var id = ""
    private var name = ""
    private var phone = ""

    private var blockList: MutableList<Contact> = mutableListOf()
    private var amigoList: MutableList<Friend> = mutableListOf()
    private var highReports: Boolean =false

    /**
     * Retorna a lista de amigos do usuário.
     *
     * @return Uma lista de objetos [Friend].
     */
    fun getAmigoList(): MutableList<Friend> = amigoList

    /**
     * Adiciona um amigo à lista de amigos do usuário.
     *
     * @param amigo O objeto [Friend] a ser adicionado.
     */
    fun addAmigo(amigo: Friend) {
        amigoList.add(amigo)
    }

    /**
     * Construtor que cria uma instância de [UserModel] a partir de um [Parcel].
     *
     * @param parcel O parcel do qual o objeto será criado.
     */
    constructor(parcel: Parcel) : this() {
        id = parcel.readString().toString()
        name = parcel.readString().toString()
        phone = parcel.readString().toString()
    }

    /**
     * Retorna o ID do usuário.
     *
     * @return O ID do usuário.
     */
    fun getId(): String {
        return id
    }

    /**
     * Define o ID do usuário.
     *
     * @param id O novo ID do usuário.
     */
    fun setId(id: String) {
        this.id = id
    }

    /**
     * Retorna o nome do usuário.
     *
     * @return O nome do usuário.
     */
    fun getName(): String {
        return name
    }

    /**
     * Define o nome do usuário.
     *
     * @param name O novo nome do usuário.
     */
    fun setName(name: String) {
        this.name = name
    }

    /**
     * Retorna o telefone do usuário.
     *
     * @return O telefone do usuário.
     */
    fun getPhone(): String {
        return phone
    }

    /**
     * Define o telefone do usuário.
     *
     * @param phone O novo telefone do usuário.
     */
    fun setPhone(phone: String) {
        this.phone = phone
    }

    /**
     * Retorna a lista de contatos bloqueados do usuário.
     *
     * @return Uma lista mutável de objetos [Contact].
     */
    fun getBlockList(): MutableList<Contact> {
        return blockList
    }

    /**
     * Define a lista de contatos bloqueados do usuário.
     *
     * @param blockList A nova lista de contatos bloqueados.
     */
    fun setBlockList(blockList: MutableList<Contact>) {
        this.blockList = blockList
    }

    /**
     * Define o valor de [highReports] do usuário.
     *
     * @param highReports O novo valor para [highReports].
     */
    fun setHighReports(highReports: Boolean) {
        this.highReports = highReports
    }

    /**
     * Retorna o valor de [highReports] do usuário.
     *
     * @return O valor de [highReports].
     */
    fun getHighReports(): Boolean {
        return highReports
    }

    /**
     * Construtor que cria uma instância de [UserModel] com ID, nome e telefone.
     *
     * @param id O ID do usuário.
     * @param name O nome do usuário.
     * @param phone O telefone do usuário.
     */
    constructor(id: String, name: String, phone: String) : this() {
        this.id = id
        this.name = name
        this.phone = phone
    }

    /**
     * Escreve o objeto [UserModel] para um [Parcel].
     *
     * @param parcel O parcel onde o objeto será escrito.
     * @param flags Sinais adicionais sobre como o objeto deve ser escrito.
     */
    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(name)
        parcel.writeString(phone)
    }

    /**
     * Retorna uma descrição do conteúdo do objeto [UserModel].
     *
     * @return Um valor inteiro indicando o tipo de objetos especiais contidos por este Parcelable.
     */
    override fun describeContents(): Int {
        return 0
    }

    /**
     * Classe responsável por criar instâncias de [UserModel] a partir de um [Parcel].
     */
    companion object CREATOR : Parcelable.Creator<UserModel> {

        /**
         * Cria um novo objeto [UserModel] a partir de um [Parcel].
         *
         * @param parcel O parcel do qual o objeto será criado.
         * @return Uma nova instância de [UserModel].
         */
        override fun createFromParcel(parcel: Parcel): UserModel {
            return UserModel(parcel)
        }

        /**
         * Cria um novo array de objetos [UserModel].
         *
         * @param size O tamanho do array a ser criado.
         * @return Um array de objetos [UserModel], inicializado com nulls.
         */
        override fun newArray(size: Int): Array<UserModel?> {
            return arrayOfNulls(size)
        }
    }
}