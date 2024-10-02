package com.example.logoquiz.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.socialmedia.R
import com.example.socialmedia.databinding.ItemFlagBinding
import com.example.socialmedia.model.Level



class HomeAdapter : ListAdapter<Level, ViewHolderHome>(DiffCallBackHome()) {
    var listener : OnClickItem?= null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderHome {
        val layout = LayoutInflater.from(parent.context)
        val binding : ItemFlagBinding = DataBindingUtil.inflate(layout, R.layout.item_flag, parent, false)
        return ViewHolderHome(binding, listener)
    }

    override fun onBindViewHolder(holder: ViewHolderHome, position: Int) {
        holder.binData(getItem(position))
    }
    interface OnClickItem{
        fun onClickLevel(position: Int, level : Level)
    }
}
class ViewHolderHome(private val binding : ItemFlagBinding,
                     private val listener : HomeAdapter.OnClickItem?)
    : RecyclerView.ViewHolder(binding.root){
     fun binData(home : Level){
         itemView.run {
             binding.itemFlag = home
             binding.executePendingBindings()
             //binding.processBar.progress = home.process.toInt()
             binding.root.setOnClickListener {
                 listener?.onClickLevel(adapterPosition, home)
             }
         }
     }
}
class DiffCallBackHome : DiffUtil.ItemCallback<Level>(){
    override fun areItemsTheSame(oldItem: Level, newItem: Level) : Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: Level, newItem: Level): Boolean {
        return oldItem == newItem
    }

}