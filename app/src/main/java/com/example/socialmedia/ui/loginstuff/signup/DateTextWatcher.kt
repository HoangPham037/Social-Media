package com.example.socialmedia.ui.loginstuff.signup

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText

class DateTextWatcher(private val editText: EditText) : TextWatcher {

    private var isFormatting: Boolean = false

    companion object {
        private const val DATE_FORMAT = "##-##-####"
        private const val SEPARATOR = "-"
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

    override fun afterTextChanged(editable: Editable?) {
        if (isFormatting) {
            return
        }

        isFormatting = true

        val formattedText = formatText(editable)
        val selectionStart = editText.selectionStart
        val selectionEnd = editText.selectionEnd

        editText.setText(formattedText)
        editText.setSelection(selectionStart, selectionEnd)

        isFormatting = false
    }

    private fun formatText(editable: Editable?): String {
        val digitsOnly = editable.toString().replace("\\D".toRegex(), "")
        val formatted = StringBuilder()

        var index = 0
        var count = 0
        while (index < digitsOnly.length && count < DATE_FORMAT.length) {
            formatted.append(digitsOnly[index])
            if (count == 1 || count == 3) {
                formatted.append(SEPARATOR)
            }
            index++
            count++
        }

        // Ensure the formatted text length does not exceed the length of the original editable text
        if (formatted.length > (editable?.length ?: 0)) {
            formatted.delete(editable?.length ?: 0, formatted.length)
        }

        return formatted.toString()
    }
}