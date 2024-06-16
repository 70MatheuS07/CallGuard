package com.ufes.callguard.UI

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.CallLog
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
    private val REQUEST_CALL_LOG_PERMISSION = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_historic_home)

        // Iniciar o processo de verificação de permissão e obtenção do histórico de chamadas
        checkCallLogPermission()
    }

    private fun checkCallLogPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CALL_LOG)
            != PackageManager.PERMISSION_GRANTED) {
            // Permissão não concedida
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.READ_CALL_LOG),
                REQUEST_CALL_LOG_PERMISSION)
        } else {
            // Permissão já concedida
            getCallLog()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_CALL_LOG_PERMISSION -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    // Permissão concedida
                    getCallLog()
                } else {
                    // Permissão negada
                    Toast.makeText(this, "Permissão para acessar o histórico de chamadas foi negada", Toast.LENGTH_SHORT).show()
                }
                return
            }
            // Adicione outros códigos de solicitação, se necessário
        }
    }

    @SuppressLint("Range")
    private fun getCallLog() {
        val callLogList = mutableListOf<Pair<String, String>>()
        val contentResolver = contentResolver
        val cursor = contentResolver.query(
            CallLog.Calls.CONTENT_URI,
            null, null, null, CallLog.Calls.DATE + " DESC"
        )

        cursor?.let {
            while (cursor.moveToNext()) {
                val name = cursor.getString(cursor.getColumnIndex(CallLog.Calls.CACHED_NAME)) ?: "Unknown"
                val phoneNumber = cursor.getString(cursor.getColumnIndex(CallLog.Calls.NUMBER))
                callLogList.add(Pair(name, phoneNumber))
            }
            cursor.close()
        }

        // Exibir a lista de histórico de chamadas ou a mensagem "Nenhuma chamada"
        displayCallLog(callLogList)
    }

    private fun displayCallLog(callLogList: List<Pair<String, String>>) {
        val recyclerView = findViewById<RecyclerView>(R.id.contactRecyclerView)
        val noCallsTextView = findViewById<TextView>(R.id.noCallsTextView)

        if (callLogList.isEmpty()) {
            noCallsTextView.visibility = TextView.VISIBLE
            recyclerView.visibility = RecyclerView.GONE
        } else {
            noCallsTextView.visibility = TextView.GONE
            recyclerView.visibility = RecyclerView.VISIBLE
            recyclerView.layoutManager = LinearLayoutManager(this)
            recyclerView.adapter = ContactAdapter(callLogList)
        }
    }
}