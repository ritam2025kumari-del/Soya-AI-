package com.example.ui.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material.icons.filled.FlashlightOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CyanNeon
import com.example.ui.theme.EmeraldShield
import com.example.ui.theme.VioletPulse

data class QuickCommand(
    val label: String,
    val prompt: String,
    val icon: ImageVector,
    val tintColor: Color
)

@Composable
fun QuickCommandChips(
    onCommandClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val commands = listOf(
        QuickCommand(
            label = "नमस्ते सोया!",
            prompt = "नमस्ते सोया! तुम आज मेरी क्या मदद कर सकती हो?",
            icon = Icons.Default.RecordVoiceOver,
            tintColor = CyanNeon
        ),
        QuickCommand(
            label = "बैटरी स्टेटस 🔋",
            prompt = "बैटरी स्टेटस बताओ",
            icon = Icons.Default.BatteryChargingFull,
            tintColor = EmeraldShield
        ),
        QuickCommand(
            label = "Security Scan 🛡️",
            prompt = "डिवाइस का Security Audit Scan चलाओ",
            icon = Icons.Default.Security,
            tintColor = VioletPulse
        ),
        QuickCommand(
            label = "Morning Brief 🌅",
            prompt = "Morning briefing शुरू करो",
            icon = Icons.Default.WbSunny,
            tintColor = CyanNeon
        ),
        QuickCommand(
            label = "Safe Password 🔐",
            prompt = "एक सुरक्षित मजबूत पासवर्ड जनरेट करो",
            icon = Icons.Default.Lock,
            tintColor = EmeraldShield
        ),
        QuickCommand(
            label = "Focus Timer ⏳",
            prompt = "25 मिनट का फोकस टाइमर सेट करो",
            icon = Icons.Default.Timer,
            tintColor = VioletPulse
        ),
        QuickCommand(
            label = "टॉर्च चालू करो 💡",
            prompt = "टॉर्च चालू करो (Torch on)",
            icon = Icons.Default.FlashlightOn,
            tintColor = CyanNeon
        )
    )

    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 12.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        commands.forEachIndexed { index, cmd ->
            AssistChip(
                onClick = { onCommandClick(cmd.prompt) },
                label = { Text(cmd.label, fontSize = 12.sp, color = Color.White) },
                leadingIcon = {
                    Icon(
                        imageVector = cmd.icon,
                        contentDescription = null,
                        tint = cmd.tintColor,
                        modifier = Modifier.size(16.dp)
                    )
                },
                colors = AssistChipDefaults.assistChipColors(
                    containerColor = Color(0xFF162032)
                ),
                border = AssistChipDefaults.assistChipBorder(
                    enabled = true,
                    borderColor = cmd.tintColor.copy(alpha = 0.4f),
                    borderWidth = 1.dp
                ),
                modifier = Modifier.testTag("quick_command_chip_$index")
            )
        }
    }
}
