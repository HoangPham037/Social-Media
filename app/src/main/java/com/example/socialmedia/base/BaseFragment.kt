package com.example.socialmedia.base


import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.view.View.OnTouchListener
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.socialmedia.base.utils.hideKeyboard


abstract class BaseFragment : Fragment() {
    private var activity : BaseActivity<*>? = null
    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.setOnTouchListener(OnTouchListener { v, event -> // Ẩn bàn phím nếu đang hiển thị
            v.hideKeyboard(requireActivity())
            v.clearFocus()
            true
        })
        activity = requireActivity() as BaseActivity<*>
        init()
        initData()
        initAction()
    }

    fun openFragment(fragmentClazz: Class<*>, args: Bundle?,
        addBackStack: Boolean
    ) {
        activity?.openFragment(fragmentClazz, args, addBackStack)
    }

    fun toast(text: String) {
        Toast.makeText(requireContext(), text, Toast.LENGTH_LONG).show()
    }
    fun onBackPressed() {
        activity?.onBackPressed()
    }

    abstract fun init()
    abstract fun initData()
    abstract fun initAction()
    fun showLoadingDialog() = activity?.showLoadingDialog()

    fun hideLoadingDialog() = activity?.hideLoadingDialog()
}