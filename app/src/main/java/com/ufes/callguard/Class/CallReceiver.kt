package com.ufes.callguard.Class

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import android.telephony.TelephonyManager
import android.util.Log
import androidx.core.app.ActivityCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.ufes.callguard.Util.BlockNumberCallback
import com.ufes.callguard.Util.ShowDialogs.DialogUtils.showReportPopup

/**
 * Classe responsável por receber as chamadas que serão recebidas e possivelmente bloqueá-las.
 */
class CallReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == TelephonyManager.ACTION_PHONE_STATE_CHANGED) {
            val state = intent.getStringExtra(TelephonyManager.EXTRA_STATE)

            if (state == TelephonyManager.EXTRA_STATE_RINGING) {
                val incomingNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER)
                incomingNumber?.let {
                    checkAndBlockNumber(context, it, object : BlockNumberCallback {
                        override fun onNumberBlocked() {
                            // O número foi rejeitado, não precisamos checar os reports
                        }

                        override fun onNumberNotBlocked() {
                            // O número não foi rejeitado, então precisamos checar os reports
                            checkReports(context, incomingNumber)
                        }
                    })
                }
            }
        }
    }

    /**
     * Método responsável por checar os reports do número que está ligando e exibir o popup de report.
     * @param context ambiente da aplicação.
     * @param incomingNumber número que está ligando.
     */
    private fun checkReports(context: Context?, incomingNumber: String) {
        val db = FirebaseFirestore.getInstance()
        val reportRef = db.collection("reports").document(incomingNumber)

        reportRef.get().addOnSuccessListener { document ->
            if (document.exists()) {
                val report = document.toObject(ContactReport::class.java)
                report?.let {
                    if (context != null) {
                        if (Settings.canDrawOverlays(context)) {
                            showReportPopup(context, report)
                        } else {
                            // Solicitando a permissão de sobreposição
                            val intent = Intent(
                                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                                Uri.parse("package:${context.packageName}")
                            )
                            context.startActivity(intent)
                        }
                    }
                }
            }
        }.addOnFailureListener { e ->
            Log.e("CallReceiver", "Erro ao obter o documento", e)
        }
    }

    /**
     * Método responsável por checar se o número está na lista de bloqueio do usuário, se estiver,
     * rejeitamos a chamada.
     * @param context ambiente da aplicação.
     * @param incomingNumber número que está ligando.
     * @param callback Decide entre OnblockNumber ou OnNumberNotBlocked.
     */
    private fun checkAndBlockNumber(context: Context?, incomingNumber: String, callback: BlockNumberCallback) {
        val db = FirebaseFirestore.getInstance()
        val userId = FirebaseAuth.getInstance().currentUser?.uid.toString()

        db.collection("usuario").document(userId)
            .get()
            .addOnSuccessListener { document ->
                var blocked = false

                if (document != null) {
                    val user = document.toObject(UserModel::class.java)
                    val blockList = document.get("blockList") as? List<Map<String, Any>>

                    // Verificar a lista de bloqueio do usuário
                    if (blockList != null) {
                        for (contact in blockList) {
                            val number = contact["number"] as? String
                            if (number != null && number == incomingNumber) {
                                rejectCall(context)
                                blocked = true
                                Log.d("CallReceiver", "Ligação bloqueada de: $incomingNumber")
                                callback.onNumberBlocked()
                                return@addOnSuccessListener
                            }
                        }
                    }

                    // Verificar se o usuário possui muitos relatórios
                    if (user != null && user.getHighReports()) {
                        db.collection("high_reports").document(incomingNumber)
                            .get()
                            .addOnSuccessListener { reportDoc ->
                                if (reportDoc.exists()) {
                                    rejectCall(context)
                                    blocked = true
                                    Log.d("CallReceiver", "Ligação bloqueada pelos reports: $incomingNumber")
                                    callback.onNumberBlocked()
                                } else {
                                    checkFriendsBlockList(db, user, incomingNumber, context, callback)
                                }
                            }
                            .addOnFailureListener { e ->
                                Log.e("CallReceiver", "Erro ao checar reports", e)
                                checkFriendsBlockList(db, user, incomingNumber, context, callback)
                            }
                    } else {
                        //Verifica o compartilhamento da lista de bloqueio
                        checkFriendsBlockList(db, user!!, incomingNumber, context, callback)
                    }
                } else {
                    callback.onNumberNotBlocked()
                }
            }
            .addOnFailureListener { e ->
                Log.e("CallReceiver", "Erro ao checar números bloqueados", e)
                callback.onNumberNotBlocked()
            }
    }

    /**
     * Método responsável por checar se o número está na lista de bloqueio de amigos habilitados para
     * o compartilhamento de lista de bloqueio, caso sim, rejeitamos a chamada.
     *  @param db banco de dados do Firebase.
     *  @param user usuário logado.
     *  @param incomingNumber número que está ligando.
     *  @param context ambiente da aplicação.
     *  @param callback Decide entre OnblockNumber ou OnNumberNotBlocked.
     */
    private fun checkFriendsBlockList(db: FirebaseFirestore, user: UserModel, incomingNumber: String, context: Context?, callback: BlockNumberCallback) {
        val amigoList = user.getAmigoList()

        val selectedFriends = amigoList.filter { it.isSelected }

        if (selectedFriends.isEmpty()) {
            callback.onNumberNotBlocked()
            return
        }

        var pendingRequests = selectedFriends.size
        var blocked = false

        selectedFriends.forEach { friend ->
            db.collection("usuario").whereEqualTo("name", friend.userName)
                .get()
                .addOnSuccessListener { documents ->
                    if (documents != null && !documents.isEmpty) {
                        val friendDoc = documents.first()
                        val friendBlockList = friendDoc.get("blockList") as? List<Map<String, Any>>

                        if (friendBlockList != null) {
                            for (contact in friendBlockList) {
                                val number = contact["number"] as? String
                                if (number != null && number == incomingNumber) {
                                    rejectCall(context)
                                    blocked = true
                                    Log.d("CallReceiver", "ligacao rejeitada pela lista de bloqueio de amigos: $incomingNumber")
                                    callback.onNumberBlocked()
                                    return@addOnSuccessListener
                                }
                            }
                        }
                    }

                    if (!blocked && --pendingRequests == 0) {
                        callback.onNumberNotBlocked()
                    }
                }
                .addOnFailureListener { e ->
                    Log.e("CallReceiver", "Erro ao checar a lista de bloqueio de amigos", e)
                    if (!blocked && --pendingRequests == 0) {
                        callback.onNumberNotBlocked()
                    }
                }
        }
    }

    /**
     * Método responsável por rejeitar a chamada.
     * @param context ambiente da aplicação.
     *
     */
    private fun rejectCall(context: Context?) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
            val telecomManager =
                context?.getSystemService(Context.TELECOM_SERVICE) as? android.telecom.TelecomManager
            if (context?.let {
                    ActivityCompat.checkSelfPermission(
                        it,
                        Manifest.permission.ANSWER_PHONE_CALLS
                    )
                } != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
            telecomManager?.endCall()
        }
    }


}