package com.example.socialmedia.ui.chat.chatscreen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import com.example.socialmedia.R
import com.example.socialmedia.base.utils.click
import com.example.socialmedia.loacal.Preferences
import com.example.socialmedia.model.Conversion
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import org.koin.android.ext.android.inject

class BottomSheetOption(
    private val uid: String,
    private val conversion: Conversion,
    private val event: (String, String, TypeLayoutClick) -> Unit
) : BottomSheetDialogFragment() {
    val sharedPreferences: Preferences by inject()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_bottom_option, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val markAsUnread = view.findViewById<LinearLayout>(R.id.layoutMarkAsUnread)
        val mute = view.findViewById<LinearLayout>(R.id.layoutMute)
        val imgMute = view.findViewById<AppCompatImageView>(R.id.imgMute)
        val tvMute = view.findViewById<TextView>(R.id.tvMute)
        val deleteConversion = view.findViewById<LinearLayout>(R.id.layoutDeleteConversion)
        markAsUnread.click {
            conversion.id?.let { it1 -> event.invoke("", it1, TypeLayoutClick.MARK_AS_UNREAD) }
        }

        if (uid == conversion.listUid?.get(0)) {
            if (conversion.isMuteSend == true) {
                imgMute.setImageResource(R.drawable.ic_un_mute)
                tvMute.text = resources.getString(R.string.un_mute)
            } else {
                imgMute.setImageResource(R.drawable.ic_bell)
                tvMute.text = resources.getString(R.string.mute)
            }
        } else {
            if (conversion.isMuteReversion == true) {
                imgMute.setImageResource(R.drawable.ic_un_mute)
                tvMute.text = resources.getString(R.string.un_mute)
            } else {
                imgMute.setImageResource(R.drawable.ic_bell)
                tvMute.text = resources.getString(R.string.mute)
            }
        }

        mute.click {
            if (uid == conversion.listUid?.get(0)) {
                conversion.id?.let { conversionId ->
                    event.invoke(
                        "send",
                        conversionId,
                        if (conversion.isMuteSend == true) TypeLayoutClick.UN_MUTE else TypeLayoutClick.MUTE
                    )
                }
            } else {
                conversion.id?.let { conversionId ->
                    event.invoke(
                        "receiver",
                        conversionId,
                        if (conversion.isMuteReversion == true) TypeLayoutClick.UN_MUTE else TypeLayoutClick.MUTE
                    )
                }
            }
        }

        deleteConversion.click {
            conversion.id?.let { it1 -> event.invoke("", it1, TypeLayoutClick.DELETE_CONVERSION) }
        }
    }
}

enum class TypeLayoutClick {
    MARK_AS_UNREAD,
    MUTE,
    UN_MUTE,
    DELETE_CONVERSION
}