package com.example.androidsmstest

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Telephony
import android.widget.Toast
import android.content.ContentValues


class SmsReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent == null) {
            println("SmsReceiver.onReceive")
            println("context = [$context], intent = [$intent]")
            println("intent is null")

            return // Do not throw!
        }

        if (intent.action == Telephony.Sms.Intents.SMS_DELIVER_ACTION) {
            val messages = Telephony.Sms.Intents.getMessagesFromIntent(intent)

            for(smsMessage in messages) {
                val messageBody: String = smsMessage.messageBody

                if (messageBody.startsWith("LS")) {
                    val payload = messageBody.removePrefix("LS")
                    Toast.makeText(context, payload, Toast.LENGTH_SHORT).show()
                }
                else {
                    val values = ContentValues()
                    values.put("address", smsMessage.originatingAddress)
                    values.put("body", smsMessage.messageBody)

                    context?.contentResolver?.insert(Uri.parse("content://sms/inbox"), values)
                }

            }
        }
    }
}