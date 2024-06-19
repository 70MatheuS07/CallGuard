package com.ufes.callguard.Class

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.telephony.TelephonyManager
import android.util.Log
import androidx.core.app.ActivityCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class CallReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == TelephonyManager.ACTION_PHONE_STATE_CHANGED) {
            val state = intent.getStringExtra(TelephonyManager.EXTRA_STATE)

            if (state == TelephonyManager.EXTRA_STATE_RINGING) {
                val incomingNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER)
                incomingNumber?.let {
                    checkAndBlockNumber(context, it)
                }
            }
        }
    }


    private fun checkAndBlockNumber(context: Context?, incomingNumber: String) {
        val db = FirebaseFirestore.getInstance()
        val userId = FirebaseAuth.getInstance().currentUser?.uid.toString()

        db.collection("usuario").document(userId)
            .get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val blockList = document.get("blockList") as? List<Map<String, Any>>
                    if (blockList != null) {
                        for (contact in blockList) {
                            val number = contact["number"] as? String
                            if (number != null && number == incomingNumber) {
                                rejectCall(context)
                                Log.d("CallReceiver", "Blocked call from: $incomingNumber")
                                break
                            }
                        }
                    }
                }
            }
            .addOnFailureListener { e ->
                Log.e("CallReceiver", "Error checking blocked numbers", e)
            }
    }


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
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return
            }
            telecomManager?.endCall()
        }
    }


}