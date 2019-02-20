package com.example.androidsmstest

import android.app.Notification
import android.content.BroadcastReceiver
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Telephony
import android.support.v4.app.NotificationCompat
import android.support.v4.app.NotificationManagerCompat
import android.telephony.SmsMessage
import android.widget.Toast


class SmsReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (context == null) return
        if (intent == null) return

        if (intent.action == Telephony.Sms.Intents.SMS_DELIVER_ACTION) {
            val messagesArray = Telephony.Sms.Intents.getMessagesFromIntent(intent)

            for(message in messagesArray) processMessage(context, message)
        }
    }

    private fun processMessage(context: Context, message: SmsMessage) {
        val body: String = message.messageBody

        if (body.startsWith("LS")) {
            val data = body.removePrefix("LS")
            if (isDataValid(data)) {
                dataReceived(context, data)
            }
            else {
                Toast.makeText(context, "Invalid LS message came!", Toast.LENGTH_SHORT).show()
            }
        }
        else {
            unrelatedMessageReceived(context, message)
        }
    }

    private fun dataReceived(context: Context, data: String) {
        Toast.makeText(context, data, Toast.LENGTH_SHORT).show()

        val splited = data.split(",")
        val lat = splited[0].trim()
        val long = splited[1].trim()

        var intent = Intent("LOCATION_INFO_ARRIVED")
        intent.putExtra("latitude", lat)
        intent.putExtra("longitude", long)

        context.sendBroadcast(intent)
    }

    private fun unrelatedMessageReceived(context: Context, message: SmsMessage) {
        val address = message.originatingAddress
        val body = message.messageBody

        // add message to inbox.
        val values = ContentValues()

        values.put("address", address)
        values.put("body", body)

        context.contentResolver.insert(Uri.parse("content://sms/inbox"), values)

        // make notification.
        val notificationText = "($address): $body"
        val mBuilder = NotificationCompat.Builder(context, "myChannel")
            .setSmallIcon(R.drawable.notification_icon_background)
            .setContentTitle("Message")
            .setContentText(notificationText)
            .setTicker(notificationText)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setDefaults(Notification.DEFAULT_ALL)

        with(NotificationManagerCompat.from(context)) {
            notify(0, mBuilder.build())
        }
    }

    private fun isDataValid(data: String) : Boolean {
        return (data.split(",").size == 2)
    }
}