package com.ufes.callguard.UI

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.Task
import com.ufes.callguard.Class.UserModel
import com.ufes.callguard.databinding.ActivitySignupBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.firestore.FirebaseFirestore
import com.ufes.callguard.Util.PhoneNumberMask

/**
 * Activity que representa a tela de cadastro do usuário.
 */
class SignupActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignupBinding

    private val phoneNumberTextWatcher = PhoneNumberMask(12)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Adiciona o TextWatcher ao EditText do número de telefone
        binding.editNumberSignup.addTextChangedListener(phoneNumberTextWatcher)

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

    /**
     * Responsável por cadastrar o usuário no Firebase.
     * @param email E-mail do usuário.
     * @param pass Senha do usuário.
     * @param view View da activity.
     * @param user Nome do usuário.
     * @param number Número do usuário.
     */
    private fun CadastraUser(email: String, pass: String, view: View, user: String, number: String) {
        val database = FirebaseFirestore.getInstance()

        database.collection("usuario")
            .whereEqualTo("name", user)
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful && task.result.isEmpty) {
                    // Verifica se o número é único
                    database.collection("usuario")
                        .whereEqualTo("phone", number)
                        .get()
                        .addOnCompleteListener { taskNumber ->
                            if (taskNumber.isSuccessful && taskNumber.result.isEmpty) {
                                // Se user e number são únicos, criar o usuário
                                FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, pass)
                                    .addOnCompleteListener { cadastro ->
                                        if (cadastro.isSuccessful) {
                                            val usuario = UserModel(FirebaseAuth.getInstance().currentUser?.uid.toString(), user, number)
                                            SalvarDadosUser(usuario)
                                            val snackbar = Snackbar.make(view, "Sucesso ao cadastrar usuário", Snackbar.LENGTH_SHORT)
                                            snackbar.setBackgroundTint(Color.BLUE)
                                            snackbar.show()
                                            binding.editEmail.setText("")
                                            binding.editPass.setText("")
                                        } else {
                                            handleAuthErrors(cadastro, view)
                                        }
                                    }
                            } else {
                                val snackbar = Snackbar.make(view, "Número já cadastrado", Snackbar.LENGTH_LONG)
                                snackbar.setBackgroundTint(Color.RED)
                                snackbar.setTextColor(Color.BLACK)
                                snackbar.show()
                            }
                        }
                } else {
                    val snackbar = Snackbar.make(view, "Usuário já cadastrado", Snackbar.LENGTH_LONG)
                    snackbar.setBackgroundTint(Color.RED)
                    snackbar.setTextColor(Color.BLACK)
                    snackbar.show()
                }
            }
    }

    /**
     * Responsável por lidar com os erros de autenticação do Firebase.
     * @param cadastro Resultado da autenticação.
     * @param view View da activity.
     */
    private fun handleAuthErrors(cadastro: Task<AuthResult>, view: View) {
        var erro = ""
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

    /**
     * Responsável por criar a coleção usuário,caso ela não exista, no Firebase e salvar os dados do usuário.
     * @param usuario usuário a ser salvo no Firebase.
     */
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
