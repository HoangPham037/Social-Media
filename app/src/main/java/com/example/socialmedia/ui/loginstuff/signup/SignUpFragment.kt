package com.example.socialmedia.ui.loginstuff.signup

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Context
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Patterns
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewTreeObserver
import android.widget.EditText
import android.widget.LinearLayout
import androidx.core.content.ContextCompat

import com.example.socialmedia.R
import com.example.socialmedia.ui.firstTimeSetUp.ChoosePreferencesFragment
import com.example.socialmedia.base.BaseFragmentWithBinding
import com.example.socialmedia.base.utils.click
import com.example.socialmedia.databinding.FragmentSignUpBinding
import com.example.socialmedia.loacal.Preferences
import com.example.socialmedia.repository.MainViewModel
import com.example.socialmedia.ui.firstTimeSetUp.LogInFirstTimeFragment
import com.example.socialmedia.ui.loginstuff.CheckStuff
import com.example.socialmedia.ui.loginstuff.LoginStuffViewModel
import com.google.firebase.auth.FirebaseAuth
import org.koin.android.ext.android.inject
import java.text.SimpleDateFormat
import java.util.ArrayList
import java.util.Calendar
import java.util.Locale


class SignUpFragment : BaseFragmentWithBinding<FragmentSignUpBinding>(),CheckStuff {
    val firebaseViewModel: MainViewModel by inject()
    val sharedPreferences: Preferences by inject()
    val viewmodel : LoginStuffViewModel by inject()

    private var rootView: View? = null
    private var isKeyboardOpen = false


    override fun getViewBinding(inflater: LayoutInflater): FragmentSignUpBinding {
        return FragmentSignUpBinding.inflate(layoutInflater)
    }

    override fun init() {
        binding.apply {
            rootView?.viewTreeObserver?.addOnGlobalLayoutListener(globalLayoutListener)
        }
    }

    override fun initData() {
        binding.apply {
            icBackSignUp.visibility = View.VISIBLE
            spaces.visibility = View.GONE
            val listEditText = listOf<EditText>(edtEmailSignup,edtNameSignUp,edtPasswordSignUp,edtConfirmPassword)
            preventUserDownLine(listEditText)
            checkIfPasswordIsLongEnought(edtPasswordSignUp,pw1)
            checkIfPasswordIsLongEnought(edtConfirmPassword,pw2)
        }
    }


    override fun initAction() {
        binding.apply {
            val checkloginfirsttime = sharedPreferences.getBoolean("firstLaunch")
            val checkState = sharedPreferences.getBoolean("fblogin")
            val checkStateGG = sharedPreferences.getBoolean("gglogin")
            if (checkState == true || checkStateGG == true) {
                icBackSignUp.visibility = View.GONE
                spaces.visibility = View.VISIBLE
                val currentUser = FirebaseAuth.getInstance().currentUser
                currentUser.let {
                    val email = it?.email
                    val name = it?.displayName
                    if(email !=null || name !=null) {
                        edtEmailSignup.setText(email)
                        edtNameSignUp.setText(name)
                    }
                }
                fl1.visibility = View.VISIBLE
                fl2.visibility = View.VISIBLE
                fl3.visibility = View.VISIBLE
                fl4.visibility = View.GONE
                fl5.visibility = View.GONE
                if(!checkStateGG!!) {
                    firebaseViewModel.fetchUserInfo()
                    firebaseViewModel.userName.observe(viewLifecycleOwner) {
                        edtNameSignUp.setText(it)
                    }
                }
            }
            btnLogin.click {
                if (checkState == true || checkStateGG == true) {
                    SignUpFromFacebooknGoogle()
                } else {
                    SignMeUp()
                }

            }
            icBackSignUp.click {
                onBackPressed()
                //openFragment(LogInFirstTimeFragment::class.java, null, false)
            }

            checkEmail(edtEmailSignup)
            edtDoBSignUp.click {
                val currentDate = Calendar.getInstance()
                val year = currentDate.get(Calendar.YEAR)
                val month = currentDate.get(Calendar.MONTH)
                val day = currentDate.get(Calendar.DAY_OF_MONTH)

                val datePickerDialog = CustomDatePickerDialog(
                    requireContext(),
                    R.style.CustomDateTimePickerTheme,
                    { _, year, monthOfYear, dayOfMonth ->
                        val selectedDate = Calendar.getInstance()
                        selectedDate.set(Calendar.YEAR, year)
                        selectedDate.set(Calendar.MONTH, monthOfYear)
                        selectedDate.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                        val formattedDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(selectedDate.time)
                        edtDoBSignUp.setText(formattedDate)

                        if(!isUserOver16(edtDoBSignUp.text.toString())) {
                            lefet.visibility = View.VISIBLE
                        }
                        else {
                            lefet.visibility = View.GONE
                        }
                    },
                    year,
                    month,
                    day
                )
                datePickerDialog.show()
            }

            var state = false

            edtPasswordSignUp.setOnClickListener {
                state = !state
                statePassword(edtPasswordSignUp,state)
            }
            edtConfirmPassword.click {
                state = !state
                statePassword(edtConfirmPassword,state)
            }
        }
    }

