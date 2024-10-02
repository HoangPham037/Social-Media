package com.example.socialmedia.ui.minigame.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import com.example.socialmedia.R
import com.example.socialmedia.databinding.FragmentSuccessDialogBinding


class SuccessDialogFragment : DialogFragment() {
    var nameImage : String?= null
    var listener : OnClickButton?= null
    var idQuestion : Int?= null
    var coin : Int?= null
    private lateinit var binding: FragmentSuccessDialogBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        nameImage = arguments?.getString("NAME")
        idQuestion = arguments?.getInt("ID_QUESTION")
        coin = arguments?.getInt("COIN")
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        dialog!!.window!!.setBackgroundDrawableResource(R.color.transparent)
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_success_dialog, container, false
        )
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.tvResult.text = nameImage?.toUpperCase()
        binding.btnHome.setOnClickListener {
            listener?.onClickHome()
        }
        binding.btnNext.setOnClickListener {
            listener?.onClickNext()
        }
        if (idQuestion == 20){
            binding.btnNext.visibility = View.GONE
        }
        binding.tvCoinSuccess.text = coin.toString()
    }
    companion object{
        fun newInstance(name : String, id : Int, coin : Int): SuccessDialogFragment {
            val f = SuccessDialogFragment()
            val args = Bundle()
            args.putInt("COIN", coin)
            args.putString("NAME", name)
            args.putInt("ID_QUESTION", id)
            f.arguments = args
            return f
        }
    }
    interface OnClickButton{
        fun onClickHome()
        fun onClickNext()
    }
}