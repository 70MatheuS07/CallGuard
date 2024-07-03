package com.ufes.callguard.UI

import android.app.AlertDialog
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.ufes.callguard.Class.Amigo
import com.ufes.callguard.Class.BlockedContactModel
import com.ufes.callguard.Class.UserModel
import com.ufes.callguard.R
import com.ufes.callguard.Util.BlockAdapter

class BlockActivity : AppCompatActivity(), BlockAdapter.OnItemClickListener {

    private lateinit var recyclerView: RecyclerView
    private lateinit var blockAdapter: BlockAdapter
    private val blockList = mutableListOf<BlockedContactModel>()
    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var currentUser: UserModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_block_home)

        recyclerView = findViewById(R.id.blockRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        blockAdapter = BlockAdapter(blockList, this)
        recyclerView.adapter = blockAdapter

        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        val currentUser = auth.currentUser
        if (currentUser != null) {
            val userId = currentUser.uid
            fetchBlockedNumbers(userId)
        } else {
            Toast.makeText(this, "Usuário não autenticado", Toast.LENGTH_SHORT).show()
        }
    }

    private fun fetchCurrentUser(userId: String) {
        firestore.collection("usuario").document(userId)
            .get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    currentUser = document.toObject(UserModel::class.java) ?: UserModel()
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Erro ao carregar usuário atual", Toast.LENGTH_SHORT).show()
            }
    }

    private fun fetchFriendsBlockedNumbers(userId: String) {
        firestore.collection("usuario").document(userId).collection("amigoBlockList")
            .get()
            .addOnSuccessListener { querySnapshot ->
                blockList.clear()

                for (document in querySnapshot) {
                    val friendId = document.id
                    fetchBlockedNumbers(friendId)
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Erro ao carregar a lista de bloqueios dos amigos", Toast.LENGTH_SHORT).show()
            }
    }

    private fun fetchBlockedNumbers(friendId: String) {
        firestore.collection("usuario").document(friendId)
            .get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val blockListData = document.get("blockList") as List<Map<String, String>>?
                    if (blockListData != null) {
                        for (block in blockListData) {
                            val name = block["name"] ?: "Desconhecido"
                            val number = block["number"] ?: "Sem número"
                            blockList.add(BlockedContactModel(name, number))
                        }
                        blockAdapter.notifyDataSetChanged()
                    } else {
                        Toast.makeText(this, "Nenhum número bloqueado encontrado", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, "Documento não encontrado", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Erro ao carregar os números bloqueados", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onItemClick(contact: BlockedContactModel) {
        showRemoveBlockedNumberDialog(contact)
    }

    private fun showRemoveBlockedNumberDialog(contact: BlockedContactModel) {
        val dialog = AlertDialog.Builder(this)
            .setTitle("Remover número bloqueado")
            .setMessage("Deseja remover ${contact.name} (${contact.number}) da lista de bloqueados?")
            .setPositiveButton("Remover") { _, _ ->
                removeBlockedNumber(contact)
            }
            .setNegativeButton("Cancelar", null)
            .create()

        dialog.show()
    }

    private fun removeBlockedNumber(contact: BlockedContactModel) {
        val position = blockList.indexOf(contact)
        if (position != -1) {
            blockList.removeAt(position)
            blockAdapter.notifyItemRemoved(position)

            val userId = auth.currentUser?.uid ?: return
            firestore.collection("usuario").document(userId).update("blockList", blockList.map { it.toMap() })
                .addOnSuccessListener {
                    Toast.makeText(this, "Número bloqueado removido", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Erro ao remover número bloqueado", Toast.LENGTH_SHORT).show()
                }
        }
    }
}
