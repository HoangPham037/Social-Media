package com.example.socialmedia.ui.minigame.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import com.example.socialmedia.R
import com.example.socialmedia.databinding.FragmentCompleteDialogBinding
import com.example.socialmedia.ui.minigame.di.Constant

class CompleteDialogFragment : DialogFragment() {
    var listenerComplete : OnClickComplete?= null
    var id : Int?= null
    private lateinit var binding: FragmentCompleteDialogBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        id = arguments?.getInt(Constant.COMPLETE)
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dialog!!.window!!.setBackgroundDrawableResource(R.color.transparent)
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_complete_dialog, container, false
        )
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tvComplete.text = "Complete Level $id"
    }
    interface OnClickComplete{
        fun onClickComplete()
    }
    companion object{
        fun getInstanceComplete(id : Int) : CompleteDialogFragment {
            val complete = CompleteDialogFragment()
            val arg = Bundle()
            arg.putInt(Constant.COMPLETE, id)
            complete.arguments = arg
            return complete
        }
    }
}