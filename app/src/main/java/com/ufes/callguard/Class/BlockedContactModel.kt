package com.ufes.callguard.Class

/**
 * Classe responsável por representar um contato bloqueado na lista de bloqueio.
 * @property name Nome do contato bloqueado.
 * @property number Número do contato bloqueado.
 */
data class BlockedContactModel(val name: String, val number: String)