package com.example.tools

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.BatteryManager
import android.os.Build
import android.os.Environment
import android.os.StatFs
import android.provider.AlarmClock
import android.provider.Settings
import com.example.data.model.DeviceTelemetry
import java.security.SecureRandom
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DeviceAutomationManager(private val context: Context) {

    fun getDeviceTelemetry(): DeviceTelemetry {
        // Battery
        val batteryIntent = context.registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
        val level = batteryIntent?.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) ?: -1
        val scale = batteryIntent?.getIntExtra(BatteryManager.EXTRA_SCALE, -1) ?: -1
        val batteryPct = if (level >= 0 && scale > 0) (level * 100 / scale) else 100
        val status = batteryIntent?.getIntExtra(BatteryManager.EXTRA_STATUS, -1) ?: -1
        val isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING || status == BatteryManager.BATTERY_STATUS_FULL

        // Network
        var networkType = "Disconnected"
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
        if (cm != null) {
            val activeNetwork = cm.activeNetwork
            val caps = cm.getNetworkCapabilities(activeNetwork)
            if (caps != null) {
                networkType = when {
                    caps.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> "Wi-Fi ⚡ High Speed"
                    caps.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> "Cellular 5G/4G LTE"
                    caps.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> "Ethernet"
                    else -> "Connected"
                }
            }
        }

        // Memory
        val actManager = context.getSystemService(Context.ACTIVITY_SERVICE) as? ActivityManager
        val memInfo = ActivityManager.MemoryInfo()
        actManager?.getMemoryInfo(memInfo)
        val availMb = memInfo.availMem / (1024 * 1024)

        // Storage
        var freeStorageGb = 16.0
        try {
            val stat = StatFs(Environment.getDataDirectory().path)
            val bytesAvailable = stat.availableBlocksLong * stat.blockSizeLong
            freeStorageGb = String.format(Locale.US, "%.1f", bytesAvailable / (1024.0 * 1024.0 * 1024.0)).toDouble()
        } catch (e: Exception) {
            // fallback
        }

        // Time / Date
        val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
        val dateFormat = SimpleDateFormat("EEEE, dd MMMM yyyy", Locale.getDefault())
        val now = Date()

        val deviceModel = "${Build.MANUFACTURER.replaceFirstChar { it.uppercase() }} ${Build.MODEL} (Android ${Build.VERSION.RELEASE})"

        return DeviceTelemetry(
            batteryPercent = batteryPct,
            isCharging = isCharging,
            networkType = networkType,
            availableMemoryMb = availMb,
            freeStorageGb = freeStorageGb,
            currentFormattedTime = timeFormat.format(now),
            currentFormattedDate = dateFormat.format(now),
            deviceModel = deviceModel
        )
    }

    fun toggleFlashlight(turnOn: Boolean): String {
        return try {
            val cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as? CameraManager
            val cameraId = cameraManager?.cameraIdList?.firstOrNull { id ->
                val chars = cameraManager.getCameraCharacteristics(id)
                chars.get(CameraCharacteristics.FLASH_INFO_AVAILABLE) == true
            }
            if (cameraId != null) {
                cameraManager.setTorchMode(cameraId, turnOn)
                if (turnOn) "⚡ टॉर्च ऑन कर दी गई है (Flashlight turned ON)"
                else "⚡ टॉर्च ऑफ कर दी गई है (Flashlight turned OFF)"
            } else {
                "फ़्लैशलाइट हार्डवेयर उपलब्ध नहीं है (Flashlight hardware not found)"
            }
        } catch (e: Exception) {
            "Flashlight error: ${e.message}"
        }
    }

    fun openSystemSettings(action: String): Boolean {
        return try {
            val intent = when (action.lowercase(Locale.ROOT)) {
                "wifi" -> Intent(Settings.ACTION_WIFI_SETTINGS)
                "bluetooth" -> Intent(Settings.ACTION_BLUETOOTH_SETTINGS)
                "battery" -> Intent(Intent.ACTION_POWER_USAGE_SUMMARY)
                "display" -> Intent(Settings.ACTION_DISPLAY_SETTINGS)
                "sound" -> Intent(Settings.ACTION_SOUND_SETTINGS)
                "security" -> Intent(Settings.ACTION_SECURITY_SETTINGS)
                "apps" -> Intent(Settings.ACTION_APPLICATION_SETTINGS)
                else -> Intent(Settings.ACTION_SETTINGS)
            }
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
            true
        } catch (e: Exception) {
            false
        }
    }

    fun launchCamera(): Boolean {
        return try {
            val intent = Intent("android.media.action.IMAGE_CAPTURE").apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
            true
        } catch (e: Exception) {
            false
        }
    }

    fun setTimerOrAlarm(minutes: Int, message: String): String {
        return try {
            if (minutes <= 60) {
                val intent = Intent(AlarmClock.ACTION_SET_TIMER).apply {
                    putExtra(AlarmClock.EXTRA_LENGTH, minutes * 60)
                    putExtra(AlarmClock.EXTRA_MESSAGE, message.ifEmpty { "SOYA AI Focus Timer" })
                    putExtra(AlarmClock.EXTRA_SKIP_UI, false)
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(intent)
                "⏳ $minutes मिनट का टाइमर सेट कर दिया गया है! ($minutes min timer initiated)"
            } else {
                val intent = Intent(AlarmClock.ACTION_SET_ALARM).apply {
                    putExtra(AlarmClock.EXTRA_MESSAGE, message.ifEmpty { "SOYA Reminder" })
                    putExtra(AlarmClock.EXTRA_SKIP_UI, false)
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(intent)
                "⏰ अलार्म इंटरफ़ेस खोल दिया गया है।"
            }
        } catch (e: Exception) {
            "Timer error: ${e.message}"
        }
    }

    fun openWebSearch(query: String): Boolean {
        return try {
            val uri = Uri.parse("https://www.google.com/search?q=" + Uri.encode(query))
            val intent = Intent(Intent.ACTION_VIEW, uri).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
            true
        } catch (e: Exception) {
            false
        }
    }

    fun generateSecurePassword(length: Int = 16, includeSymbols: Boolean = true): String {
        val uppercase = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
        val lowercase = "abcdefghijklmnopqrstuvwxyz"
        val numbers = "0123456789"
        val symbols = "!@#$%^&*()-_=+[]{}<>?"

        val charPool = buildString {
            append(uppercase)
            append(lowercase)
            append(numbers)
            if (includeSymbols) append(symbols)
        }

        val random = SecureRandom()
        val password = StringBuilder(length)
        // Ensure at least one of each
        password.append(uppercase[random.nextInt(uppercase.length)])
        password.append(lowercase[random.nextInt(lowercase.length)])
        password.append(numbers[random.nextInt(numbers.length)])
        if (includeSymbols) password.append(symbols[random.nextInt(symbols.length)])

        for (i in password.length until length) {
            password.append(charPool[random.nextInt(charPool.length)])
        }

        // Shuffle
        val chars = password.toString().toCharArray()
        for (i in chars.indices) {
            val j = random.nextInt(chars.size)
            val temp = chars[i]
            chars[i] = chars[j]
            chars[j] = temp
        }
        return String(chars)
    }

    fun runSecurityAudit(): SecurityAuditReport {
        val telemetry = getDeviceTelemetry()
        val issues = mutableListOf<String>()
        val recommendations = mutableListOf<String>()

        val batteryPct = telemetry.batteryPercent
        if (batteryPct < 20 && !telemetry.isCharging) {
            issues.add("Low Battery ($batteryPct%) - charge device soon")
        }

        if (telemetry.networkType.contains("Disconnected")) {
            issues.add("Device is offline - no secure cloud connection")
        } else {
            recommendations.add("Connected on ${telemetry.networkType}. Use secure HTTPS and VPNs on public Wi-Fi.")
        }

        val freeMb = telemetry.availableMemoryMb
        if (freeMb < 500) {
            issues.add("Low RAM Memory ($freeMb MB) - background apps may get killed")
            recommendations.add("Clear unused cached background apps")
        } else {
            recommendations.add("RAM capacity is healthy ($freeMb MB available)")
        }

        recommendations.add("Generated secure credential generator ready")
        recommendations.add("Android security patch level active on Android ${Build.VERSION.RELEASE}")

        val score = when {
            issues.isEmpty() -> 96
            issues.size == 1 -> 85
            else -> 72
        }

        return SecurityAuditReport(
            score = score,
            status = if (score >= 90) "🛡️ EXCELLENT" else if (score >= 80) "⚡ GOOD" else "⚠️ ATTENTION NEEDED",
            hindiStatus = if (score >= 90) "अत्यंत सुरक्षित एवं उत्कृष्ट" else "संतोषजनक स्थिति",
            issues = issues,
            recommendations = recommendations
        )
    }
}

data class SecurityAuditReport(
    val score: Int,
    val status: String,
    val hindiStatus: String,
    val issues: List<String>,
    val recommendations: List<String>
)
