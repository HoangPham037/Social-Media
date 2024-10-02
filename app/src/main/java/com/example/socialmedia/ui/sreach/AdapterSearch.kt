package com.example.socialmedia.ui.sreach

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.compose.ui.graphics.Color
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.socialmedia.R
import com.example.socialmedia.base.utils.goneIf
import com.example.socialmedia.model.ItemHome
import com.example.socialmedia.model.Notification
import com.example.socialmedia.model.Profile
import com.vanniktech.ui.setBackgroundColor
import com.vanniktech.ui.setTextColor
import de.hdodenhof.circleimageview.CircleImageView

class AdapterSearch(val context: Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var listHot = listOf<ItemHome>()
    var listUsers = listOf<Profile>()

    private var mListenter: OnItemClickListener? = null
    private var buttonClickListener: OnButtonClickListener? = null

    private val VIEW_TYPE_1 = 1
    private val VIEW_TYPE_2 = 2

    interface OnItemClickListener {
        fun onitemclick(position: Int)
    }

    interface OnButtonClickListener {
        fun onButtonClick(position: Int)
    }

    fun setOnButtonClickListener(listener: OnButtonClickListener) {
        buttonClickListener = listener
    }

    fun setonitemclicklistener(listener: OnItemClickListener) {
        mListenter = listener
    }

    override fun getItemViewType(position: Int): Int {
        return when {
            position < listHot.size -> VIEW_TYPE_1
            else -> VIEW_TYPE_2
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_1 -> AdapterSearch.ViewHolderType1(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_news_feed, parent, false),
                mListenter!!
            )

            VIEW_TYPE_2 -> AdapterSearch.ViewHolderType2(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_for_search_users, parent, false),
                mListenter!!,buttonClickListener!!,listUsers
            )

            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder.itemViewType) {
            VIEW_TYPE_1 -> (holder as AdapterSearch.ViewHolderType1).bind(listHot[position])
            VIEW_TYPE_2 -> (holder as AdapterSearch.ViewHolderType2).bind(listUsers[position])
        }
    }

    override fun getItemCount(): Int {
        return listHot.size + listUsers.size
    }

    class ViewHolderType1(itemView: View, listener: OnItemClickListener) :
        RecyclerView.ViewHolder(itemView) {

        val userAvt = itemView.findViewById<CircleImageView>(R.id.iv_avatar)
        val userName = itemView.findViewById<TextView>(R.id.tv_user_name)
        val content = itemView.findViewById<TextView>(R.id.tv_description)
        val cvMeida = itemView.findViewById<RecyclerView>(R.id.userDes)


        val btnfls = itemView.findViewById<AppCompatButton>(R.id.btnfollow)
        val btnfollowing = itemView.findViewById<AppCompatButton>(R.id.btnFollowing)

        init {
            itemView.setOnClickListener {
                listener.onitemclick(adapterPosition)
            }
        }

        fun bind(item: ItemHome) {
            Glide.with(itemView).load(item.urlImage).into(userAvt)
        }
    }

    class ViewHolderType2(itemView: View, listener: OnItemClickListener,btnlistener: OnButtonClickListener,private val list: List<Profile>) :
        RecyclerView.ViewHolder(itemView) {

        val userAvt = itemView.findViewById<CircleImageView>(R.id.userAvtSearch)
        val userName = itemView.findViewById<TextView>(R.id.userName)
        val userId = itemView.findViewById<TextView>(R.id.userid)
        val userDes = itemView.findViewById<TextView>(R.id.userDes)
        val btnfls = itemView.findViewById<AppCompatButton>(R.id.btnfollow)
        val btnfollowing = itemView.findViewById<AppCompatButton>(R.id.btnFollowing)
        val space : androidx.legacy.widget.Space = itemView.findViewById(R.id.space2)

        init {
            itemView.setOnClickListener {
                listener.onitemclick(adapterPosition)
            }
            btnfls.setOnClickListener {
                btnlistener.onButtonClick(adapterPosition)
            }
        }
        @SuppressLint("ResourceAsColor")
        fun bind(item: Profile) {
            space.goneIf(position != list.size - 1)
            userName.text = item.name
            userId.text = item.email
            userDes.text = item.description

            if(item.avtPath!=null){
                Glide.with(itemView)
                    .load(item.avtPath)
                    .into(userAvt)
            } else {
                Glide.with(itemView)
                    .load(R.drawable.avatar)
                    .into(userAvt)
            }

            val orangeAppColor = ContextCompat.getColor(btnfls.context, R.color.color_orange_app)
            val white = ContextCompat.getColor(btnfls.context, R.color.white)
            if (!item.isFollow){
                btnfls.setBackgroundResource(R.drawable.bg_round_boder_btn)
                btnfls.setTextColor(white)
                btnfls.setText(R.string.following)
            } else {
                btnfls.setText(R.string.follow_back)
                btnfls.setTextColor(orangeAppColor)
                btnfls.setBackgroundResource(R.drawable.bg_followong)
            }
            // Assuming textView is your TextView reference
            userDes.maxLines = 2
            userDes.ellipsize = TextUtils.TruncateAt.END


        }
    }

    fun updateFollowStatus(profile: Profile) {
        val index = listUsers.indexOfFirst { it.id == profile.id }
        if (index != -1) {
            listUsers[index].isFollow = profile.isFollow
            notifyItemChanged(index)
        }
    }
}