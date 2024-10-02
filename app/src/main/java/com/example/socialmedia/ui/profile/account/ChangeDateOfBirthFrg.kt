package com.example.socialmedia.ui.profile.account

import android.app.DatePickerDialog
import android.util.Log
import android.view.LayoutInflater
import com.example.socialmedia.base.BaseFragmentWithBinding
import com.example.socialmedia.base.utils.click
import com.example.socialmedia.databinding.FragmentChangeDatebirthBinding
import org.koin.android.ext.android.inject
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


class ChangeDateOfBirthFrg : BaseFragmentWithBinding<FragmentChangeDatebirthBinding>() {
    private val viewModel: AccountViewModel by inject()
    override fun init() {
        viewModel.getProfileUser(viewModel.repository.getUid())
    }
    override fun initData() {
        viewModel.livedataProfile.observe(viewLifecycleOwner) {
            binding.tvDateOld.text = it.dateOfBirth
        }
    }


    override fun initAction() {
        binding.apply {
            btnSave.click { saveChangeDateOfBirth() }
            ivBack.click { onBackPressed() }
            tvDateBirth.click { openTimePickerDialog() }
        }
    }

    private fun openTimePickerDialog() {
        val currentDate = Calendar.getInstance()
        if (viewModel.livedataProfile.value != null) {
            viewModel.livedataProfile.value?.dateOfBirth?.let {
                try {
                    val date = it.split("/")
                    currentDate.set(Calendar.YEAR, date[2].toInt())
                    currentDate.set(Calendar.MONTH, date[1].toInt() - 1)
                    currentDate.set(Calendar.DAY_OF_MONTH, date[0].toInt())
                } catch (e: Exception) {
                    Log.e("Error", "Lỗi không xác định: ${e.message}")
                }
            }
        }

        val year = currentDate.get(Calendar.YEAR)
        val month = currentDate.get(Calendar.MONTH)
        val day = currentDate.get(Calendar.DAY_OF_MONTH)
        val datePickerDialog =
            DatePickerDialog(requireContext(), { _, _, monthOfYear, dayOfMonth ->
                val selectedDate = Calendar.getInstance()
                selectedDate.set(Calendar.YEAR, year)
                selectedDate.set(Calendar.MONTH, monthOfYear)
                selectedDate.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                val formattedDate =
                    SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(selectedDate.time)
                binding.tvDateBirth.text = formattedDate
            }, year, month, day)
        datePickerDialog.show()
    }

    private fun saveChangeDateOfBirth() {
        val tvDate = binding.tvDateBirth.text.toString().trim()
        if (tvDate.isEmpty()) {
            toast("Please enter your date of birth")
        } else {
            if (checkDate(tvDate)) editDateOfBirth(tvDate) else toast("You must be at least 13 years old to sign up")
        }

    }

    private fun checkDate(dob: String): Boolean {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val currentDate = Calendar.getInstance()
        val dobDate = Calendar.getInstance()
        dobDate.time = dateFormat.parse(dob) ?: return false
        var age = currentDate.get(Calendar.YEAR) - dobDate.get(Calendar.YEAR)
        if (currentDate.get(Calendar.DAY_OF_YEAR) < dobDate.get(Calendar.DAY_OF_YEAR)) {
            age--
        }
        return age >= 13
    }

    private fun editDateOfBirth(textDate: String) {
        viewModel.editAccount("dateOfBirth", textDate) { sms ->
            toast(sms)
            if (sms == "Update successful") {
                onBackPressed()
            }
        }
    }

    override fun getViewBinding(inflater: LayoutInflater): FragmentChangeDatebirthBinding {
        return FragmentChangeDatebirthBinding.inflate(layoutInflater)
    }
}


