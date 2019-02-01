package com.example.androidsmstest

import android.app.Notification
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Telephony
import android.widget.Toast
import android.content.ContentValues
import android.support.v4.app.NotificationCompat
import android.support.v4.app.NotificationManagerCompat
import android.telephony.SmsMessage


class SmsReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val actionToRespond = Telephony.Sms.Intents.SMS_DELIVER_ACTION

        if (intent?.action == actionToRespond) {
            val messages = Telephony.Sms.Intents.getMessagesFromIntent(intent)

            for(message in messages) processMessage(context, message)
        }
    }

    private fun processMessage(context: Context?, message: SmsMessage?) {
        val messageBody: String = message?.messageBody ?: "null message"

        if (messageBody.startsWith("LS")) {
            dataReceived(context, messageBody.removePrefix("LS"))
        }
        else {
            unrelatedMessageReceived(context, message)
        }
    }

    private fun dataReceived(context: Context?, data: String) {
        Toast.makeText(context, data, Toast.LENGTH_SHORT).show()
    }

    private fun unrelatedMessageReceived(context: Context?, message: SmsMessage?) {
        val values = ContentValues()
        val address = message?.originatingAddress
        val body = message?.messageBody

        values.put("address", address)
        values.put("body", body)

        context?.contentResolver?.insert(Uri.parse("content://sms/inbox"), values)

        var mBuilder = NotificationCompat.Builder(context!!, "myChannel")
            .setSmallIcon(R.drawable.notification_icon_background)
            .setContentTitle(address)
            .setContentText(body)
            .setTicker("($address): $body")
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setDefaults(Notification.DEFAULT_ALL)

        with(NotificationManagerCompat.from(context)) {
            notify(0, mBuilder.build())
        }
    }
}