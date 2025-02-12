package com.ufes.callguard.UI

import FriendsAdapter
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.ufes.callguard.Class.Friend
import com.ufes.callguard.Class.UserModel
import com.ufes.callguard.R
import com.ufes.callguard.Util.UserAdapter

/**
 * Activity que representa a tela de comunidade do aplicativo.
 */
class CommunityActivity : AppCompatActivity() {

    private lateinit var searchView: SearchView
    private lateinit var recyclerView: RecyclerView
    private lateinit var userAdapter: UserAdapter
    private val userList = mutableListOf<UserModel>()
    private val filteredUserList = mutableListOf<UserModel>()
    private lateinit var currentUserId: String
    private lateinit var currentUser: UserModel

    private lateinit var recyclerViewFriends: RecyclerView
    private lateinit var friendsAdapter: FriendsAdapter
    private val friendsList = mutableListOf<Friend>()
    private val friendsUsernames = mutableSetOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_community)

        searchView = findViewById(R.id.searchView)
        recyclerView = findViewById(R.id.recyclerViewUsers)
        recyclerView.layoutManager = LinearLayoutManager(this)

        currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

        // Configurar o adapter e o RecyclerView
        userAdapter = UserAdapter(filteredUserList) { user ->
            showAddFriendDialog(user)
        }
        recyclerView.adapter = userAdapter

        // Inicialmente ocultar o RecyclerView
        recyclerView.visibility = View.GONE

        // Configurar o OnClickListener para o LinearLayout que envolve o SearchView
        val searchContainer = findViewById<LinearLayout>(R.id.searchContainer)
        searchContainer.setOnClickListener {
            // Abrir o campo de texto para escrita no SearchView
            searchView.isIconified = false
        }

        // Configurar o SearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                handleSearchQuery(newText)
                return true
            }
        })

        recyclerViewFriends = findViewById(R.id.recyclerViewFriends)
        recyclerViewFriends.layoutManager = LinearLayoutManager(this)

        // Configurar o adapter e o RecyclerView para amigos
        friendsAdapter = FriendsAdapter(this, friendsList, currentUserId) { friend ->
            showRemoveFriendDialog(friend)
        }
        recyclerViewFriends.adapter = friendsAdapter

        // Funções para buscar usuários e o usuário atual
        fetchCurrentUser()
    }

    private fun fetchUsers() {
        val database = FirebaseFirestore.getInstance()
        database.collection("usuario")
            .get()
            .addOnSuccessListener { result ->
                userList.clear()
                filteredUserList.clear()
                for (document in result) {
                    val user = document.toObject(UserModel::class.java)
                    if (user.getId() != currentUserId && !friendsUsernames.contains(user.getName())) {
                        userList.add(user)
                        filteredUserList.add(user)
                    }
                }
                userAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                // Handle errors
            }
    }

    private fun fetchCurrentUser() {
        val database = FirebaseFirestore.getInstance()
        database.collection("usuario").document(currentUserId)
            .get()
            .addOnSuccessListener { document ->
                currentUser = document.toObject(UserModel::class.java) ?: UserModel()
                fetchFriends()
            }
            .addOnFailureListener { exception ->
                // Handle errors
            }
    }

    private fun handleSearchQuery(query: String?) {
        filteredUserList.clear()
        val searchText = query?.trim()?.lowercase() ?: ""
        if (searchText.isNotEmpty()) {
            userList.forEach { user ->
                if (user.getName().lowercase().contains(searchText)) {
                    filteredUserList.add(user)
                }
            }
            recyclerView.visibility = View.VISIBLE
        } else {
            filteredUserList.addAll(userList)
            recyclerView.visibility = View.GONE
        }
        userAdapter.notifyDataSetChanged()
    }

    private fun showAddFriendDialog(user: UserModel) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Adicionar Amigo")
        builder.setMessage("Você deseja adicionar ${user.getName()} em sua lista de amigos?")
        builder.setPositiveButton("Sim") { dialog, _ ->
            addFriend(user)
            dialog.dismiss()
        }
        builder.setNegativeButton("Não") { dialog, _ ->
            dialog.dismiss()
        }
        builder.show()
    }

    private fun addFriend(user: UserModel) {
        val newFriend = Friend(user.getName(), false)
        currentUser.addAmigo(newFriend)

        val database = FirebaseFirestore.getInstance()
        database.collection("usuario").document(currentUserId)
            .set(currentUser)
            .addOnSuccessListener {
                Toast.makeText(this, "${user.getName()} adicionado a sua lista de amigos", Toast.LENGTH_SHORT).show()
                fetchFriends()
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Falha ao adicionar amigo", Toast.LENGTH_SHORT).show()
            }
    }

    private fun fetchFriends() {
        val database = FirebaseFirestore.getInstance()
        database.collection("usuario").document(currentUserId)
            .get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val friendListData = document.get("amigoList") as List<Map<String, Any>>?
                    if (friendListData != null) {
                        friendsList.clear()
                        friendsUsernames.clear()
                        for (friendData in friendListData) {
                            val userName = friendData["userName"] as String? ?: "Não encontrado"
                            val isSelected = friendData["selected"] as Boolean? ?: false
                            friendsList.add(Friend(userName, isSelected))
                            friendsUsernames.add(userName)
                        }
                        friendsAdapter.notifyDataSetChanged()
                        fetchUsers()
                    } else {
                        Toast.makeText(this, "Nenhum amigo encontrado", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, "Documento não encontrado", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Erro ao carregar a lista de amigos", Toast.LENGTH_SHORT).show()
            }
    }

    private fun showRemoveFriendDialog(friend: Friend) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Remover Amigo")
        builder.setMessage("Você deseja remover ${friend.userName} da sua lista de amigos?")
        builder.setPositiveButton("Sim") { dialog, _ ->
            removeFriend(friend)
            dialog.dismiss()
        }
        builder.setNegativeButton("Não") { dialog, _ ->
            dialog.dismiss()
        }
        builder.show()
    }

    private fun removeFriend(friend: Friend) {
        currentUser.removeAmigo(friend)

        val database = FirebaseFirestore.getInstance()
        database.collection("usuario").document(currentUserId)
            .set(currentUser)
            .addOnSuccessListener {
                Toast.makeText(this, "${friend.userName} removido da sua lista de amigos", Toast.LENGTH_SHORT).show()
                fetchFriends()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Falha ao remover amigo", Toast.LENGTH_SHORT).show()
            }
    }
}
