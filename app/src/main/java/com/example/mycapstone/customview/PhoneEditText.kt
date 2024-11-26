package com.example.mycapstone.customview

import android.content.Context
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText

class PhoneEditText @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : AppCompatEditText(context, attrs) {

    init {
        hint = "Phone"
        inputType = InputType.TYPE_CLASS_PHONE

        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (!isValidPhone(s)) {
                    error = "Invalid phone number"
                } else {
                    error = null
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus && text.isNullOrEmpty()) {
                error = "Phone is required"
            }
        }
    }

    private fun isValidPhone(phone: CharSequence?): Boolean {
        // Basic validation: Check for non-empty, numeric values with a minimum length of 10
        return phone?.let {
            it.isNotEmpty() && it.length >= 10 && it.all { char -> char.isDigit() }
        } ?: false
    }
}
