package com.ufes.callguard.Util

import android.text.Editable
import android.text.TextWatcher

class PhoneNumberMask(private val maxLength: Int) : TextWatcher {
    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

    override fun afterTextChanged(s: Editable?) {
        s?.let {
            if (it.length > maxLength) {
                it.delete(maxLength, it.length)
            }
        }
    }
}
