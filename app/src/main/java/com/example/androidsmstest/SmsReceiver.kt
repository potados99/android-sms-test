package com.example.androidsmstest

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Telephony
import android.widget.Toast
import android.content.ContentValues
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

        values.put("address", message?.originatingAddress)
        values.put("body", message?.messageBody)

        context?.contentResolver?.insert(Uri.parse("content://sms/inbox"), values)

        Toast.makeText(context, "Unrelated message is came.", Toast.LENGTH_SHORT).show()
    }
}