package com.example.androidsmstest.Models

import android.app.Application
import android.content.Context
import android.content.Intent
import android.provider.Telephony
import com.example.androidsmstest.MainActivity

// something global, used in MainActivity.
class MainModel(var context: Context?) {
    var currentSmsApp: String
        get() {
            return Telephony.Sms.getDefaultSmsPackage(context)
        }
        set(value) {
        changeDefaultSmsApp(value)
        }

    var isDefaultApp: Boolean = false
    get() {
        return (currentSmsApp == context?.packageName)
    }

    private lateinit var originSmsApp: String
}