package com.example.androidsmstest.ViewModels

import android.app.Application
import android.arch.lifecycle.ViewModel
import com.example.androidsmstest.Models.MainModel

class MainViewModel : ViewModel() {
    var c = a.applicationContext

    var a = Application()

    var originalDefaultSmsApp = "fallback ha ha"
    var currentDefaultSmsApp = "fallback ha ha"

    override fun onCleared() {
        super.onCleared()
    }
}