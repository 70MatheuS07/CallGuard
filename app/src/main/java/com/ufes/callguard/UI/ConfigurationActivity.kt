package com.ufes.callguard.UI

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.ufes.callguard.Class.UserModel
import com.ufes.callguard.R
import com.ufes.callguard.databinding.ActivityConfigurationBinding

/**
 * Activity responsável por exibir a tela de configuração do aplicativo.
 */
class ConfigurationActivity : AppCompatActivity() {
    private lateinit var binding: ActivityConfigurationBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityConfigurationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //Deslogar usuário
        binding.ButtonLogout.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val intent = (Intent(this, LoginActivity::class.java)).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            startActivity(intent)
        }
        //estado do switch fica de acordo com o que está no banco de dados
        val userDocRef = FirebaseFirestore.getInstance().collection("usuario")
            .document(FirebaseAuth.getInstance().currentUser!!.uid)
        userDocRef.get().addOnSuccessListener { document ->
            if (document != null) {
                var user = document.toObject(UserModel::class.java)
                loadSwitchStateFromFirestore(user!!)
                binding.configurationSwitch.setOnCheckedChangeListener { _, isChecked ->
                    user?.setHighReports(isChecked)
                    updateUserModelInDatabase(user!!)
                }
            } else {
                Log.d("Firestore", "Documento não encontrado")
            }
        }.addOnFailureListener { exception ->
            Log.d("Firestore", "Falha com", exception)
        }
    }

    /**
     * Carrega o estado do atributo highReports do usuário do banco de dados e atualiza o switch.
     * @param user o usuário do banco de dados.
     */
    private fun loadSwitchStateFromFirestore(user: UserModel) {
        val userDocRef = FirebaseFirestore.getInstance().collection("usuario").document(user.getId())
        userDocRef.get().addOnSuccessListener { document ->
            if (document.exists()) {
                val userModel = document.toObject(UserModel::class.java)
                userModel?.let {
                    binding.configurationSwitch.isChecked = it.getHighReports()
                }
            }
        }.addOnFailureListener { e ->
            Log.w("Firestore", "Erro ao obter o estado do usuário", e)
        }
    }

    /**
     * Atualiza o usuário no banco de dados.
     * @param userModel o usuário a ser atualizado.
     */
    private fun updateUserModelInDatabase(userModel: UserModel) {
        val db = FirebaseFirestore.getInstance()
        val userRef = db.collection("usuario").document(userModel.getId())

        userRef.set(userModel)
            .addOnSuccessListener {
                Log.d("Firestore", "User model atualizado com sucesso")
            }
            .addOnFailureListener { e ->
                Log.w("Firestore", "Error ao atualizar o usuário", e)
            }
    }
}