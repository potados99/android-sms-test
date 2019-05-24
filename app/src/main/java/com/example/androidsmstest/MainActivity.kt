package com.example.androidsmstest

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.Telephony
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    companion object {
        const val CHANGE_SMS_APP = 1
        const val SYSTEM_DEFAULT_SMS_APP = "com.android.mms"
    }

    /**
     * ViewModel
     */
    private var currentSmsApp: String
    get() {
        return Telephony.Sms.getDefaultSmsPackage(this)
    }
    set(value) {
        changeDefaultSmsApp(value)
    }

    private var isDefaultApp: Boolean = false
    get() {
        return (currentSmsApp == this.packageName)
    }

    private lateinit var originSmsApp: String


    /**
     * Business logic
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        getPermissions()

        originSmsApp = currentSmsApp
        var originLabelText = originSmsApp

        if (isDefaultApp) {
            originSmsApp = SYSTEM_DEFAULT_SMS_APP
            originLabelText = "$originSmsApp (default)"
        }

        updateOriginSmsAppLabel(originLabelText)
        updateCurrentSmsAppLabel()

        changeButton.setOnClickListener {
            changeDefaultSmsApp(this.packageName)
        }

        restoreButton.setOnClickListener {
            changeDefaultSmsApp(originSmsApp)
        }

        map_button.setOnClickListener {
            if (isDefaultApp) {
                val intent = Intent(this, MapActivity::class.java)
                startActivity(intent)
            }
            else {
                Toast.makeText(this, "not a default app! cannot receive message.", Toast.LENGTH_SHORT).show()
            }
        }

    }

    override fun onStop() {
        super.onStop()

        if (isDefaultApp) {
            val alertText = "Please return and restore SMS app!"
            Toast.makeText(this, alertText, Toast.LENGTH_LONG).show()
        }
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)

        if (hasFocus) {
            updateCurrentSmsAppLabel()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == CHANGE_SMS_APP) {
            if (resultCode == RESULT_OK) {
                updateCurrentSmsAppLabel()
            }
            else {
                val failMessage = "Failed to change default SMS app."
                Toast.makeText(this, failMessage, Toast.LENGTH_SHORT).show()
            }
        }

        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun changeDefaultSmsApp(appName: String) {
        val intent = Intent(Telephony.Sms.Intents.ACTION_CHANGE_DEFAULT)

        intent.putExtra(Telephony.Sms.Intents.EXTRA_PACKAGE_NAME, appName)
        startActivityForResult(intent, CHANGE_SMS_APP)
    }

    private fun getPermissions() {
        requirePermission(Manifest.permission.RECEIVE_SMS)
        requirePermission(Manifest.permission.READ_SMS)
        requirePermission(Manifest.permission.BROADCAST_SMS)
        requirePermission(Manifest.permission.SEND_SMS)
        requirePermission(Manifest.permission.READ_CONTACTS)
    }

    private fun requirePermission(permission: String) {
        val permissionCheck = ContextCompat.checkSelfPermission(this@MainActivity, permission)
        val granted = (permissionCheck == PackageManager.PERMISSION_GRANTED)
        val userDenied = ActivityCompat.shouldShowRequestPermissionRationale(this, permission)

        if (! granted) {
            if (userDenied) {
                val permissionBagging = "Please allow access to $permission"
                Toast.makeText(this@MainActivity, permissionBagging, Toast.LENGTH_SHORT).show()
            }

            ActivityCompat.requestPermissions(this, arrayOf(permission), 1)
        }
    }


    /**
     * UI control
     */
    private fun updateOriginSmsAppLabel(text: String) {
        originalSmsAppLabel.text = text
    }

    private fun updateCurrentSmsAppLabel() {
        currentSmsAppLabel.text = currentSmsApp

        if (isDefaultApp) {
            notDefaultSmsAppLabel.visibility = View.INVISIBLE
        }
        else {
            notDefaultSmsAppLabel.visibility = View.VISIBLE
        }
    }
}
