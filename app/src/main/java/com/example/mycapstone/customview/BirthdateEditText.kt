package com.example.mycapstone.customview

import android.app.DatePickerDialog
import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText
import java.util.*

class BirthdateEditText @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : AppCompatEditText(context, attrs) {

    init {
        hint = "Birthdate"
        isFocusable = false // Makes it readonly
        isClickable = true

        setOnClickListener {
            showDatePicker()
        }

        setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus && text.isNullOrEmpty()) {
                error = "Birthdate is required"
            }
        }
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePicker = DatePickerDialog(context, { _, selectedYear, selectedMonth, selectedDay ->
            val formattedDate = "${selectedDay}/${selectedMonth + 1}/${selectedYear}"
            setText(formattedDate)
        }, year, month, day)

        datePicker.show()
    }
}
