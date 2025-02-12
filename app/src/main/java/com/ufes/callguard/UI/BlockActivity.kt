package com.ufes.callguard.UI

import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.ufes.callguard.Class.BlockedContactModel
import com.ufes.callguard.R
import com.ufes.callguard.Util.BlockAdapter

/**
 * Activity responsável pela tela dos números bloqueados
 */
class BlockActivity : AppCompatActivity(), BlockAdapter.OnItemClickListener {

    private lateinit var recyclerView: RecyclerView
    private lateinit var blockAdapter: BlockAdapter
    private val blockList = mutableListOf<BlockedContactModel>()
    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

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

        findViewById<ImageView>(R.id.add_blocked_number).setOnClickListener {
            showAddBlockedNumberDialog()
        }
    }

    /**
     * Busca os números bloqueados de um usuário no Firestore
     * @param userId ID do usuário
     */
    private fun fetchBlockedNumbers(userId: String) {
        //Obtém lista de bloqueio
        firestore.collection("usuario").document(userId)
            .get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val blockListData = document.get("blockList") as List<Map<String, String>>?
                    if (blockListData != null) {
                        //Limpa a lista atual e preenche com novos dados
                        blockList.clear()
                        for (block in blockListData) {
                            val name = block["name"] ?: "Desconhecido"
                            val number = block["number"] ?: "Sem número"
                            blockList.add(BlockedContactModel(name, number))
                        }
                        // Notifica o adapter da mudança para que UI mude
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

    /**
     * Exibe um pop-up para adicionar um novo número bloqueado
     */
    private fun showAddBlockedNumberDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_blocked_number, null)
        val etBlockedNumber = dialogView.findViewById<EditText>(R.id.etBlockedNumber)
        val btnAddBlockedNumber = dialogView.findViewById<Button>(R.id.btnAddBlockedNumber)
        val btnCancel = dialogView.findViewById<Button>(R.id.btnCancel)

        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .create()

        btnAddBlockedNumber.setOnClickListener {
            val number = etBlockedNumber.text.toString().trim()
            if (number.isNotEmpty()) {
                addBlockedNumber(BlockedContactModel("Desconhecido", number))
                dialog.dismiss()
            } else {
                Toast.makeText(this, "Por favor, insira um número", Toast.LENGTH_SHORT).show()
            }
        }

        btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    /**
     * Adiciona um novo número bloqueado ao Firestore
     * @param contact Objeto BlockedContactModel contendo os dados do número bloqueado
     */
    private fun addBlockedNumber(contact: BlockedContactModel) {
        blockList.add(contact)
        blockAdapter.notifyItemInserted(blockList.size - 1)

        val userId = auth.currentUser?.uid ?: return
        firestore.collection("usuario").document(userId).update("blockList", blockList.map { it.toMap() })
            .addOnSuccessListener {
                Toast.makeText(this, "Número bloqueado adicionado", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Erro ao adicionar número bloqueado", Toast.LENGTH_SHORT).show()
            }
    }

    /**
     * Transforma o objeto BlockedContactModel em um mapa  para o Firestore
     * @return Mapa contendo os dados do número bloqueado
     */
    private fun BlockedContactModel.toMap(): Map<String, String> {
        return mapOf("name" to name, "number" to number)
    }

    /**
     * Implementação do método onItemClick que chama o dialog para remover o número bloqueado
     * @param contact Objeto BlockedContactModel contendo os dados do número bloqueado
     */
    override fun onItemClick(contact: BlockedContactModel) {
        showRemoveBlockedNumberDialog(contact)
    }

    /**
     * Exibe um pop-up para remover um número bloqueado
     * @param contact Objeto BlockedContactModel contendo os dados do número bloqueado
     */

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

    /**
     * Remove um número bloqueado do Firestore
     * @param contact Objeto BlockedContactModel contendo os dados do número bloqueado
     */
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
