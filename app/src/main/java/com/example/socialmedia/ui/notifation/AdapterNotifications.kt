package com.example.socialmedia.ui.notifation

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableString
import android.text.style.StyleSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Space
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.socialmedia.R
import com.example.socialmedia.base.utils.goneIf
import com.example.socialmedia.model.Notification
import com.example.socialmedia.ui.sreach.AdapterSearch
import com.example.socialmedia.ui.utils.getLastTimeAgo
import com.example.socialmedia.ui.utils.getTimeAgo
import de.hdodenhof.circleimageview.CircleImageView

class AdapterNotifications(val context: Context,private val itemClickListener: AdapterNotifications.OnclickListener) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var list = listOf<Notification>()


    private var mListenter: OnclickListener? = null
    private var buttonClickListener: AdapterNotifications.OnButtonClickListener? = null

    init {
        mListenter = itemClickListener
    }

    private val VIEW_TYPE_1 = 1
    private val VIEW_TYPE_2 = 2
    private val VIEW_TYPE_3 = 3
    private val VIEW_TYPE_4 = 4
    private val VIEW_TYPE_5 = 5

    interface OnclickListener {
       // fun Onitemclick(position: Int)
        fun onCommentNotificationClick(notification: Notification)
        fun onFollowNotificationClick(notification: Notification)
        fun onLikeNotificationClick(notification: Notification)
    }

    interface OnButtonClickListener {
        fun onButtonClick(position: Int)
    }

    fun setOnButtonClickListener(listener: AdapterNotifications.OnButtonClickListener) {
        buttonClickListener = listener
    }

    fun setonitemclicklistner(listener: OnclickListener) {
        mListenter = listener
    }

    override fun getItemViewType(position: Int): Int {
        return when {
            list[position].type == "comment" -> VIEW_TYPE_1
            list[position].type == "follow" -> VIEW_TYPE_2
            list[position].type == "like" -> VIEW_TYPE_3
            list[position].type == "message" -> VIEW_TYPE_4
            list[position].type == "comment3" -> VIEW_TYPE_5
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_1 -> ViewHolderType1(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_for_notifications, parent, false),
                mListenter!!,parent.context,list
            )

            VIEW_TYPE_2 -> ViewHolderType2(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_for_notifications1, parent, false),
                mListenter!!,parent.context,list,buttonClickListener!!
            )

            VIEW_TYPE_3 -> ViewHolderType3(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_for_notifications, parent, false),
                mListenter!!,parent.context,list
            )
            // Add cases for the remaining view types
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder.itemViewType) {
            VIEW_TYPE_1 ->{
                (holder as ViewHolderType1).bind(list[position])
                holder.itemView.setOnClickListener {
                    itemClickListener.onCommentNotificationClick(list[position])
                }
            }
            VIEW_TYPE_2 -> {
                (holder as ViewHolderType2).bind(list[position])
                holder.itemView.setOnClickListener {
                    itemClickListener.onFollowNotificationClick(list[position])
                }
            }
            VIEW_TYPE_3 -> {
                (holder as ViewHolderType3).bind(list[position])
                holder.itemView.setOnClickListener {
                    itemClickListener.onLikeNotificationClick(list[position])
                }
            }
            VIEW_TYPE_4 -> (holder as ViewHolderType4).bind(list[position])
            VIEW_TYPE_5 -> (holder as ViewHolderType5).bind(list[position])
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun submitlist(listadd: List<Notification>) {
        list = listadd
        notifyDataSetChanged()
    }


    open class BaseViewHolder(itemView: View, listener: AdapterNotifications.OnclickListener) :
        RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.txtNotititle)
        val bgNoti: LinearLayout = itemView.findViewById(R.id.bg_Noti)
        val time: TextView = itemView.findViewById(R.id.timenoti)
        val avt : CircleImageView = itemView.findViewById(R.id.userAvt)

        init {
            itemView.setOnClickListener {
               // listener.Onitemclick(adapterPosition)
            }
        }
    }

    class ViewHolderType1(itemView: View, listener: AdapterNotifications.OnclickListener,val context: Context,private val list: List<Notification>) :
        BaseViewHolder(itemView, listener) {
        fun bind(item: Notification) {
            //space.goneIf(position != list.size - 1)
            if (item.isread) {
                bgNoti.setBackgroundColor(Color.WHITE)
            } else {
                bgNoti.setBackgroundColor(Color.parseColor("#FFF9E9"))
            }
            if(item.avtpath!="null"){
                Glide.with(itemView)
                    .load(item.avtpath)
                    .into(avt)
            } else {
                Glide.with(itemView)
                    .load(R.drawable.avatar)
                    .into(avt)
            }
            title.text = "${item.name} has comment your post"
            val spannableString = SpannableString(title.text)

            val startIndex = title.text.indexOf(item.name)
            val endIndex = startIndex + item.name.length

            val boldStyleSpan = StyleSpan(Typeface.BOLD)
            spannableString.setSpan(
                boldStyleSpan,
                startIndex,
                endIndex,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            title.text = spannableString

            val timeago = getLastTimeAgo(item.key.toLong())
            time.text = timeago
        }
    }

    class ViewHolderType2(itemView: View, listener: OnclickListener, val context: Context,private val list: List<Notification>,val btnListener: OnButtonClickListener) :
        RecyclerView.ViewHolder(itemView) {

        //val avt: ImageView = itemView.findViewById(R.id.userAvtNoti)
        val title: TextView = itemView.findViewById(R.id.txtNotititle1)
        val btnflback: AppCompatButton = itemView.findViewById(R.id.btnfollowback1)
        val bgNoti: ConstraintLayout = itemView.findViewById(R.id.bg_Noti1)
        val avt : CircleImageView = itemView.findViewById(R.id.userAvt1)
        val time: TextView = itemView.findViewById(R.id.timenoti1)
      //  val space : androidx.legacy.widget.Space = itemView.findViewById(R.id.spacespeaker1)

        @SuppressLint("ResourceAsColor")
        fun bind(item: Notification) {
           // space.goneIf(position != list.size - 1)
            if(item.avtpath!="null"){
                Glide.with(itemView)
                    .load(item.avtpath)
                    .into(avt)
            } else {
                Glide.with(itemView)
                    .load(R.drawable.avatar)
                    .into(avt)
            }
            title.text = "${item.name} started following you"
            val spannableString = SpannableString(title.text)

            val startIndex = title.text.indexOf(item.name)
            val endIndex = startIndex + item.name.length

            val boldStyleSpan = StyleSpan(Typeface.BOLD)
            spannableString.setSpan(
                boldStyleSpan,
                startIndex,
                endIndex,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            title.text = spannableString

            if (item.isread) {
                bgNoti.setBackgroundColor(Color.WHITE)
            } else {
                bgNoti.setBackgroundColor(Color.parseColor("#FFF9E9"))
            }

            val timeago = getLastTimeAgo(item.key.toLong())
            time.text = timeago


            val orangeAppColor = ContextCompat.getColor(btnflback.context, R.color.color_orange_app)

            if (item.ispostnotification){
                btnflback.setTextColor(orangeAppColor)
                btnflback.setBackgroundResource(R.drawable.bg_follow_btn)
                btnflback.setText(R.string.following)
            } else {
                btnflback.setText(R.string.follow_back)
                btnflback.setTextColor(orangeAppColor)
                btnflback.setBackgroundResource(R.drawable.bg_follow_btn)
            }

        }
        init {
            itemView.setOnClickListener {
                //listener.Onitemclick(adapterPosition)
            }
            btnflback.setOnClickListener {
                btnListener.onButtonClick(adapterPosition)
            }
        }
    }

    class ViewHolderType3(itemView: View, listener: AdapterNotifications.OnclickListener,val context: Context,private val list: List<Notification>) :
        BaseViewHolder(itemView, listener) {
        fun bind(item: Notification) {
            //space.goneIf(position != list.size - 1)
            if (item.isread) {
                bgNoti.setBackgroundColor(Color.WHITE)
            } else {
                bgNoti.setBackgroundColor(Color.parseColor("#FFF9E9"))
            }
            if(item.avtpath!="null"){
                Glide.with(itemView)
                    .load(item.avtpath)
                    .into(avt)
            } else {
                Glide.with(itemView)
                    .load(R.drawable.avatar)
                    .into(avt)
            }

            title.text = "${item.name} has like your post"
            val spannableString = SpannableString(title.text)

            val startIndex = title.text.indexOf(item.name)
            val endIndex = startIndex + item.name.length

            val boldStyleSpan = StyleSpan(Typeface.BOLD)
            spannableString.setSpan(
                boldStyleSpan,
                startIndex,
                endIndex,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            title.text = spannableString

            val timeago = getLastTimeAgo(item.key.toLong())
            time.text = timeago
        }
    }

    class ViewHolderType4(itemView: View, listener: AdapterNotifications.OnclickListener) :
        BaseViewHolder(itemView, listener) {
        fun bind(item: Notification) {
            if (item.isread) {
                bgNoti.setBackgroundColor(Color.WHITE)
            } else {
                bgNoti.setBackgroundColor(Color.parseColor("#FFF9E9"))
            }
            title.text = item.name

            val fulltext = "${item.name} has like your post"
            val spannableString = SpannableString(fulltext)

            val startIndex = fulltext.indexOf(item.name)
            val endIndex = startIndex + item.name.length

            val boldStyleSpan = StyleSpan(Typeface.BOLD)
            spannableString.setSpan(
                boldStyleSpan,
                startIndex,
                endIndex,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            title.text = spannableString

        }
    }

    class ViewHolderType5(itemView: View, listener: AdapterNotifications.OnclickListener) :
        BaseViewHolder(itemView, listener) {
        fun bind(item: Notification) {
            val fulltext = "${item.name} has like your post"
            val spannableString = SpannableString(fulltext)

            val startIndex = fulltext.indexOf(item.name)
            val endIndex = startIndex + item.name.length

            val boldStyleSpan = StyleSpan(Typeface.BOLD)
            spannableString.setSpan(
                boldStyleSpan,
                startIndex,
                endIndex,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            title.text = spannableString

            if (item.isread) {
                bgNoti.setBackgroundColor(Color.WHITE)
            } else {
                bgNoti.setBackgroundColor(Color.parseColor("#ECEFFF"))
            }
            title.text = item.name
        }
    }


}