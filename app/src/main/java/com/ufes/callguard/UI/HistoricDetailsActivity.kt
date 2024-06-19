package com.ufes.callguard.UI

import android.app.AlertDialog
import android.app.Dialog
import android.content.ContentValues
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.provider.BlockedNumberContract
import android.util.Log
import android.view.LayoutInflater
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.ufes.callguard.Class.Contact
import com.ufes.callguard.Class.UserModel
import com.ufes.callguard.R
import com.ufes.callguard.Util.ShowDialogs.DialogUtils.showBlockDialog
import com.ufes.callguard.Util.ShowDialogs.DialogUtils.showMessageDialog
import com.ufes.callguard.databinding.ActivityHistoricDetailsBinding

class HistoricDetailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHistoricDetailsBinding

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHistoricDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        var contact: Contact
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            contact = intent.getParcelableExtra<Contact>("contact", Contact::class.java)!!
        } else {
            contact = intent.getParcelableExtra<Contact>("contact")!!
        }
        binding.textName.setText(contact?.getName())
        binding.textNumber.setText(contact?.getNumber())

        binding.ButtonBlock.setOnClickListener {
            contact?.getNumber()?.let { phoneNumber ->
                showBlockDialog(this, phoneNumber) {
                    addContactToBlockList(
                        FirebaseAuth.getInstance().currentUser!!.uid,
                        contact,
                        this
                    )

                    showMessageDialog(this, "Número bloqueado: $phoneNumber")
                }
            }
        }
    }


    /**
     * Função que adiciona o número bloqueado ao banco de dados do usuário
     * */
    private fun addContactToBlockList(userId: String, contact: Contact, context: Context) {
        val userDocRef = FirebaseFirestore.getInstance().collection("usuario").document(userId)

        userDocRef.get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val user = document.toObject(UserModel::class.java)
                    user?.let {
                        val blockList = it.getBlockList()
                        blockList.add(contact)
                        user.setBlockList(blockList)

                        userDocRef.set(user)
                            .addOnSuccessListener {
                                Log.d("Firestore", "Contact added to block list successfully")
                                showMessageDialog(
                                    context,
                                    "Número bloqueado: ${contact.getNumber()}"
                                )
                            }
                            .addOnFailureListener { e ->
                                Log.e("Firestore", "Error updating block list", e)
                            }
                    }
                } else {
                    Log.d("Firestore", "User document does not exist")
                }
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Error fetching user document", e)
            }
    }
}


