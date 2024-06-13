package com.ufes.callguard.UI

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.ufes.callguard.databinding.ActivityLoginBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.buttonLogin.setOnClickListener{view->
            val email= binding.editEmail.text.toString()
            val pass= binding.editPass.text.toString()

            if(email.isEmpty()|| pass.isEmpty()){
                val snackbar= Snackbar.make(view, "Preencha todos os campos!", Snackbar.LENGTH_SHORT)
                snackbar.setBackgroundTint(Color.RED)
                snackbar.show()
            }
            else{
                FirebaseAuth.getInstance().signInWithEmailAndPassword(email, pass).addOnCompleteListener{autenticacao->
                    if(autenticacao.isSuccessful){
                        val snackbar = Snackbar.make(view, "Sucesso ao logar usuário", Snackbar.LENGTH_SHORT)
                        snackbar.setBackgroundTint(Color.BLUE)
                        snackbar.show()
                        binding.editEmail.setText("")
                        binding.editPass.setText("")
                        startActivity(Intent(this, HomeActivity::class.java))
                    }
                }
            }
        }
        binding.textSignup.setOnClickListener{
            val i=Intent(this, SignupActivity::class.java)
            startActivity(i)
        }

        binding.textSenha.setOnClickListener{

        }


    }
}