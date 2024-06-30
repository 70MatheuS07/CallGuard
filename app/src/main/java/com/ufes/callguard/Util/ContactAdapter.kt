package com.ufes.callguard.Util

import android.content.Context
import android.database.Cursor
import android.provider.ContactsContract
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ufes.callguard.Class.Contact
import com.ufes.callguard.R

class ContactAdapter(
    private val context: Context,
    private val contactList: List<Contact>,
    private val onItemClick: (Contact) -> Unit
) : RecyclerView.Adapter<ContactAdapter.ContactViewHolder>() {

    class ContactViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val contactName: TextView = itemView.findViewById(R.id.contactName)
        val contactNumber: TextView = itemView.findViewById(R.id.contactNumber)
        val contactImage: ImageView = itemView.findViewById(R.id.contactImage)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.contact_list, parent, false)
        return ContactViewHolder(view)
    }

    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        val contact = contactList[position]

        holder.contactName.text = contact.getName()
        holder.contactNumber.text = contact.getNumber()

        if (isNumberInContacts(contact.getNumber())) {
            holder.contactImage.setImageResource(R.drawable.baseline_person_green_24)
        } else {
            holder.contactImage.setImageResource(R.drawable.baseline_person_24)
        }

        holder.itemView.setOnClickListener {
            onItemClick(contact)
        }
    }

    override fun getItemCount() = contactList.size

    private fun isNumberInContacts(number: String): Boolean {
        val uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI
        val projection = arrayOf(ContactsContract.CommonDataKinds.Phone.NUMBER)
        val cursor: Cursor? = context.contentResolver.query(uri, projection, null, null, null)
        cursor?.use {
            val numberIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
            while (it.moveToNext()) {
                val contactNumber = it.getString(numberIndex)?.replace(Regex("[^0-9]"), "")
                if (contactNumber == number) {
                    return true
                }
            }
        }
        return false
    }

}
