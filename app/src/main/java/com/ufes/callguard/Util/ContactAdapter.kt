package com.ufes.callguard.Util

import android.content.Context
import android.database.Cursor
import android.provider.ContactsContract
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ufes.callguard.Class.Contact
import com.ufes.callguard.R

/**
 *
 */
/**
 * Adapter para exibição de uma lista de contatos em um RecyclerView.
 * @param context Contexto da aplicação.
 * @param contactList Lista de contatos a ser exibida.
 * @param onItemClick Função de callback a ser chamada quando um item é clicado.
 */
class ContactAdapter(
    private val context: Context,
    private val contactList: List<Contact>,
    private val onItemClick: (Contact) -> Unit
) : RecyclerView.Adapter<ContactAdapter.ContactViewHolder>() {

    /**
     * ViewHolder para os itens da lista de contatos.
     * @param itemView A view do item.
     */
    class ContactViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val contactName: TextView = itemView.findViewById(R.id.contactName)
        val contactNumber: TextView = itemView.findViewById(R.id.contactNumber)
        val contactImage: ImageView = itemView.findViewById(R.id.contactImage)
    }

    /**
     * Método chamado para criar um novo ViewHolder.
     * @param parent O ViewGroup ao qual a nova view será anexada.
     * @param viewType O tipo de view da nova view.
     * @return Um novo ContactViewHolder.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.contact_list, parent, false)
        return ContactViewHolder(view)
    }

    /**
     * Método chamado para associar um ViewHolder com dados.
     * @param holder O ViewHolder a ser atualizado.
     * @param position A posição do item na lista.
     */
    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        val contact = contactList[position]

        holder.contactName.text = contact.getContactName()
        holder.contactNumber.text = formatPhoneNumber(contact.getContactNumber())

        if (isNumberInContacts(contact.getContactNumber())) {
            holder.contactImage.setImageResource(R.drawable.baseline_person_green_24)
        } else {
            holder.contactImage.setImageResource(R.drawable.baseline_person_24)
        }

        holder.itemView.setOnClickListener {
            onItemClick(contact)
        }
    }

    /**
     * Retorna o número total de itens na lista.
     * @return O tamanho da lista de contatos.
     */
    override fun getItemCount() = contactList.size

    /**
     * Verifica se um número de telefone está na lista de contatos do dispositivo.
     * @param number O número de telefone a ser verificado.
     * @return True se o número estiver na lista de contatos, False caso contrário.
     */
    private fun isNumberInContacts(number: String): Boolean {
        val uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI
        val projection = arrayOf(ContactsContract.CommonDataKinds.Phone.NUMBER)
        val cursor: Cursor? = context.contentResolver.query(uri, projection, null, null, null)
        cursor?.use {
            val numberIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
            while (it.moveToNext()) {
                val contactNumber = it.getString(numberIndex)?.replace(Regex("[^0-9]"), "")
                if (contactNumber == number) {
                    return true
                }
            }
        }
        return false
    }

    /**
     * Formata um número de telefone para o formato correto.
     * @param number O número de telefone a ser formatado.
     * @return O número de telefone formatado.
     */
    private fun formatPhoneNumber(number: String): String {
        return when {
            number.length <= 4 -> number
            number.length == 8 -> "${number.substring(0, 4)}-${number.substring(4)}"
            number.length == 9 -> "${number.substring(0, 5)}-${number.substring(5)}"
            number.length == 10 -> "(${number.substring(0, 2)}) ${number.substring(2, 6)}-${number.substring(6)}"
            number.length == 11 -> "(${number.substring(0, 2)}) ${number.substring(2, 7)}-${number.substring(7)}"
            number.startsWith("+") -> number
            else -> number
        }
    }
}
