package com.example.socialmedia.ui.setting

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatButton
import androidx.recyclerview.widget.RecyclerView
import com.example.socialmedia.R
import com.example.socialmedia.model.Profile
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class BottomSheetDialogFrg(private val event : View.OnClickListener) : BottomSheetDialogFragment() {
  private  var listProfile: ArrayList<Profile>? = arrayListOf()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return     inflater.inflate(R.layout.fragment_bottom_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val rcvAcc = view.findViewById<RecyclerView>(R.id.rcv_account)
        val btnAddAcc = view.findViewById<AppCompatButton>(R.id.btn_add_acc)
        val ivClose = view.findViewById<ImageView>(R.id.iv_close)
        val adapterUser = AdapterUser { event.onClick(it) }
        rcvAcc.adapter = adapterUser
        listProfile?.let { adapterUser.setData(it) }
        btnAddAcc.setOnClickListener { event.onClick(it) }
        ivClose.setOnClickListener { dismiss() }
    }

    fun setData(profile: Profile) {
        if (listProfile?.indexOf(profile) == -1){
            listProfile?.add(profile)
        }
    }
}