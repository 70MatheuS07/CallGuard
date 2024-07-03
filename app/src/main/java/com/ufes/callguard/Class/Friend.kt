package com.ufes.callguard.Class

/**
 * Classe que será utilizada para representar os dados dos amigos na lista de amigos
 *
 * @property userName Nome do amigo
 * @property isSelected Indica se o usuário quer compartilhar da lista de bloqueios do amigo
 */
data class Friend(val userName: String, val isSelected: Boolean) {
    // Default constructor
    /**
     * Construtor do objeto Friend
     * @param userName Nome do amigo
     * @param isSelected Indica se o usuário quer compartilhar da lista de bloqueios do amigo
     */
    constructor() : this("", false)
}
