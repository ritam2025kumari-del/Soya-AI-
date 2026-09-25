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
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
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
import com.example.data.model.VoiceLanguage
import com.example.ui.SoyaMainViewModel
import com.example.ui.components.AudioWaveVisualizer
import com.example.ui.theme.AmberGaze
import com.example.ui.theme.CyanNeon
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.EmeraldShield
import com.example.ui.theme.VioletPulse

@Composable
fun VoiceStudioScreen(
    viewModel: SoyaMainViewModel,
    modifier: Modifier = Modifier
) {
    val selectedLang by viewModel.preferencesManager.voiceLanguage.collectAsState()
    val pitch by viewModel.preferencesManager.voicePitch.collectAsState()
    val speed by viewModel.preferencesManager.voiceSpeed.collectAsState()
    val autoSpeak by viewModel.preferencesManager.autoSpeak.collectAsState()
    val isSpeaking by viewModel.voiceEngine.isSpeaking.collectAsState()
    val isHindiSupported by viewModel.voiceEngine.isHindiSupported.collectAsState()
    val audioAmp by viewModel.audioAmplitude.collectAsState()

    var testCustomPhrase by remember {
        mutableStateOf("नमस्ते! मैं SOYA AI हूँ। मेरी आवाज़ अब और भी प्राकृतिक और स्पष्ट है।")
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Hero Header
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = DarkSurfaceElevated),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, CyanNeon.copy(alpha = 0.3f), RoundedCornerShape(20.dp))
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape)
                                .background(VioletPulse.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.RecordVoiceOver,
                                contentDescription = null,
                                tint = CyanNeon,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Natural Voice Studio",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = "प्राकृतिक हिंदी व बहुभाषी आवाज़ इंजन",
                                fontSize = 12.sp,
                                color = CyanNeon
                            )
                        }
                    }

                    if (isSpeaking) {
                        AudioWaveVisualizer(
                            isActive = true,
                            amplitude = audioAmp,
                            color = EmeraldShield
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "SOYA integrates authentic Devanagari Hindi Text-To-Speech with dynamic phonetics tuning, speech rate control, and instant preview.",
                    fontSize = 12.sp,
                    color = Color.LightGray,
                    lineHeight = 18.sp
                )
            }
        }

        // Voice Language Selector
        Text(
            text = "SELECT ASSISTANT VOICE",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = CyanNeon,
            letterSpacing = 1.sp
        )

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            VoiceLanguage.entries.forEach { lang ->
                val isSelected = lang == selectedLang
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelected) Color(0xFF1B283E) else DarkSurface
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            viewModel.setVoiceConfig(lang, pitch, speed)
                        }
                        .border(
                            width = if (isSelected) 1.5.dp else 1.dp,
                            color = if (isSelected) CyanNeon else Color(0xFF223048),
                            shape = RoundedCornerShape(14.dp)
                        )
                        .testTag("voice_option_${lang.code}")
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = lang.displayName,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = if (isSelected) CyanNeon else Color.White
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = lang.sampleText,
                                fontSize = 11.sp,
                                color = Color.Gray,
                                maxLines = 1
                            )
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(
                                onClick = {
                                    viewModel.voiceEngine.speak(lang.sampleText, lang)
                                },
                                modifier = Modifier.size(32.dp).testTag("play_sample_${lang.code}")
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                                    contentDescription = "Test Sample",
                                    tint = CyanNeon,
                                    modifier = Modifier.size(18.dp)
                                )
                            }

                            if (isSelected) {
                                Spacer(modifier = Modifier.width(4.dp))
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
        }

        // Modulation Controls (Pitch & Speed)
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = DarkSurface),
            modifier = Modifier.fillMaxWidth().border(1.dp, Color(0xFF223048), RoundedCornerShape(16.dp))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Tune,
                            contentDescription = null,
                            tint = VioletPulse,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Voice Pitch: ${String.format("%.2f", pitch)}x",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.White
                        )
                    }
                }

                Slider(
                    value = pitch,
                    onValueChange = { newPitch ->
                        viewModel.setVoiceConfig(selectedLang, newPitch, speed)
                    },
                    valueRange = 0.6f..1.6f,
                    steps = 10,
                    colors = SliderDefaults.colors(
                        thumbColor = VioletPulse,
                        activeTrackColor = VioletPulse,
                        inactiveTrackColor = Color(0xFF1E2D44)
                    ),
                    modifier = Modifier.testTag("pitch_slider")
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Speed,
                            contentDescription = null,
                            tint = CyanNeon,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Speech Speed Rate: ${String.format("%.2f", speed)}x",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.White
                        )
                    }
                }

                Slider(
                    value = speed,
                    onValueChange = { newSpeed ->
                        viewModel.setVoiceConfig(selectedLang, pitch, newSpeed)
                    },
                    valueRange = 0.5f..1.5f,
                    steps = 10,
                    colors = SliderDefaults.colors(
                        thumbColor = CyanNeon,
                        activeTrackColor = CyanNeon,
                        inactiveTrackColor = Color(0xFF1E2D44)
                    ),
                    modifier = Modifier.testTag("speed_slider")
                )
            }
        }

        // Live Voice Tester Playground
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = DarkSurface),
            modifier = Modifier.fillMaxWidth().border(1.dp, Color(0xFF223048), RoundedCornerShape(16.dp))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "LIVE PRONUNCIATION TESTER",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = AmberGaze
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = testCustomPhrase,
                    onValueChange = { testCustomPhrase = it },
                    label = { Text("Enter Hindi or English sentence to speak") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("test_voice_text_input"),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CyanNeon,
                        unfocusedBorderColor = Color(0xFF2A3D5A),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    maxLines = 3
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = {
                            viewModel.speakMessage(testCustomPhrase)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = CyanNeon),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("audition_voice_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = null,
                            tint = Color.Black,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Audition Voice", color = Color.Black, fontWeight = FontWeight.Bold)
                    }

                    if (isSpeaking) {
                        Button(
                            onClick = { viewModel.stopSpeaking() },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444)),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.testTag("stop_voice_button")
                        ) {
                            Icon(imageVector = Icons.Default.Stop, contentDescription = null, tint = Color.White)
                        }
                    }
                }
            }
        }

        // Auto Speak Toggle
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
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Auto-Speak AI Responses",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                    Text(
                        text = "Automatically pronounce assistant replies aloud in Hindi/English",
                        fontSize = 11.sp,
                        color = Color.Gray
                    )
                }

                Switch(
                    checked = autoSpeak,
                    onCheckedChange = { viewModel.preferencesManager.setAutoSpeak(it) },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.Black,
                        checkedTrackColor = CyanNeon
                    ),
                    modifier = Modifier.testTag("auto_speak_switch")
                )
            }
        }
    }
}
