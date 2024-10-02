package com.example.socialmedia.ui.minigame.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import com.example.socialmedia.R
import com.example.socialmedia.databinding.FragmentReleaseDialogBinding

class ReleaseDialogFragment : DialogFragment() {
    var listenerRelease : OnClickRelease?= null
    var nameRelease : String?=null
    var idQuestion : Int?= null
    private lateinit var binding: FragmentReleaseDialogBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        nameRelease = arguments?.getString("NAME")
        idQuestion = arguments?.getInt("ID_QUESTION")
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dialog!!.window!!.setBackgroundDrawableResource(R.color.transparent)
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_release_dialog, container, false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnHomeRelease.setOnClickListener {
            listenerRelease?.onClickRelease()
        }
        binding.btnNextRelease.setOnClickListener {
            listenerRelease?.onClickNextRelease()
        }
        binding.tvResult.text = nameRelease?.toUpperCase()
        if (idQuestion == 20) binding.btnNextRelease.visibility = View.GONE

    }
    interface OnClickRelease{
        fun onClickRelease()
        fun onClickNextRelease()
    }
    companion object{
        fun newInstance(name : String, id : Int): ReleaseDialogFragment {
            val r = ReleaseDialogFragment()
            val args = Bundle()
            args.putString("NAME", name)
            args.putInt("ID_QUESTION",id)
            r.arguments = args
            return r
        }
    }
}