package com.ufes.callguard.UI

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.ufes.callguard.Class.Contact
import com.ufes.callguard.Class.ContactReport
import com.ufes.callguard.Class.UserModel
import com.ufes.callguard.R
import com.ufes.callguard.Util.ReportReasonCallback
import com.ufes.callguard.Util.ShowDialogs.DialogUtils.showBlockDialog
import com.ufes.callguard.Util.ShowDialogs.DialogUtils.showMessageDialog
import com.ufes.callguard.Util.ShowDialogs.DialogUtils.showReportDialog
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
        if (!Settings.canDrawOverlays(this)) {
            val intent = Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:${packageName}")
            )
            startActivity(intent)
        }

        binding.textName.text = contact.getContactName()
        binding.textNumber.text = contact.getContactNumber()

        binding.ButtonBlock.setOnClickListener {
            contact.getContactNumber()?.let { phoneNumber ->
                showBlockDialog(this, phoneNumber) {
                    addContactToBlockList(
                        FirebaseAuth.getInstance().currentUser!!.uid,
                        contact,
                        this
                    )
                }
            }
        }

        binding.ButtonReport.setOnClickListener {
            showReportDialog(this, object : ReportReasonCallback {
                override fun onReasonSelected(reasonIndex: Int) {
                    val contactReport = ContactReport(contact.getContactName(), contact.getContactNumber(), mutableListOf(0, 0, 0, 0))
                    addContactToReportedList(contactReport, this@HistoricDetailsActivity, reasonIndex)
                }
            })
        }

        binding.ButtonCall.setOnClickListener {
            val phoneNumber = contact.getContactNumber()
            if (phoneNumber != null) {
                makePhoneCall(phoneNumber)
            }
        }
    }

    private fun makePhoneCall(phoneNumber: String) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CALL_PHONE), 1)
        } else {
            val intent = Intent(Intent.ACTION_CALL)
            intent.data = Uri.parse("tel:$phoneNumber")
            startActivity(intent)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                val phoneNumber = binding.textNumber.text.toString()
                makePhoneCall(phoneNumber)
            } else {
                Toast.makeText(this, "Permission DENIED", Toast.LENGTH_SHORT).show()
            }
        }
    }

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
                                    "Número bloqueado: ${contact.getContactNumber()}"
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

    private fun addContactToReportedList(contact: ContactReport, context: Context, reasonIndex: Int) {
        val db = FirebaseFirestore.getInstance()
        val reportRef = db.collection("reports").document(contact.number)

        reportRef.get().addOnSuccessListener { document ->
            if (document.exists()) {
                val existingReport = document.toObject(ContactReport::class.java)
                existingReport?.type?.let {
                    it[reasonIndex] = it[reasonIndex] + 1
                    reportRef.set(existingReport.toHashMap())
                        .addOnSuccessListener {
                            Log.d("Firestore", "Report atualizado com sucesso")
                            checkAndAddToHighReports(existingReport)
                        }
                        .addOnFailureListener { e ->
                            Log.w("Firestore", "Erro ao atualizar o report", e)
                        }
                }
            } else {
                val initialType = mutableListOf(0, 0, 0, 0)
                initialType[reasonIndex] = 1
                val newReport = ContactReport(contact.name, contact.number, initialType)
                reportRef.set(newReport.toHashMap())
                    .addOnSuccessListener {
                        Log.d("Firestore", "Report adicionado com sucesso")
                        checkAndAddToHighReports(newReport)
                    }
                    .addOnFailureListener { e ->
                        Log.w("Firestore", "Erro ao adicionar o report", e)
                    }
            }
        }.addOnFailureListener { e ->
            Log.w("Firestore", "Erro ao obter o documento", e)
        }
    }

    private fun checkAndAddToHighReports(contact: ContactReport) {
        val totalReports = contact.type.sum()
        if (totalReports > 100) {
            val db = FirebaseFirestore.getInstance()
            val highReportRef = db.collection("high_reports").document(contact.number)

            highReportRef.set(contact.toHashMap())
                .addOnSuccessListener {
                    Log.d("Firestore", "Report adicionado à coleção de high_reports com sucesso")
                }
                .addOnFailureListener { e ->
                    Log.w("Firestore", "Erro ao adicionar o report à coleção de high_reports", e)
                }
        }
    }
}
