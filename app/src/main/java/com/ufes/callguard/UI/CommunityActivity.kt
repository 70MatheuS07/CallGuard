package com.ufes.callguard.UI

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.ufes.callguard.Class.UserModel
import com.ufes.callguard.R
import androidx.appcompat.widget.SearchView
import com.google.firebase.auth.FirebaseAuth
import com.ufes.callguard.Util.UserAdapter

class CommunityActivity : AppCompatActivity() {

    private lateinit var searchView: SearchView
    private lateinit var recyclerView: RecyclerView
    private lateinit var userAdapter: UserAdapter
    private val userList = mutableListOf<UserModel>()
    private val filteredUserList = mutableListOf<UserModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_community)

        searchView = findViewById(R.id.searchView)
        recyclerView = findViewById(R.id.recyclerViewUsers)
        recyclerView.layoutManager = LinearLayoutManager(this)
        userAdapter = UserAdapter(filteredUserList)
        recyclerView.adapter = userAdapter

        // Inicialmente ocultar o RecyclerView
        recyclerView.visibility = View.GONE

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                handleSearchQuery(newText)
                return true
            }
        })

        fetchUsers()
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
                    userList.add(user)
                    filteredUserList.add(user)
                }
                userAdapter.notifyDataSetChanged()
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
            // Mostrar o RecyclerView apenas quando há resultados de pesquisa
            recyclerView.visibility = View.VISIBLE
        } else {
            filteredUserList.addAll(userList)
            // Ocultar o RecyclerView quando não há texto de pesquisa
            recyclerView.visibility = View.GONE
        }
        userAdapter.notifyDataSetChanged()
    }
}
