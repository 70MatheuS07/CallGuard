package com.ufes.callguard.Util

/**
 * Interface responsável por manipular o callback referente ao motivo do report.
 */
interface ReportReasonCallback {
    /**
     * Método responsável por adicionar um número à lista de reports, apenas quando o usuário selecionar
     * um motivo no aplicativo.
     * @param reasonIndex Motivo do report.
     */
    fun onReasonSelected(reasonIndex: Int)
}