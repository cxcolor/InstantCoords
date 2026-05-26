package com.relicorex.instantcoords

import android.Manifest
import android.annotation.SuppressLint
import android.app.ActivityManager
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.PowerManager
import android.provider.Settings
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.net.toUri

class MainActivity : ComponentActivity() {

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val fineLocationGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false
        if (fineLocationGranted) {
            checkBatteryOptimization()
        } else {
            Toast.makeText(this, "GPS permission is required for this app to work.", Toast.LENGTH_LONG).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            var isTracking by remember { mutableStateOf(isServiceRunning(CoordsService::class.java)) }

            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Button(
                    onClick = {
                        if (hasPermissions()) {
                            toggleService { running -> isTracking = running }
                        } else {
                            requestPermissions()
                        }
                    },
                    modifier = Modifier.padding(24.dp).fillMaxWidth().height(64.dp)
                ) {
                    Text(
                        text = if (isTracking) "Stop Tracking" else "Start Tracking",
                        fontSize = 18.sp
                    )
                }
            }
        }
    }

    private fun hasPermissions(): Boolean {
        val fineLocation = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
        val postNotification = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
        } else {
            PackageManager.PERMISSION_GRANTED
        }
        return fineLocation == PackageManager.PERMISSION_GRANTED && postNotification == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermissions() {
        val permissionsToRequest = mutableListOf(Manifest.permission.ACCESS_FINE_LOCATION)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissionsToRequest.add(Manifest.permission.POST_NOTIFICATIONS)
        }
        requestPermissionLauncher.launch(permissionsToRequest.toTypedArray())
    }

    @SuppressLint("BatteryLife")
    private fun checkBatteryOptimization() {
        val pm = getSystemService(POWER_SERVICE) as PowerManager
        if (!pm.isIgnoringBatteryOptimizations(packageName)) {
            val intent = Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS).apply {
                data = "package:$packageName".toUri()
            }
            startActivity(intent)
        }
    }

    private fun toggleService(onStatusChanged: (Boolean) -> Unit) {
        val intent = Intent(this, CoordsService::class.java)
        if (isServiceRunning(CoordsService::class.java)) {
            stopService(intent)
            onStatusChanged(false)
            Toast.makeText(this, "Tracking Stopped", Toast.LENGTH_SHORT).show()
        } else {
            ContextCompat.startForegroundService(this, intent)
            onStatusChanged(true)
            Toast.makeText(this, "Tracking Active in Notification Shade", Toast.LENGTH_SHORT).show()
        }
    }

    private fun isServiceRunning(serviceClass: Class<*>): Boolean {
        val manager = getSystemService(ACTIVITY_SERVICE) as ActivityManager
        @Suppress("DEPRECATION")
        for (service in manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.name == service.service.className) {
                return true
            }
        }
        return false
    }
}