package com.ufes.callguard.UI

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.CallLog
import android.provider.ContactsContract
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ufes.callguard.R
import com.ufes.callguard.Util.ContactAdapter

class HistoricHomeActivity : AppCompatActivity() {
    private val REQUEST_CONTACTS_AND_CALL_LOG_PERMISSION = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_historic_home)

        // Iniciar o processo de verificação de permissão e obtenção dos contatos e histórico de chamadas
        checkPermissions()
    }

    private fun checkPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)
            != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CALL_LOG)
            != PackageManager.PERMISSION_GRANTED) {
            // Permissão não concedida
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.READ_CONTACTS, Manifest.permission.READ_CALL_LOG),
                REQUEST_CONTACTS_AND_CALL_LOG_PERMISSION)
        } else {
            // Permissão já concedida
            getContactsAndCalls()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_CONTACTS_AND_CALL_LOG_PERMISSION -> {
                if (grantResults.isNotEmpty() && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                    // Permissão concedida
                    getContactsAndCalls()
                } else {
                    // Permissão negada
                    Toast.makeText(this, "Permissões para acessar contatos e histórico de chamadas foram negadas", Toast.LENGTH_SHORT).show()
                }
                return
            }
        }
    }

    private fun getContactsAndCalls() {
        val contactsMap = getContacts()
        val callsList = getCallLogs(contactsMap)
        displayCalls(callsList)
    }

    private fun getContacts(): Map<String, String> {
        val contactsMap = mutableMapOf<String, String>()
        val contentResolver = contentResolver
        val cursor = contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            arrayOf(
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Phone.NUMBER
            ),
            null,
            null,
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC"
        )

        cursor?.use {
            val nameIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
            val numberIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
            while (cursor.moveToNext()) {
                val name = cursor.getString(nameIndex) ?: "Desconhecido"
                val phoneNumber = cursor.getString(numberIndex)?.replace(Regex("[^0-9]"), "") ?: "Sem número"
                contactsMap[phoneNumber] = name
            }
        }
        return contactsMap
    }

    private fun getCallLogs(contactsMap: Map<String, String>): List<Pair<String, String>> {
        val callsList = mutableListOf<Pair<String, String>>()
        val contentResolver = contentResolver
        val cursor = contentResolver.query(
            CallLog.Calls.CONTENT_URI,
            arrayOf(
                CallLog.Calls.NUMBER,
                CallLog.Calls.DATE
            ),
            null,
            null,
            CallLog.Calls.DATE + " DESC"
        )

        cursor?.use {
            val numberIndex = cursor.getColumnIndex(CallLog.Calls.NUMBER)
            while (cursor.moveToNext()) {
                val phoneNumber = cursor.getString(numberIndex)?.replace(Regex("[^0-9]"), "") ?: "Sem número"
                val name = contactsMap[phoneNumber] ?: "Desconhecido"
                callsList.add(Pair(name, phoneNumber))
            }
        }
        return callsList
    }

    private fun displayCalls(callsList: List<Pair<String, String>>) {
        val recyclerView = findViewById<RecyclerView>(R.id.contactRecyclerView)
        val noCallsTextView = findViewById<TextView>(R.id.noCallsTextView)

        if (callsList.isEmpty()) {
            noCallsTextView.visibility = TextView.VISIBLE
            recyclerView.visibility = RecyclerView.GONE
        } else {
            noCallsTextView.visibility = TextView.GONE
            recyclerView.visibility = RecyclerView.VISIBLE
            recyclerView.layoutManager = LinearLayoutManager(this)
            recyclerView.adapter = ContactAdapter(callsList)
        }
    }
}