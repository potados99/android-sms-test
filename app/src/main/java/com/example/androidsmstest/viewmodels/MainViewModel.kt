package com.example.androidsmstest.viewmodels

import android.app.Activity
import android.opengl.Visibility
import android.view.View
import com.example.androidsmstest.models.MainModel

class MainViewModel(activity: Activity?) {
    private var model = MainModel(activity)

    val thisAppName: String = model.thisAppName
    var isDefaultApp: Boolean = false
        get() = model.isDefaultApp

    val notDefaultAppMessage: String = "This app is not a default SMS app."

    var originalDefaultSmsAppName: String = "default sms app"
        get() = model.originalDefaultSmsApp

    var currentDefaultSmsAppName: String
        get() = model.currentDefaultSmsApp
        set(value) {
            model.currentDefaultSmsApp = value
        }

    var notDefaultAppWarnVisible: Int = android.view.View.GONE
        get () = if (isDefaultApp) android.view.View.GONE else android.view.View.VISIBLE

    fun changeButtonClick() {
        model.currentDefaultSmsApp = thisAppName

    }

    fun restoreButtonClick() {
        model.currentDefaultSmsApp = originalDefaultSmsAppName

    }

}