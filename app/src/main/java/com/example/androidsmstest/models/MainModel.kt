package com.example.androidsmstest.models

import android.app.Activity
import android.content.Intent
import android.provider.Telephony

// something global, used in MainActivity.
class MainModel(private val activity: Activity?) {

    var thisAppName: String = activity?.packageName ?: "(null)"

    var isDefaultApp: Boolean = false
        get() = (currentDefaultSmsApp == thisAppName) /* false when null. */

    val originalDefaultSmsApp: String = if (isDefaultApp) "com.android.mms" else currentDefaultSmsApp

    var currentDefaultSmsApp: String
        get() {
            return Telephony.Sms.getDefaultSmsPackage(activity)
        }
        set(value) {
            currentDefaultSmsApp = value
            changeDefaultSmsApp(value)
        }

    private fun changeDefaultSmsApp(appName: String?) {
        val intent = Intent(Telephony.Sms.Intents.ACTION_CHANGE_DEFAULT)

        intent.putExtra(Telephony.Sms.Intents.EXTRA_PACKAGE_NAME, appName)
        activity?.startActivityForResult(intent, 1)
    }
}