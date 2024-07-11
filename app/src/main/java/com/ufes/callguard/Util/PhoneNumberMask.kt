package com.ufes.callguard.Util

import android.text.Editable
import android.text.TextWatcher

/**
 * Classe PhoneNumberMask que implementa a interface TextWatcher
 * para limitar o comprimento do texto em um EditText.
 *
 * @property maxLength O comprimento máximo permitido para o texto.
 */
class PhoneNumberMask(private val maxLength: Int) : TextWatcher {

    /**
     * Método chamado antes do texto ser alterado.
     *
     * @param s O texto antes da alteração.
     * @param start A posição inicial onde o texto será alterado.
     * @param count O número de caracteres que serão alterados.
     * @param after O número de caracteres que serão adicionados.
     */
    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

    /**
     * Método chamado enquanto o texto está sendo alterado.
     *
     * @param s O texto durante a alteração.
     * @param start A posição inicial onde o texto está sendo alterado.
     * @param before O número de caracteres que estavam presentes antes da alteração.
     * @param count O número de caracteres que foram adicionados.
     */
    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

    /**
     * Método chamado após o texto ter sido alterado.
     * Verifica se o comprimento do texto excede o comprimento máximo permitido e,
     * se necessário, remove os caracteres extras.
     *
     * @param s O texto após a alteração.
     */
    override fun afterTextChanged(s: Editable?) {
        s?.let {
            if (it.length > maxLength) {
                it.delete(maxLength, it.length)
            }
        }
    }
}
