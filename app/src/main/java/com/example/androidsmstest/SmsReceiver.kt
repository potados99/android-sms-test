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
        if (context == null) return // null catch here.
        if (intent == null) return

        if (intent.action == Telephony.Sms.Intents.SMS_DELIVER_ACTION) {
            val messagesArray = Telephony.Sms.Intents.getMessagesFromIntent(intent)

            for(message in messagesArray) processMessage(context, message)
        }
    }

    private fun processMessage(context: Context, message: SmsMessage) {
        val body: String = message.messageBody

        if (body.startsWith("LS")) {
            dataReceived(context, body.removePrefix("LS"))
        }
        else {
            unrelatedMessageReceived(context, message)
        }
    }

    private fun dataReceived(context: Context, data: String) {
        Toast.makeText(context, data, Toast.LENGTH_SHORT).show()
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
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Message")
            .setContentText(notificationText)
            .setTicker(notificationText)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setDefaults(Notification.DEFAULT_ALL)

        with(NotificationManagerCompat.from(context)) {
            notify(0, mBuilder.build())
        }
    }
}