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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.PersonalityMode
import com.example.ui.SoyaMainViewModel
import com.example.ui.theme.AmberGaze
import com.example.ui.theme.CyanNeon
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.EmeraldShield
import com.example.ui.theme.VioletPulse

@Composable
fun SettingsScreen(
    viewModel: SoyaMainViewModel,
    modifier: Modifier = Modifier
) {
    val userName by viewModel.preferencesManager.userName.collectAsState()
    val customApiKey by viewModel.preferencesManager.customApiKey.collectAsState()
    val selectedModel by viewModel.preferencesManager.selectedModel.collectAsState()
    val selectedPersona by viewModel.preferencesManager.selectedPersona.collectAsState()
    val hapticFeedback by viewModel.preferencesManager.hapticFeedback.collectAsState()

    var editName by remember(userName) { mutableStateOf(userName) }
    var editApiKey by remember(customApiKey) { mutableStateOf(customApiKey) }
    var showClearDialog by remember { mutableStateOf(false) }

    val models = listOf(
        "gemini-3.5-flash" to "Gemini 3.5 Flash (Ultra-Fast & Smart)",
        "gemini-3.1-pro-preview" to "Gemini 3.1 Pro (Deep STEM & Reasoning)",
        "gemini-3.1-flash-lite-preview" to "Gemini 3.1 Flash Lite (Low Latency)"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = DarkSurfaceElevated),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, CyanNeon.copy(alpha = 0.3f), RoundedCornerShape(20.dp))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(CyanNeon.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = null,
                            tint = CyanNeon,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "SOYA AI Engine Settings",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "कोर ब्रेन, पर्सनालिटी एवं API विन्यास",
                            fontSize = 12.sp,
                            color = CyanNeon
                        )
                    }
                }
            }
        }

        // User Profile Name
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = DarkSurface),
            modifier = Modifier.fillMaxWidth().border(1.dp, Color(0xFF223048), RoundedCornerShape(16.dp))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "USER PROFILE",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = CyanNeon,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = editName,
                    onValueChange = {
                        editName = it
                        viewModel.preferencesManager.setUserName(it)
                    },
                    label = { Text("Your Name (SOYA addresses you by this)") },
                    leadingIcon = {
                        Icon(Icons.Default.Person, contentDescription = null, tint = CyanNeon)
                    },
                    modifier = Modifier.fillMaxWidth().testTag("user_name_input"),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CyanNeon,
                        unfocusedBorderColor = Color(0xFF2A3D5A),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    )
                )
            }
        }

        // Persona Switcher
        Text(
            text = "ASSISTANT PERSONA MODE",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = CyanNeon,
            letterSpacing = 1.sp
        )

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            PersonalityMode.entries.forEach { persona ->
                val isSelected = persona == selectedPersona
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelected) Color(0xFF1C273C) else DarkSurface
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { viewModel.preferencesManager.setSelectedPersona(persona) }
                        .border(
                            width = if (isSelected) 1.5.dp else 1.dp,
                            color = if (isSelected) CyanNeon else Color(0xFF223048),
                            shape = RoundedCornerShape(14.dp)
                        )
                        .testTag("persona_${persona.id}")
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(if (isSelected) CyanNeon.copy(alpha = 0.2f) else Color(0xFF131D2D)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = when (persona) {
                                    PersonalityMode.OMNI -> Icons.Default.AutoAwesome
                                    PersonalityMode.SHUDDH_HINDI -> Icons.Default.RecordVoiceOver
                                    PersonalityMode.HINGLISH_TECH -> Icons.Default.Code
                                    PersonalityMode.CYBER_SENTINEL -> Icons.Default.Security
                                    PersonalityMode.AUTOMATION_COMMANDER -> Icons.Default.Bolt
                                },
                                contentDescription = null,
                                tint = if (isSelected) CyanNeon else Color.Gray,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "${persona.title} (${persona.hindiTitle})",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) CyanNeon else Color.White
                            )
                            Text(
                                text = persona.subtitle,
                                fontSize = 11.sp,
                                color = Color.LightGray
                            )
                        }

                        if (isSelected) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Selected",
                                tint = EmeraldShield,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        }

        // Gemini AI Model Selector
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = DarkSurface),
            modifier = Modifier.fillMaxWidth().border(1.dp, Color(0xFF223048), RoundedCornerShape(16.dp))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "GEMINI CORE BRAIN MODEL",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = VioletPulse,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(10.dp))

                models.forEach { (modelId, desc) ->
                    val isModelSelected = (selectedModel == modelId)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp)
                            .clickable { viewModel.preferencesManager.setSelectedModel(modelId) },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(16.dp)
                                .clip(CircleShape)
                                .background(if (isModelSelected) CyanNeon else Color.Transparent)
                                .border(2.dp, if (isModelSelected) CyanNeon else Color.Gray, CircleShape)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = modelId,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = if (isModelSelected) CyanNeon else Color.White
                            )
                            Text(text = desc, fontSize = 11.sp, color = Color.Gray)
                        }
                    }
                }
            }
        }

        // API Key Management Card
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = DarkSurface),
            modifier = Modifier.fillMaxWidth().border(1.dp, Color(0xFF223048), RoundedCornerShape(16.dp))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Key, contentDescription = null, tint = AmberGaze, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "GEMINI API KEY CONFIGURATION",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = AmberGaze
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "By default, SOYA AI uses BuildConfig from AI Studio Secrets. You can also override with a custom Gemini API key here:",
                    fontSize = 11.sp,
                    color = Color.LightGray
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = editApiKey,
                    onValueChange = {
                        editApiKey = it
                        viewModel.preferencesManager.setCustomApiKey(it)
                    },
                    placeholder = { Text("Enter custom Gemini API key (optional)", color = Color.Gray, fontSize = 12.sp) },
                    modifier = Modifier.fillMaxWidth().testTag("custom_api_key_input"),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AmberGaze,
                        unfocusedBorderColor = Color(0xFF2A3D5A),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    maxLines = 1
                )
            }
        }

        // Haptic Toggle
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = DarkSurface),
            modifier = Modifier.fillMaxWidth().border(1.dp, Color(0xFF223048), RoundedCornerShape(16.dp))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Vibration, contentDescription = null, tint = CyanNeon, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text("Haptic Feedback", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
                        Text("Vibrate on voice and actions", fontSize = 11.sp, color = Color.Gray)
                    }
                }

                Switch(
                    checked = hapticFeedback,
                    onCheckedChange = { viewModel.preferencesManager.setHapticFeedback(it) },
                    colors = SwitchDefaults.colors(checkedThumbColor = Color.Black, checkedTrackColor = CyanNeon),
                    modifier = Modifier.testTag("haptic_switch")
                )
            }
        }

        // Clear Chat History
        Button(
            onClick = { showClearDialog = true },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3B1824)),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth().testTag("clear_history_button")
        ) {
            Icon(Icons.Default.DeleteSweep, contentDescription = null, tint = Color(0xFFFF5252), modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Clear Chat History", color = Color(0xFFFF5252), fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(20.dp))
    }

    if (showClearDialog) {
        AlertDialog(
            onDismissRequest = { showClearDialog = false },
            title = { Text("Clear Conversation History?") },
            text = { Text("This will remove all stored chat messages from the local Room database.") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.clearChatHistory()
                        showClearDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF5252))
                ) {
                    Text("Clear All", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearDialog = false }) {
                    Text("Cancel", color = Color.LightGray)
                }
            },
            containerColor = DarkSurfaceElevated,
            titleContentColor = Color.White,
            textContentColor = Color.LightGray
        )
    }
}
