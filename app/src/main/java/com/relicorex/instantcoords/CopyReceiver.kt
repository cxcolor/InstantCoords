package com.relicorex.instantcoords

import android.content.BroadcastReceiver
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast

class CopyReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == "com.relicorex.instantcoords.COPY_ACTION") {
            val coords = intent.getStringExtra("coords") ?: return

            if (!coords.contains("Waiting") && !coords.contains("OFF")) {
                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clip = ClipData.newPlainText("Coordinates", coords)
                clipboard.setPrimaryClip(clip)

                Toast.makeText(context, "Successfully copied: $coords", Toast.LENGTH_SHORT).show()
            }
        }
    }
}