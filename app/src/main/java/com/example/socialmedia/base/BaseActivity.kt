package com.example.socialmedia.base


import android.annotation.SuppressLint
import android.app.Dialog
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.example.socialmedia.R
import com.example.socialmedia.base.utils.hideKeyboard


abstract class BaseActivity<VB : ViewBinding> : AppCompatActivity() {
    lateinit var binding: VB
    private var loadingDialog: Dialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = getViewBinding(layoutInflater)
        setContentView(binding.root)
        setUpDialogLoading()
        init()
    }

    abstract fun getViewBinding(inflater: LayoutInflater): VB
    abstract fun init()


    @Throws
    open fun openFragment(
        fragmentClazz: Class<*>,
        args: Bundle?,
        addBackStack: Boolean = false
    ) {
        val tag = fragmentClazz.simpleName
        try {
            val fragment: Fragment
            try {
                fragment = (fragmentClazz.asSubclass(Fragment::class.java)).newInstance()
                    .apply { arguments = args }

                val transaction = supportFragmentManager.beginTransaction()
                transaction.setCustomAnimations(
                    R.anim.slide_in,
                    R.anim.fade_out,
                    R.anim.fade_in,
                    R.anim.slide_out
                )
                if (addBackStack) {
                    transaction.addToBackStack(tag)
                }
                transaction.add(android.R.id.content, fragment, tag)
                transaction.commit()
            } catch (e: InstantiationException) {
                e.printStackTrace()
            } catch (e: IllegalAccessException) {
                e.printStackTrace()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @SuppressLint("ResourceAsColor")
    private fun setUpDialogLoading() {
        loadingDialog = Dialog(this)
        val view = LayoutInflater.from(this).inflate(R.layout.loading_dialog, null, false)
        loadingDialog?.window?.setBackgroundDrawable(ColorDrawable(android.R.color.transparent))
        loadingDialog?.setCancelable(false)
        loadingDialog?.setContentView(view)
    }

    fun showLoadingDialog() {
        loadingDialog?.show()
        binding.root.hideKeyboard(this)
    }

    fun hideLoadingDialog() {
        loadingDialog?.hide()
    }
}