    fun statePassword(edt: EditText,state: Boolean){
        val drawableEndVisible = ContextCompat.getDrawable(requireContext(), R.drawable.ic_eye)
        val drawableEndHidden = ContextCompat.getDrawable(requireContext(), R.drawable.ic_hideeyes)

        val drawableEnd = if (state) drawableEndVisible else drawableEndHidden

        edt.setCompoundDrawablesWithIntrinsicBounds(
            null,
            null,
            drawableEnd,
            null
        )
        togglePasswordVisibility(edt)
    }

    @SuppressLint("SetTextI18n")
    private fun SignMeUp() {
        binding.apply {
            val avtpath : Uri? =null
            if (edtEmailSignup.text.isNotEmpty() && edtPasswordSignUp.text.isNotEmpty() && edtNameSignUp.text.isNotEmpty() && edtDoBSignUp.text.isNotEmpty() && edtConfirmPassword.text.isNotEmpty()) {
                fillallfield.visibility = View.GONE
                val password = edtPasswordSignUp.text.toString()
                if (password.length >= 8) {
                    if (edtConfirmPassword.text.toString() == password) {
                        val isOver16 = isUserOver16(edtDoBSignUp.text.toString())
                        if (isOver16) {
                            firebaseViewModel.RegisterFirebasebyAccount(edtEmailSignup.text.toString(), password, object : SignUpCallBack {
                                override fun onLoginSuccess() {
                                    val uid = firebaseViewModel.getUid()
                                    firebaseViewModel.getUid()?.let {
                                        firebaseViewModel.addDataFireStore(
                                            addUserInfo(
                                                uid,
                                                edtEmailSignup.text.toString(),
                                                edtNameSignUp.text.toString(),
                                                edtDoBSignUp.text.toString(),
                                                avtpath,
                                                "Offline",false,
                                            ), "Users", uid!!
                                        )
                                    }
                                    openFragment(ChoosePreferencesFragment::class.java, null, false)
                                }

                                override fun onLoginFailure(error: Exception) {
                                    right.visibility = View.VISIBLE
                                    txterror.setText("This email already exists")
                                    //toast("Fail $error")
                                }
                            })
                        } else {
                        }
                    } else {
                        fillallfield.visibility = View.VISIBLE
                        fillAllorMatchpw.setText("Your password is not match")
                    }
                } else {
                    fillallfield.visibility = View.VISIBLE
                    fillAllorMatchpw.setText("You must be over 16 years old to use the service")
                }
            } else {
                fillAllorMatchpw.setText("Please fill all the field before you signup")
                fillallfield.visibility = View.VISIBLE
            }
        }
    }

