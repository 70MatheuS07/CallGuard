package com.ufes.callguard.UI

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.ufes.callguard.Model.UserModel
import com.ufes.callguard.databinding.ActivityLoginBinding
import com.ufes.callguard.databinding.ActivitySignupBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class SignupActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignupBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.buttonSignup.setOnClickListener { view ->
            val number = binding.editNumberSignup.text.toString()
            val user = binding.editUser.text.toString()
            val pass = binding.editPass.text.toString()
            val email = binding.editEmail.text.toString()

            if (email.isEmpty() || pass.isEmpty()) {
                val snackbar =
                    Snackbar.make(view, "Preencha todos os campos", Snackbar.LENGTH_SHORT)
                snackbar.setBackgroundTint(Color.RED)
                snackbar.show()
            } else {
                CadastraUser(email, pass, view);
            }
            val usuario =
                UserModel(FirebaseAuth.getInstance().currentUser?.uid.toString(), user, number)
            SalvarDadosUser(usuario, view)
            finish()
        }
    }


    private fun CadastraUser(email: String, pass: String, view: View) {
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, pass)
            .addOnCompleteListener { cadastro ->
                if (cadastro.isSuccessful) {
                    val snackbar =
                        Snackbar.make(view, "Sucesso ao cadastrar usuário", Snackbar.LENGTH_SHORT)
                    snackbar.setBackgroundTint(Color.BLUE)
                    snackbar.show()
                    binding.editEmail.setText("")
                    binding.editPass.setText("")

                }
            }.addOnFailureListener {

            }
    }

    private fun SalvarDadosUser(usuario: UserModel, view: View) {

        val database = FirebaseFirestore.getInstance()
        database.collection("usuario")
            .document(usuario.id)
            .set(usuario)
            .addOnSuccessListener {
                Log.d("Teste", "Sucesso ao salvar os dados do usuário")
            }
            .addOnFailureListener {
                Log.d("Teste", "Erro ao salvar os dados do usuário")
            }
    }
}
