package com.example.androidsmstest

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.opengl.Visibility
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.Telephony
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.view.View
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private val CHANGE_SMS_APP = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        getPermissions()

        var originSmsApp = getDefaultSmsApp()
        updateCurrentSmsAppLabel(originSmsApp) // original sms app.
        updateOriginSmsAppLabel(originSmsApp)

        changeButton.setOnClickListener {
            changeDefaultSmsApp(this.packageName)
        }

        restoreButton.setOnClickListener {
            changeDefaultSmsApp(originSmsApp)
        }
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)

        if (hasFocus) {
            updateCurrentSmsAppLabel(getDefaultSmsApp())
        }
    }

    private fun getDefaultSmsApp(): String {
        return Telephony.Sms.getDefaultSmsPackage(this)
    }

    private fun updateOriginSmsAppLabel(text: String) {
        originalSmsAppLabel.text = text
    }

    private fun updateCurrentSmsAppLabel(text: String) {
        currentSmsAppLabel.text = text

        if (text == this.packageName) {
            notDefaultSmsAppLabel.visibility = View.INVISIBLE
        }
        else {
            notDefaultSmsAppLabel.visibility = View.VISIBLE
        }
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
        val permissionCheck: Int = ContextCompat.checkSelfPermission(this@MainActivity, permission)
        val granted = (permissionCheck == PackageManager.PERMISSION_GRANTED)

        if (! granted) {
            // if user denied permission.
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
                // Hey please give me permission :(
                Toast.makeText(this@MainActivity, "Please allow access to $permission", Toast.LENGTH_SHORT).show()
            }

            // request.
            ActivityCompat.requestPermissions(this, arrayOf(permission), 1)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == CHANGE_SMS_APP) {
            if (resultCode == RESULT_OK) {
                updateCurrentSmsAppLabel(getDefaultSmsApp())
            }
            else {
                Toast.makeText(this, "Failed to change default SMS app.", Toast.LENGTH_SHORT).show()
            }
        }

        super.onActivityResult(requestCode, resultCode, data)
    }
}
