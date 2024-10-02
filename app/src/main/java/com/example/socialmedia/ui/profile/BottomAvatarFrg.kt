package com.example.socialmedia.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TableRow
import com.example.socialmedia.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class BottomAvatarFrg(val  event : View.OnClickListener) : BottomSheetDialogFragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_bottom_avatar, container, false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val tbViewAvt = view.findViewById<TableRow>(R.id.tb_view_avt)
        val tvChangeAvt = view.findViewById<TableRow>(R.id.tb_change_avt)
        tbViewAvt.setOnClickListener {
            event.onClick(it)
            dismiss()
        }
        tvChangeAvt.setOnClickListener {
            event.onClick(it)
            dismiss()
        }
    }
}