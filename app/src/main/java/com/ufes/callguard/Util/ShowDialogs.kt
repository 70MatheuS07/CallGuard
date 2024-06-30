package com.ufes.callguard.Util

import android.app.AlertDialog
import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.WindowManager
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import com.ufes.callguard.Class.Contact
import com.ufes.callguard.Class.ContactReport
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

        fun showReportPopup(contact: Contact, context: Context, callback: ReportReasonCallback) {
            val builder = AlertDialog.Builder(context)
            val inflater = LayoutInflater.from(context)
            val view = inflater.inflate(R.layout.report_reason_dialog, null)
            builder.setView(view)
            val dialog = builder.create()

            val spinnerReportReason = view.findViewById<Spinner>(R.id.spinner_report_reason)
            val buttonReport = view.findViewById<Button>(R.id.button_report)

            val adapter = ArrayAdapter.createFromResource(
                context,
                R.array.report_reasons,
                android.R.layout.simple_spinner_item
            )
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerReportReason.adapter = adapter

            buttonReport.setOnClickListener {
                val selectedReason = spinnerReportReason.selectedItemPosition
                callback.onReasonSelected(selectedReason)
                dialog.dismiss()
            }

            dialog.show()
        }


        fun showReportPopup(context: Context, report: ContactReport) {
            val builder = AlertDialog.Builder(context)
            val inflater = LayoutInflater.from(context)
            val view = inflater.inflate(R.layout.dialog_reports, null)
            builder.setView(view)
            val dialog = builder.create()

            val tvReportTitle = view.findViewById<TextView>(R.id.tv_report_title)
            val tvReportReason = view.findViewById<TextView>(R.id.tv_report_reason)
            val buttonOk = view.findViewById<Button>(R.id.button_ok)

            val reasonIndex = report.type.indexOf(report.type.maxOrNull())
            val reasons = context.resources.getStringArray(R.array.report_reasons)
            val mostReportedReason = reasons.getOrNull(reasonIndex) ?: "Desconhecido"

            tvReportReason.text = "Motivo: $mostReportedReason"

            buttonOk.setOnClickListener {
                dialog.dismiss()
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                dialog.window?.setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY)
            } else {
                dialog.window?.setType(WindowManager.LayoutParams.TYPE_PHONE)
            }
            dialog.show()
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