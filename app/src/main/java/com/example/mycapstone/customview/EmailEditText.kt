package com.example.mycapstone.customview

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.util.Patterns
import androidx.appcompat.widget.AppCompatEditText

class EmailEditText @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : AppCompatEditText(context, attrs) {

    private var isFocusedInternally = false

    init {
        addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (!isFocusedInternally) {
                    if (s != null && s.isNotEmpty() && !Patterns.EMAIL_ADDRESS.matcher(s).matches()) {
                        error = "Please enter a valid email"
                    }
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        setOnFocusChangeListener { _, hasFocus ->
            isFocusedInternally = hasFocus
            if (!hasFocus) {
                if (text?.isNotEmpty() == true && !Patterns.EMAIL_ADDRESS.matcher(text).matches()) {
                    error = "Please enter a valid email"
                }
            }
        }
    }
}
