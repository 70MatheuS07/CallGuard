package com.ufes.callguard.Util

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.widget.Button
import com.ufes.callguard.R

class ShowDialogs {
    object DialogUtils {

        fun showBlockDialog(context: Context, phoneNumber: String, onBlock: () -> Unit) {
            val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_box_block, null)

            val btnBlock = dialogView.findViewById<Button>(R.id.btn_block)
            val btnCancel = dialogView.findViewById<Button>(R.id.btn_cancel)

            val alertDialog = AlertDialog.Builder(context)
                .setView(dialogView)
                .create()

            btnCancel.setOnClickListener {
                alertDialog.dismiss()
            }

            btnBlock.setOnClickListener {
                onBlock()
                alertDialog.dismiss()
            }

            alertDialog.show()
        }

        fun showMessageDialog(context: Context, message: String) {
            AlertDialog.Builder(context)
                .setMessage(message)
                .setPositiveButton("OK", null)
                .create()
                .show()
        }
    }
}