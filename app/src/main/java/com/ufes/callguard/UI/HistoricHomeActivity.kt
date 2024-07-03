package com.ufes.callguard.UI

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.CallLog
import android.provider.ContactsContract
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ufes.callguard.Class.Contact
import com.ufes.callguard.R
import com.ufes.callguard.Util.ContactAdapter

/**
 * Activity responsável por exibir o histórico de chamadas.
 */
class HistoricHomeActivity : AppCompatActivity() {
    private val REQUEST_CONTACTS_AND_CALL_LOG_PERMISSION = 1

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_historic_home)

        checkPermissions()
    }

    /**
     * Verifica as permissões necessárias para acessar os contatos e o histórico de chamadas.
     */
    @RequiresApi(Build.VERSION_CODES.O)
    private fun checkPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)
            != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CALL_LOG)
            != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)
            != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(this, Manifest.permission.ANSWER_PHONE_CALLS)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                arrayOf(
                    Manifest.permission.READ_CONTACTS,
                    Manifest.permission.READ_CALL_LOG,
                    Manifest.permission.READ_PHONE_STATE,
                    Manifest.permission.ANSWER_PHONE_CALLS
                ),
                REQUEST_CONTACTS_AND_CALL_LOG_PERMISSION
            )
        } else {
            getContactsAndCalls()
        }
    }

    /**
     * Trata o resultado das solicitações de permissões.
     *
     * @param requestCode O código de solicitação associado à solicitação de permissões.
     * @param permissions Um array de permissões solicitadas.
     * @param grantResults Um array de resultados de concessão para as permissões correspondentes.
     */
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        // Verifica se o requestCode corresponde ao código de solicitação de permissões para contatos e histórico de chamadas
        if (requestCode == REQUEST_CONTACTS_AND_CALL_LOG_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                getContactsAndCalls()
            } else {
                Toast.makeText(this, "Permissões para acessar contatos e histórico de chamadas foram negadas", Toast.LENGTH_SHORT).show()
            }
        }
    }

    /**
     * Obtém os contatos e os históricos de chamadas do dispositivo.
     */
    private fun getContactsAndCalls() {
        val contactsMap = getContacts()
        val callsList = getCallLogs(contactsMap)
        displayCalls(callsList)
    }

    /**
     * Obtém os contatos do dispositivo e armazena-os em um mapa com o número
     * do telefone como chave e o nome como valor.
     *
     * @return Um mapa com o número do telefone como chave e o nome como valor.
     */
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
            val nameIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
            val numberIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
            // Percorre o cursor e forma o map com o nome e o número do telefone
            while (it.moveToNext()) {
                val name = it.getString(nameIndex) ?: "Desconhecido"
                val phoneNumber = it.getString(numberIndex)?.replace(Regex("[^0-9]"), "") ?: "Sem número"
                contactsMap[phoneNumber] = name
            }
        }
        return contactsMap
    }

    /**
     * Obtém o histórico de chamadas do dispositivo.
     *
     * @param contactsMap Um mapa com o número do telefone como chave e o nome como valor.
     *
     * @return Uma lista de contatos com o nome e o número do telefone.
     */
    private fun getCallLogs(contactsMap: Map<String, String>): List<Contact> {
        val callsList = mutableListOf<Contact>()
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
            val numberIndex = it.getColumnIndex(CallLog.Calls.NUMBER)
            while (it.moveToNext()) {
                val phoneNumber = it.getString(numberIndex)?.replace(Regex("[^0-9]"), "") ?: "Sem número"
                //Se o número não está no mapa, o nome será "Desconhecido"
                val name = contactsMap[phoneNumber] ?: "Desconhecido"
                callsList.add(Contact(name, phoneNumber))
            }
        }
        return callsList
    }

    /**
     * Exibe o histórico de chamadas na tela.
     * @param callsList Uma lista de contatos com o nome e o número do telefone.
     */
    private fun displayCalls(callsList: List<Contact>) {
        val recyclerView = findViewById<RecyclerView>(R.id.contactRecyclerView)
        val noCallsTextView = findViewById<TextView>(R.id.noCallsTextView)

        if (callsList.isEmpty()) {
            noCallsTextView.visibility = TextView.VISIBLE
            recyclerView.visibility = RecyclerView.GONE
        } else {
            noCallsTextView.visibility = TextView.GONE
            recyclerView.visibility = RecyclerView.VISIBLE
            recyclerView.layoutManager = LinearLayoutManager(this)
            recyclerView.adapter = ContactAdapter(this, callsList) { contact ->
                val intent = Intent(this, HistoricDetailsActivity::class.java).apply {
                    putExtra("contact", contact)
                }
                startActivity(intent)
            }
        }
    }
}
