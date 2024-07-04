package com.ufes.callguard.Util

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ufes.callguard.Class.UserModel
import com.ufes.callguard.R

/**
 * Adapter para exibição de uma lista de usuários em um RecyclerView.
 * @param userList Lista de usuários a ser exibida.
 * @param onItemClick Callback para quando um item da lista for clicado.
 */
class UserAdapter(
    private val userList: List<UserModel>,
    private val onItemClick: (UserModel) -> Unit
) : RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

    /**
     * ViewHolder para os itens da lista de usuários.
     * @param itemView A view do item.
     */
    inner class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val userName: TextView = itemView.findViewById(R.id.textViewUserName)
        val userNumber: TextView = itemView.findViewById(R.id.textViewUserNumber)

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemClick(userList[position])
                }
            }
        }
    }

    /**
     * Método chamado para criar um novo ViewHolder.
     * @param parent O ViewGroup ao qual a nova view será anexada.
     * @param viewType O tipo de view da nova view.
     * @return Um novo UserViewHolder.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_user, parent, false)
        return UserViewHolder(view)
    }

    /**
     * Método chamado para associar um ViewHolder com dados.
     * @param holder O ViewHolder a ser atualizado.
     * @param position A posição do item na lista.
     */
    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = userList[position]
        holder.userName.text = user.getName()
        holder.userNumber.text = user.getPhone()
    }

    /**
     * Retorna o número total de itens na lista.
     * @return O tamanho da lista de usuários.
     */
    override fun getItemCount(): Int {
        return userList.size
    }
}
