package com.ufes.callguard.UI

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.ufes.callguard.databinding.ActivityLoginBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth

/**
 * Activity que representa a tela de login do aplicativo.
 */
class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.buttonLogin.setOnClickListener { view ->
            val email = binding.editEmail.text.toString()
            val pass = binding.editPass.text.toString()
            val keeplogged = binding.checkBoxLogged.isChecked
            saveLoginPreference(keeplogged)
            if (email.isEmpty() || pass.isEmpty()) {
                val snackbar =
                    Snackbar.make(view, "Preencha todos os campos!", Snackbar.LENGTH_SHORT)
                snackbar.setBackgroundTint(Color.RED)
                snackbar.show()
            } else {
                AutenticaUser(email, pass, view)
            }
        }
        binding.textSignup.setOnClickListener {
            val i = Intent(this, SignupActivity::class.java)
            startActivity(i)
        }

        binding.textSenha.setOnClickListener {
           startActivity(Intent(this, ForgotPasswordActivity::class.java))

        }


    }

    /**
     * Método responsável por realizar a autenticação do usuário.
     * @param email E-mail do usuário.
     * @param pass Senha do usuário.
     * @param view View da tela de login para exibir os toasts.
     */
    private fun AutenticaUser(email: String, pass: String, view: View) {
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, pass)
            .addOnCompleteListener { autenticacao ->
                if (autenticacao.isSuccessful) {
                    val snackbar =
                        Snackbar.make(view, "Sucesso ao logar usuário", Snackbar.LENGTH_SHORT)
                    snackbar.setBackgroundTint(Color.BLUE)
                    snackbar.show()
                    binding.editEmail.setText("")
                    binding.editPass.setText("")
                    startActivity(Intent(this, HomeActivity::class.java))
                } else {

                    val snackbar =
                        Snackbar.make(view, "Erro ao logar usuário", Snackbar.LENGTH_SHORT)
                    snackbar.setBackgroundTint(Color.RED)
                    snackbar.show()
                }
            }

    }

    /**
     * Método responsável por salvar as preferências de login do usuário.
     * @param keepLoggedIn Indica se o usuário deseja manter-se logado.
     */
    private fun saveLoginPreference(keepLoggedIn: Boolean) {
        val sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putBoolean("keepLoggedIn", keepLoggedIn)
        editor.apply()
    }

    /**
     * Método responsável por verificar se o usuário já está logado quando a tela é iniciada.
     */
    override fun onStart() {
        super.onStart()
        val sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE)
        val keepLoggedIn = sharedPreferences.getBoolean("keepLoggedIn", false)

        val usuarioAtual = FirebaseAuth.getInstance().currentUser
        //Se usuário estiver logado, redireciona para a HomeActivity
        if (usuarioAtual != null && keepLoggedIn) {
            startActivity(Intent(this, HomeActivity::class.java))
        }

    }
}
