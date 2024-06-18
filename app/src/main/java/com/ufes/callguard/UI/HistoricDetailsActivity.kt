package com.ufes.callguard.UI

import android.app.AlertDialog
import android.app.Dialog
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
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

        val contact = intent.getParcelableExtra<Contact>("contact", Contact::class.java)
        binding.textName.setText(contact?.getName())
        binding.textNumber.setText(contact?.getNumber())

        binding.ButtonBlock.setOnClickListener {
            contact?.getNumber()?.let { phoneNumber ->
                showBlockDialog(this, phoneNumber) {

                    showMessageDialog(this, "Número bloqueado: $phoneNumber")
                }
            }
        }
    }

