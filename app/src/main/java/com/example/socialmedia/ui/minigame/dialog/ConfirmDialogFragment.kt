package com.example.socialmedia.ui.minigame.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import com.example.socialmedia.R
import com.example.socialmedia.databinding.FragmentConfirmDialogBinding


class ConfirmDialogFragment : DialogFragment() {
    var listenerConfirm : OnClickConfirm?= null
    private lateinit var binding: FragmentConfirmDialogBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dialog!!.window!!.setBackgroundDrawableResource(R.color.transparent)
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_confirm_dialog, container, false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnNo.setOnClickListener {
            listenerConfirm?.onClickNo()
        }
        binding.btnYes.setOnClickListener {
            listenerConfirm?.onClickYes()
        }
    }
    interface OnClickConfirm{
        fun onClickNo()
        fun onClickYes()
    }
}