package com.example.mycapstone.customview

import android.content.Context
import android.util.AttributeSet
import android.text.InputType
import android.text.Editable
import android.text.TextWatcher
import androidx.appcompat.widget.AppCompatEditText

class NameEditText @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : AppCompatEditText(context, attrs) {

    init {
        hint = "Name"
        inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_CAP_WORDS
        addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (s.isNullOrEmpty()) {
                    error = "Name is required"
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus && text.isNullOrEmpty()) {
                error = "Name is required"
            }
        }
    }
}
