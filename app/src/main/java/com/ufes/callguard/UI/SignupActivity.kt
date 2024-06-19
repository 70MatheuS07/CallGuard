package com.ufes.callguard.UI

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.ufes.callguard.Class.UserModel
import com.ufes.callguard.databinding.ActivitySignupBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
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
                CadastraUser(email, pass, view, user, number)
            }
            finish()
        }
    }


    private fun CadastraUser(email: String, pass: String, view: View, user:String, number:String) {
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, pass)
            .addOnCompleteListener { cadastro ->
                if (cadastro.isSuccessful) {
                    val usuario= UserModel(FirebaseAuth.getInstance().currentUser?.uid.toString(),user,number)
                    SalvarDadosUser(usuario)
                    val snackbar =
                        Snackbar.make(view, "Sucesso ao cadastrar usuário", Snackbar.LENGTH_SHORT)
                    snackbar.setBackgroundTint(Color.BLUE)
                    snackbar.show()
                    binding.editEmail.setText("")
                    binding.editPass.setText("")

                } else {
                    // Verifica o tipo de exceção lançada e define a mensagem de erro correspondente
                    var erro: String = ""
                    try {
                        throw cadastro.exception!!
                    } catch (e: FirebaseAuthWeakPasswordException) {
                        erro = "A senha deve conter no mínimo 6 caracteres"
                    } catch (e: FirebaseAuthUserCollisionException) {
                        erro = "E-mail já foi cadastrado"
                    } catch (e: FirebaseAuthInvalidCredentialsException) {
                        erro = "E-mail inválido"
                    } catch (e: Exception) {
                        erro = "Erro ao cadastrar usuário"
                    }


                    val snackbar = Snackbar.make(view, erro, Snackbar.LENGTH_LONG)
                    snackbar.setBackgroundTint(Color.RED)
                    snackbar.setTextColor(Color.BLACK)
                    snackbar.show()

                }

            }
    }

    private fun SalvarDadosUser(usuario: UserModel) {

        val database = FirebaseFirestore.getInstance()
        database.collection("usuario")
            .document(usuario.getId())
            .set(usuario)
            .addOnSuccessListener {
                Log.d("Teste", "Sucesso ao salvar os dados do usuário")
            }
            .addOnFailureListener {
                Log.d("Teste", "Erro ao salvar os dados do usuário")
            }
    }
}
