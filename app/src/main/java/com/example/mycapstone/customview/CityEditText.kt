package com.example.mycapstone.customview

import android.content.Context
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText

class CityEditText @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : AppCompatEditText(context, attrs) {

    private var isFocusedInternally = false

    init {
        hint = "City"
        inputType = InputType.TYPE_CLASS_TEXT

        addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (!isFocusedInternally && s.isNullOrEmpty()) {
                    error = "City is required"
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        setOnFocusChangeListener { _, hasFocus ->
            isFocusedInternally = hasFocus
            if (!hasFocus && text.isNullOrEmpty()) {
                error = "City is required"
            }
        }
    }
}
