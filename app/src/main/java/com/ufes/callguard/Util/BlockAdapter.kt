package com.ufes.callguard.Util

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ufes.callguard.Class.BlockedContactModel
import com.ufes.callguard.R

/**
 * Adapter para exibir uma lista de contatos bloqueados em um RecyclerView.
 * @param blockList Lista de contatos bloqueados.
 * @param listener Lida com cliques nos itens da lista.
 */
class BlockAdapter(private val blockList: List<BlockedContactModel>, private val listener: OnItemClickListener) : RecyclerView.Adapter<BlockAdapter.BlockViewHolder>() {

    /**
     * Interface para lidar com cliques em itens da lista.
     */
    interface OnItemClickListener {
        fun onItemClick(contact: BlockedContactModel)
    }

    /**
     * ViewHolder para os itens da lista de contatos bloqueados.
     * @param itemView A visualização de cada item da lista.
     */
    inner class BlockViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        val blockedNameTextView: TextView = itemView.findViewById(R.id.blockedNameTextView)
        val blockedNumberTextView: TextView = itemView.findViewById(R.id.blockedNumberTextView)

        init {
            itemView.setOnClickListener(this)
        }

        /**
         * Método chamado quando um item é clicado.
         * @param v A visualização clicada.
         */
        override fun onClick(v: View?) {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                listener.onItemClick(blockList[position])
            }
        }
    }

    /**
     * Cria um novo ViewHolder quando não há visualizações existentes que possam ser reutilizadas.
     * @param parent O ViewGroup ao qual a nova visualização será adicionada após ser vinculada a uma posição do adapter.
     * @param viewType O tipo de visualização da nova visualização.
     * @return Um novo ViewHolder que possui uma visualização do tipo fornecido.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BlockViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.block_list, parent, false)
        return BlockViewHolder(itemView)
    }

    /**
     * Substitui o conteúdo de uma visualização.
     * @param holder O ViewHolder que deve ser atualizado para representar o conteúdo do item na posição fornecida no conjunto de dados.
     * @param position A posição do item dentro do conjunto de dados do adapter.
     */
    override fun onBindViewHolder(holder: BlockViewHolder, position: Int) {
        val currentItem = blockList[position]
        holder.blockedNameTextView.text = currentItem.name
        holder.blockedNumberTextView.text = currentItem.number
    }

    /**
     * Retorna o tamanho da lista.
     * @return O tamanho da lista de contatos bloqueados.
     */
    override fun getItemCount() = blockList.size
}
