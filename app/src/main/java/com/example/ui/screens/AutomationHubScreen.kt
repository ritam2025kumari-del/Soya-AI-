package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.FlashlightOn
import androidx.compose.material.icons.filled.NightsStay
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.SoyaMainViewModel
import com.example.ui.theme.AmberGaze
import com.example.ui.theme.CyanNeon
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.EmeraldShield
import com.example.ui.theme.VioletPulse

@Composable
fun AutomationHubScreen(
    viewModel: SoyaMainViewModel,
    onNavigateToChat: () -> Unit,
    modifier: Modifier = Modifier
) {
    val routines by viewModel.automationRoutines.collectAsState()
    val telemetry by viewModel.telemetry.collectAsState()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            // Header Banner
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurfaceElevated),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, EmeraldShield.copy(alpha = 0.3f), RoundedCornerShape(20.dp))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(EmeraldShield.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Bolt,
                                    contentDescription = null,
                                    tint = EmeraldShield,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Automation & Action Hub",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    text = "दैनिक रूटीन एवं डिवाइस ऑटोमेशन",
                                    fontSize = 12.sp,
                                    color = EmeraldShield
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Trigger multi-step routines, security audits, focus sessions, and hardware controls via one tap or natural Hindi voice commands.",
                        fontSize = 12.sp,
                        color = Color.LightGray,
                        lineHeight = 17.sp
                    )
                }
            }
        }

        item {
            Text(
                text = "FAST HARDWARE & SYSTEM INTENTS",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = CyanNeon,
                letterSpacing = 1.sp
            )
        }

        item {
            // Fast Action Grid
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkSurface),
                    modifier = Modifier
                        .weight(1f)
                        .clickable {
                            viewModel.executeFlashlight(true)
                        }
                        .border(1.dp, Color(0xFF223048), RoundedCornerShape(14.dp))
                        .testTag("action_torch_on")
                ) {
                    Column(modifier = Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.FlashlightOn, contentDescription = null, tint = AmberGaze, modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.height(6.dp))
                        Text("Torch ON", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        Text("टॉर्च चालू", fontSize = 10.sp, color = Color.Gray)
                    }
                }

                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkSurface),
                    modifier = Modifier
                        .weight(1f)
                        .clickable {
                            viewModel.executeQuickTimer(25)
                        }
                        .border(1.dp, Color(0xFF223048), RoundedCornerShape(14.dp))
                        .testTag("action_timer_25")
                ) {
                    Column(modifier = Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.Timer, contentDescription = null, tint = VioletPulse, modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.height(6.dp))
                        Text("25m Focus", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        Text("टाइमर सेट", fontSize = 10.sp, color = Color.Gray)
                    }
                }

                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkSurface),
                    modifier = Modifier
                        .weight(1f)
                        .clickable {
                            viewModel.executeSecurityAudit()
                            onNavigateToChat()
                        }
                        .border(1.dp, Color(0xFF223048), RoundedCornerShape(14.dp))
                        .testTag("action_security_audit")
                ) {
                    Column(modifier = Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.Security, contentDescription = null, tint = EmeraldShield, modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.height(6.dp))
                        Text("Safety Scan", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        Text("सुरक्षा ऑडिट", fontSize = 10.sp, color = Color.Gray)
                    }
                }
            }
        }

        item {
            Text(
                text = "SAVED AUTOMATION WORKFLOWS",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = CyanNeon,
                letterSpacing = 1.sp
            )
        }

        items(routines, key = { it.id }) { routine ->
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Color(0xFF202E44), RoundedCornerShape(16.dp))
                    .testTag("routine_card_${routine.id}")
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(
                                when (routine.id) {
                                    "morning_brief" -> AmberGaze.copy(alpha = 0.2f)
                                    "security_audit" -> EmeraldShield.copy(alpha = 0.2f)
                                    "night_protocol" -> VioletPulse.copy(alpha = 0.2f)
                                    else -> CyanNeon.copy(alpha = 0.2f)
                                }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = when (routine.id) {
                                "morning_brief" -> Icons.Default.WbSunny
                                "security_audit" -> Icons.Default.Security
                                "night_protocol" -> Icons.Default.NightsStay
                                else -> Icons.Default.Bolt
                            },
                            contentDescription = null,
                            tint = when (routine.id) {
                                "morning_brief" -> AmberGaze
                                "security_audit" -> EmeraldShield
                                "night_protocol" -> VioletPulse
                                else -> CyanNeon
                            },
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = routine.title,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = routine.hindiTitle,
                            fontSize = 12.sp,
                            color = CyanNeon
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = routine.description,
                            fontSize = 11.sp,
                            color = Color.Gray
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = Color(0xFF131D2D)
                        ) {
                            Text(
                                text = "🗣️ \"${routine.triggerPhrase}\"",
                                fontSize = 10.sp,
                                color = Color.LightGray,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        onClick = {
                            viewModel.runRoutine(routine)
                            onNavigateToChat()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = CyanNeon),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.testTag("run_routine_${routine.id}")
                    ) {
                        Icon(Icons.Default.PlayArrow, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Run", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }
        }
    }
}
