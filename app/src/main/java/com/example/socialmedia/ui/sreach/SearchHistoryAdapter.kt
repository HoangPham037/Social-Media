package com.example.socialmedia.ui.sreach

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.socialmedia.R

class SearchHistoryAdapter(val context: Context) : RecyclerView.Adapter<SearchHistoryAdapter.Viewholder>() {
    var mlist = mutableListOf<String>()

    lateinit var mlistener: onItemClickListener

    interface onItemClickListener {
        fun onItemClick(position: Int)
    }

    fun setOnItemClickListener(listener: onItemClickListener){
        mlistener = listener
    }

    inner class Viewholder(val itemView: View,listener: onItemClickListener ): RecyclerView.ViewHolder(itemView) {
        val mtxt = itemView.findViewById<TextView>(R.id.txtHistory)
        init {
            itemView.setOnClickListener {
                mlistener.onItemClick(adapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Viewholder {
        return Viewholder(LayoutInflater.from(parent.context).inflate(R.layout.item_for_history_search,parent,false),mlistener)
    }

    override fun getItemCount(): Int {
        return mlist.size
    }

    override fun onBindViewHolder(holder: Viewholder, position: Int) {
        val txt = mlist[position]
        holder.mtxt.text = txt
    }

    fun submitList2(newList: List<String>) {
        mlist.clear()
        mlist.addAll(newList)
        notifyDataSetChanged()
    }

}