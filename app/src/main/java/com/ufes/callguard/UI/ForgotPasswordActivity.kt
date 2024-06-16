package com.ufes.callguard.UI

import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.ufes.callguard.databinding.ActivityForgotPasswordBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ForgotPasswordActivity : AppCompatActivity() {
    private lateinit var binding: ActivityForgotPasswordBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForgotPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.ButtonRecovery.setOnClickListener { view ->
            val email = binding.EditTextEmail.text.toString()
            FirebaseAuth.getInstance().sendPasswordResetEmail(email).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val snackbar =
                        Snackbar.make(view, "Email de recuperação enviado!", Snackbar.LENGTH_LONG)
                    snackbar.setBackgroundTint(Color.GREEN)
                    snackbar.show()
                    finish()
                    val activityScope = CoroutineScope(Dispatchers.Main)
                    activityScope.launch {
                        delay(2000)
                        finish()
                    }

                }

            }
                .addOnFailureListener {
                    val snackbar =
                        Snackbar.make(
                            view,
                            "Erro ao enviar o email de recuperação!",
                            Snackbar.LENGTH_LONG
                        )
                    snackbar.setBackgroundTint(Color.RED)
                    snackbar.show()
                }

        }
    }
}