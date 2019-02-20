package com.example.androidsmstest

import android.Manifest
import android.content.*
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Telephony
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.ListAdapter
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    companion object {
        const val CHANGE_SMS_APP = 1
        const val SHOW_ON_MAP = 2
        const val SYSTEM_DEFAULT_SMS_APP = "com.android.mms"
    }

    var receiver = object: BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            showOnMap(intent?.getStringExtra("latitude"),
                intent?.getStringExtra("longitude"),
                14)
        }
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
     * logic
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
            var installed = packageManager.getInstalledPackages(PackageManager.GET_PERMISSIONS)

            var ml = mutableListOf<PackageInfo>()

            for (item in installed) {
                var has_SEND_SMS = (PackageManager.PERMISSION_GRANTED ==
                        packageManager.checkPermission(
                            Manifest.permission.SEND_SMS,
                            item.packageName))
                var has_RECEIVE_SMS = (PackageManager.PERMISSION_GRANTED ==
                        packageManager.checkPermission(
                            Manifest.permission.RECEIVE_SMS,
                            item.packageName))
                var has_BROADCAST_SMS = (PackageManager.PERMISSION_GRANTED ==
                        packageManager.checkPermission(
                            Manifest.permission.BROADCAST_SMS,
                            item.packageName))

                if (has_RECEIVE_SMS && has_SEND_SMS) {
                    ml.add(item)
                }
            }

            changeDefaultSmsApp(originSmsApp)

            showSelectionDialog(ml)
        }

        registerReceiver(receiver, IntentFilter("LOCATION_INFO_ARRIVED"));
    }

    override fun onStop() {
        super.onStop()

        if (isDefaultApp) {
            val alertText = "Please return and restore SMS app!"
            Toast.makeText(this, alertText, Toast.LENGTH_LONG).show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(receiver)
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

    private fun showSelectionDialog(list: List<PackageInfo>) {
        var builder =  AlertDialog.Builder(this)

        var ml = mutableListOf<CharSequence>()

        for (item in list) {
            ml.add(item.packageName)
        }

        var array = ml.toTypedArray()

        builder.setTitle("Select app")
            .setSingleChoiceItems(array, 1)
            {dialog: DialogInterface, which: Int ->
                Toast.makeText(this, "You selected!", Toast.LENGTH_SHORT).show()
            }

            .setPositiveButton("Ok")
            {dialog: DialogInterface, id: Int ->
                Toast.makeText(this, "OK!", Toast.LENGTH_SHORT).show()

            }
            .setNegativeButton("Cancel") {
                    dialog: DialogInterface, id: Int ->
                Toast.makeText(this, "No!", Toast.LENGTH_SHORT).show()
            }

        builder.create().show()
    }


    /**
     * Util
     */
    private fun showOnMap(latitude: String?, longitude: String?, zoom: Int) {
        val location = Uri.parse("geo:$latitude,$longitude?z=$zoom")
        val mapIntent = Intent(Intent.ACTION_VIEW, location)

        startActivityForResult(mapIntent, SHOW_ON_MAP)
    }
}
