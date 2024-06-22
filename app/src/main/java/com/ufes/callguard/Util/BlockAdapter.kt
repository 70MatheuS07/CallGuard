package com.ufes.callguard.Util

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ufes.callguard.Class.BlockedContactModel
import com.ufes.callguard.R


class BlockAdapter(private val blockList: List<BlockedContactModel>) :
    RecyclerView.Adapter<BlockAdapter.BlockViewHolder>() {

    class BlockViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val blockedNameTextView: TextView = itemView.findViewById(R.id.blockedNameTextView)
        val blockedNumberTextView: TextView = itemView.findViewById(R.id.blockedNumberTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BlockViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.block_list, parent, false)
        return BlockViewHolder(view)
    }

    override fun onBindViewHolder(holder: BlockViewHolder, position: Int) {
        val blockedContact = blockList[position]
        holder.blockedNameTextView.text = blockedContact.name
        holder.blockedNumberTextView.text = blockedContact.number
    }

    override fun getItemCount() = blockList.size
}