    fun preventUserDownLine(list: List<EditText>) {
        for(i in list) {
            i.setOnKeyListener { _, keyCode, event ->
                if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN) {
                    return@setOnKeyListener true
                }
                return@setOnKeyListener false
            }
        }
    }


    fun SignUpFromFacebooknGoogle() {
        binding.apply {
            val avtpath : Uri?
            val user = FirebaseAuth.getInstance().currentUser
            user.let {
                avtpath = it?.photoUrl
            }
            firebaseViewModel.getUid()?.let {
                firebaseViewModel.addDataFireStore(
                    addUserInfo(
                        it,
                        edtEmailSignup.text.toString(),
                        edtNameSignUp.text.toString(),
                        edtDoBSignUp.text.toString(),
                        avtpath,
                        "Offline",false,null
                    ), "Users", it
                )
            } //sai thi xoa o day nhe Hien
        }
        openFragment(ChoosePreferencesFragment::class.java,null,false)
    }

    fun addUserInfo(
        idUser: String?,
        mail: String? = null,
        name: String,
        dateOfBirth: String,
        avtpath: Uri? =null,
        status: String?=null,
        isFollow: Boolean?=null,
        listblock: ArrayList<String>? = ArrayList(),
        address: String?="",
        phone: String?="",
        type: String?="",
        description:String?="",
        listfollowing:ArrayList<String>?= ArrayList(),
        listfollower: ArrayList<String>?= ArrayList(),
        gold : Int = 0
    ): HashMap<String, Any?> {
        val user = hashMapOf(
            "id" to idUser,
            "email" to mail,
            "name" to name,
            "dateOfBirth" to dateOfBirth,
            "avtPath" to avtpath,
            "status" to status,
            "isFollow" to isFollow,
            "listBlock" to listblock,
            "address" to address,
            "phone" to phone,
            "type" to type,
            "description" to description,
            "listFollowing" to listfollowing,
            "listFollowers" to listfollower,
            "gold" to gold
        )
        return user
    }

    override fun checkEmail(edt: EditText) {
        binding.apply {
            edt.addTextChangedListener(object:TextWatcher{
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                }

                override fun afterTextChanged(s: Editable?) {
                    if(isEmailValid(edt.text.toString()) ) {
                            binding.right.visibility = View.GONE
                            edt.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_greentick, 0)
                    } else {
                        if( edtEmailSignup.text.toString().isNullOrEmpty()) {
                            binding.right.visibility = View.GONE
                        } else {
                            binding.right.visibility = View.VISIBLE
                            edt.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
                        }
                    }
                }
            })
        }
    }

    fun checkIfPasswordIsLongEnought(edt: EditText,layout: LinearLayout) {
        edt.addTextChangedListener(object :TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                val text = s.toString()
                if(text.length<8){
                    layout.visibility = View.VISIBLE
                } else {layout.visibility = View.GONE}
            }

        })
    }

    override fun isEmailValid(email: String): Boolean {
            val pattern = Patterns.EMAIL_ADDRESS
            return pattern.matcher(email).matches()
    }

    fun isUserOver16(dob: String): Boolean {
        val formattedDob = dob.replace("-", "/")
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val currentDate = Calendar.getInstance()

        val dobDate = Calendar.getInstance()
        dobDate.time = dateFormat.parse(formattedDob) ?: return false // Parse the date of birth string

        var age = currentDate.get(Calendar.YEAR) - dobDate.get(Calendar.YEAR)
        if (currentDate.get(Calendar.DAY_OF_YEAR) < dobDate.get(Calendar.DAY_OF_YEAR)) {
            age--
        }
        return age >= 16
    }

    override fun onPause() {
        super.onPause()
        rootView?.viewTreeObserver?.removeOnGlobalLayoutListener(globalLayoutListener)
    }

    private val globalLayoutListener = object : ViewTreeObserver.OnGlobalLayoutListener {
        override fun onGlobalLayout() {
            val r = Rect()
            rootView?.getWindowVisibleDisplayFrame(r)
            val screenHeight = rootView?.height
            val keypadHeight = screenHeight?.minus(r.bottom)
            if (keypadHeight != null) {
                if (keypadHeight > screenHeight * 0.15) {
                    binding.scrollview.translationY = (-keypadHeight).toFloat()
                } else {
                    binding.scrollview.translationY = 0f
                }
            }
        }
    }

    fun togglePasswordVisibility(editText: EditText) {
        val currentTransformationMethod = editText.transformationMethod
        if (currentTransformationMethod == PasswordTransformationMethod.getInstance()) {
            editText.transformationMethod = HideReturnsTransformationMethod.getInstance()
        } else {
            editText.transformationMethod = PasswordTransformationMethod.getInstance()
        }
        editText.setSelection(editText.text.length)
    }
}

class CustomDatePickerDialog(
    context: Context,
    themeResId: Int,
    private val onDateSetListener: DatePickerDialog.OnDateSetListener,
    year: Int,
    month: Int,
    dayOfMonth: Int
) : DatePickerDialog(context, themeResId, onDateSetListener, year, month, dayOfMonth) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val positiveButton = getButton(DatePickerDialog.BUTTON_POSITIVE)
        val negativeButton = getButton(DatePickerDialog.BUTTON_NEGATIVE)

        positiveButton.setTextColor(ContextCompat.getColor(context, R.color.color_orange_app))
        negativeButton.setTextColor(ContextCompat.getColor(context, R.color.color_orange_app))
    }
}
