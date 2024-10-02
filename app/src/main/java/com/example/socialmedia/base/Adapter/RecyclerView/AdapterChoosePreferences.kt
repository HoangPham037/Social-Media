package com.example.socialmedia.base.Adapter.RecyclerView

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.socialmedia.R

class AdapterChoosePreferences() : RecyclerView.Adapter<AdapterChoosePreferences.ViewHolder>() {

    var list: List<Itme> = listOf()
    lateinit var mlistener: onClickListener

    interface onClickListener {
        fun onItemClickListener(position: Int)
    }

    fun setOnItemClickListener(listener: onClickListener) {
        mlistener = listener
    }

    inner class ViewHolder(item: View) : RecyclerView.ViewHolder(item) {
        val hien = item.findViewById<AppCompatTextView>(R.id.text1)

        init {
            item.setOnClickListener {
                mlistener.onItemClickListener(adapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_for_rcv, parent, false)
        )
    }

    @SuppressLint("ResourceAsColor")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val pos = list[position]
        holder.hien.text = pos.name

        if (pos.choose1) {
            val orangeAppColor = ContextCompat.getColor(holder.hien.context, R.color.color_orange_app)
            holder.hien.setBackgroundColor(orangeAppColor)
        } else {
            holder.hien.setBackgroundColor(Color.WHITE)
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun submitList(newList: List<Itme>) {
        list = newList
        notifyDataSetChanged()
    }

    fun getSelectedItems2(): HashMap<String, String> {
        val selectedItem = HashMap<String, String>()
        val listChoose = mutableListOf<String>()
        for (item in list) {
            if (item.choose1) {
                listChoose.add(item.name)
                checklist(listChoose,selectedItem)
            }
        }
        return selectedItem
    }

    fun getSelectedItems() : List<String> {
        val listChoose = mutableListOf<String>()
        for (item in list) {
            if (item.choose1) {
                listChoose.add(item.name)
            }
        }
        return listChoose
    }

    fun checklist(list1: List<String>, selectedItem: HashMap<String, String>) {
        for ((index, item) in list1.withIndex()) {
            selectedItem["key$index"] = item
        }
    }
}

data class Itme(val name: String, var choose1: Boolean = false)



