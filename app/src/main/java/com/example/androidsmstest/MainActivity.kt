package com.example.androidsmstest

import android.Manifest
import android.content.*
import android.content.pm.PackageManager
import android.databinding.DataBindingUtil
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.example.androidsmstest.viewmodels.MainViewModel
import com.example.androidsmstest.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding

    private var receiver = object: BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            showOnMap(intent?.getStringExtra("latitude"),
                intent?.getStringExtra("longitude"),
                14)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        getPermissions()

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.vm = MainViewModel(this)

        registerReceiver(receiver, IntentFilter("LOCATION_INFO_ARRIVED"));
    }

    override fun onStop() {
        super.onStop()

        binding.vm?.isDefaultApp?.let {
            if (it) {
                val alertText = "Please return and restore SMS app!"
                Toast.makeText(this, alertText, Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(receiver)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode != RESULT_OK) {
            var failmsg = ""
            when (requestCode) {
                1 -> failmsg = "Failed to change default SMS app."
                2 -> failmsg = "Failed to open map."
                else -> "Something failed."
            }
            Toast.makeText(this, failmsg, Toast.LENGTH_SHORT).show()
        }

        super.onActivityResult(requestCode, resultCode, data)
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

    private fun showOnMap(latitude: String?, longitude: String?, zoom: Int) {
        val location = Uri.parse("geo:$latitude,$longitude?z=$zoom")
        val mapIntent = Intent(Intent.ACTION_VIEW, location)

        startActivityForResult(mapIntent, 2)
    }
}
