package com.ufes.callguard.Util

/**
 * Interface que manipula os callbacks em onRecive.
 */
interface BlockNumberCallback {
    /**
     * Método chamado quando o número é bloqueado.
     */
    fun onNumberBlocked()
    /**
     * Método chamado quando o número não é bloqueado.
     */
    fun onNumberNotBlocked()
}
