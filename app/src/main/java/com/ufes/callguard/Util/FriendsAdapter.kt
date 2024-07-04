package com.ufes.callguard.Util

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Switch
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.ufes.callguard.Class.Friend
import com.ufes.callguard.Class.UserModel
import com.ufes.callguard.R

/**
 * Adapter para exibição de uma lista de amigos em um RecyclerView.
 * @param context Contexto da aplicação.
 * @param friendsList Lista de amigos a ser exibida.
 * @param currentUserId ID do usuário atual.
 */
class FriendsAdapter(
    private val context: Context,
    private val friendsList: List<Friend>,
    private val currentUserId: String
) : RecyclerView.Adapter<FriendsAdapter.FriendViewHolder>() {

    private val firestore = FirebaseFirestore.getInstance()

    /**
     * Método chamado para criar um novo ViewHolder.
     * @param parent O ViewGroup ao qual a nova view será anexada.
     * @param viewType O tipo de view da nova view.
     * @return Um novo FriendViewHolder.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.friend_list, parent, false)
        return FriendViewHolder(view)
    }

    /**
     * Método chamado para associar um ViewHolder com dados.
     * @param holder O ViewHolder a ser atualizado.
     * @param position A posição do item na lista.
     */
    override fun onBindViewHolder(holder: FriendViewHolder, position: Int) {
        val friend = friendsList[position]
        holder.friendNameTextView.text = friend.userName
        holder.friendSwitch.isChecked = friend.isSelected

        holder.friendSwitch.setOnCheckedChangeListener { _, isChecked ->
            // Atualize o estado de seleção do amigo
            friend.isSelected = isChecked
            // Atualize a alteração no Firebase
            updateFriendSelectionInDatabase(friend)
        }
    }

    /**
     * Retorna o número total de itens na lista.
     * @return O tamanho da lista de amigos.
     */
    override fun getItemCount(): Int {
        return friendsList.size
    }

    /**
     * Atualiza o estado de seleção de um amigo no Firebase.
     * @param friend O amigo a ser atualizado.
     */
    private fun updateFriendSelectionInDatabase(friend: Friend) {
        // Encontre o documento do usuário atual no Firestore
        val userDocumentRef = firestore.collection("usuario").document(currentUserId)

        userDocumentRef.get().addOnSuccessListener { documentSnapshot ->
            if (documentSnapshot.exists()) {
                // Obtenha a lista de amigos do documento
                val amigoList = documentSnapshot.toObject(UserModel::class.java)?.getAmigoList()
                amigoList?.let {
                    // Encontre o amigo e atualize o campo selected
                    for (amigo in it) {
                        if (amigo.userName == friend.userName) {
                            amigo.isSelected = friend.isSelected
                            break
                        }
                    }
                    // Salve a lista atualizada de volta no Firestore
                    userDocumentRef.update("amigoList", it)
                        .addOnSuccessListener {
                            // Sucesso ao atualizar
                        }
                        .addOnFailureListener { e ->
                            // Erro ao atualizar
                        }
                }
            }
        }
    }

    /**
     * ViewHolder para exibir uma lista de amigos.
     * @param itemView A view associada ao ViewHolder.
     */
    class FriendViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val friendNameTextView: TextView = itemView.findViewById(R.id.friendNameTextView)
        val friendSwitch: Switch = itemView.findViewById(R.id.friendSwitch)
    }
}
