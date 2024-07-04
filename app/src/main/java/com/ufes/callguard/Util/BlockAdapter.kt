package com.ufes.callguard.Util

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ufes.callguard.Class.BlockedContactModel
import com.ufes.callguard.R

class BlockAdapter(private val blockList: List<BlockedContactModel>, private val listener: OnItemClickListener) : RecyclerView.Adapter<BlockAdapter.BlockViewHolder>() {

    interface OnItemClickListener {
        fun onItemClick(contact: BlockedContactModel)
    }

    inner class BlockViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        val blockedNameTextView: TextView = itemView.findViewById(R.id.blockedNameTextView)
        val blockedNumberTextView: TextView = itemView.findViewById(R.id.blockedNumberTextView)

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                listener.onItemClick(blockList[position])
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BlockViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.block_list, parent, false)
        return BlockViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: BlockViewHolder, position: Int) {
        val currentItem = blockList[position]
        holder.blockedNameTextView.text = currentItem.name
        holder.blockedNumberTextView.text = currentItem.number
    }

    override fun getItemCount() = blockList.size
}
