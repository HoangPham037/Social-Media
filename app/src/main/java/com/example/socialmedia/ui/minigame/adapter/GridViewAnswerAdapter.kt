package com.example.logoquiz.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.socialmedia.databinding.ItemButtonAnswerBinding
import com.example.socialmedia.model.AnswerModel


class GridViewAnswerAdapter : ListAdapter<AnswerModel,GridViewAnswerAdapter.ViewHolderAnswer>(DiffCallBackAnswer()){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderAnswer {

        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemButtonAnswerBinding.inflate(inflater)
        return ViewHolderAnswer(
           binding
        )
    }

    override fun onBindViewHolder(holder: ViewHolderAnswer, position: Int) {
        holder.binDataAnswer(getItem(position))
    }
    inner class ViewHolderAnswer(private val binding: ItemButtonAnswerBinding) : RecyclerView.ViewHolder(binding.root){

        fun binDataAnswer(answer : AnswerModel){
            itemView.run {
                if (answer.isCheck){
                   binding.btnAnswer.text = " "
                }else{
                    binding.btnAnswer.text = answer.label
                }
            }
        }
    }


}
class DiffCallBackAnswer : DiffUtil.ItemCallback<AnswerModel>(){
    override fun areItemsTheSame(oldItem: AnswerModel, newItem: AnswerModel): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: AnswerModel, newItem: AnswerModel): Boolean {
        return oldItem == newItem
    }

}