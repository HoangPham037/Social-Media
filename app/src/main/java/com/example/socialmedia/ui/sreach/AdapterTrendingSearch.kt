package com.example.socialmedia.ui.sreach

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.socialmedia.R

class AdapterTrendingSearch(context : Context) : RecyclerView.Adapter<AdapterTrendingSearch.Viewholder>() {

    var list = listOf<ItemTrendingSearch>()
    inner class Viewholder(itemview: View): RecyclerView.ViewHolder(itemview) {
        val image = itemview.findViewById<ImageView>(R.id.imgtrendingsearch)
        val title = itemview.findViewById<TextView>(R.id.tittrendingsearch)
        val keytrending = itemview.findViewById<TextView>(R.id.keytrendingsearch)
        val postnumber = itemview.findViewById<TextView>(R.id.posttrendingsearch)
    }
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AdapterTrendingSearch.Viewholder {
        return Viewholder(LayoutInflater.from(parent.context).inflate(R.layout.item_for_trending_search,parent,false))
    }

    override fun onBindViewHolder(holder: AdapterTrendingSearch.Viewholder, position: Int) {
        val pos = list[position]
    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun submitlist(list1: List<ItemTrendingSearch>) {
        list = emptyList()
        list = list1
        notifyDataSetChanged()
    }

}

data class ItemTrendingSearch(
    val avt: String?= null,
    val title: String?=null,
    val keyword: String?=null,
    val post: String?=null
)