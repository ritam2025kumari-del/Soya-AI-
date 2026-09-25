package com.example.data.local

import android.content.Context
import android.content.SharedPreferences
import com.example.BuildConfig
import com.example.data.model.PersonalityMode
import com.example.data.model.VoiceLanguage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class PreferencesManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("soya_ai_prefs", Context.MODE_PRIVATE)

    private val _userName = MutableStateFlow(prefs.getString(KEY_USER_NAME, "User") ?: "User")
    val userName: StateFlow<String> = _userName.asStateFlow()

    private val _customApiKey = MutableStateFlow(prefs.getString(KEY_CUSTOM_API_KEY, "") ?: "")
    val customApiKey: StateFlow<String> = _customApiKey.asStateFlow()

    private val _selectedModel = MutableStateFlow(prefs.getString(KEY_SELECTED_MODEL, "gemini-3.5-flash") ?: "gemini-3.5-flash")
    val selectedModel: StateFlow<String> = _selectedModel.asStateFlow()

    private val _selectedPersona = MutableStateFlow(
        PersonalityMode.entries.find { it.id == prefs.getString(KEY_SELECTED_PERSONA, PersonalityMode.OMNI.id) }
            ?: PersonalityMode.OMNI
    )
    val selectedPersona: StateFlow<PersonalityMode> = _selectedPersona.asStateFlow()

    private val _voiceLanguage = MutableStateFlow(
        VoiceLanguage.entries.find { it.code == prefs.getString(KEY_VOICE_LANG, VoiceLanguage.HINDI_INDIA.code) }
            ?: VoiceLanguage.HINDI_INDIA
    )
    val voiceLanguage: StateFlow<VoiceLanguage> = _voiceLanguage.asStateFlow()

    private val _voicePitch = MutableStateFlow(prefs.getFloat(KEY_VOICE_PITCH, 1.05f))
    val voicePitch: StateFlow<Float> = _voicePitch.asStateFlow()

    private val _voiceSpeed = MutableStateFlow(prefs.getFloat(KEY_VOICE_SPEED, 0.95f))
    val voiceSpeed: StateFlow<Float> = _voiceSpeed.asStateFlow()

    private val _autoSpeak = MutableStateFlow(prefs.getBoolean(KEY_AUTO_SPEAK, true))
    val autoSpeak: StateFlow<Boolean> = _autoSpeak.asStateFlow()

    private val _hapticFeedback = MutableStateFlow(prefs.getBoolean(KEY_HAPTIC, true))
    val hapticFeedback: StateFlow<Boolean> = _hapticFeedback.asStateFlow()

    fun getEffectiveApiKey(): String {
        val custom = _customApiKey.value.trim()
        if (custom.isNotEmpty()) return custom
        return try {
            BuildConfig.GEMINI_API_KEY
        } catch (e: Exception) {
            ""
        }
    }

    fun setUserName(name: String) {
        prefs.edit().putString(KEY_USER_NAME, name).apply()
        _userName.value = name
    }

    fun setCustomApiKey(key: String) {
        prefs.edit().putString(KEY_CUSTOM_API_KEY, key).apply()
        _customApiKey.value = key
    }

    fun setSelectedModel(model: String) {
        prefs.edit().putString(KEY_SELECTED_MODEL, model).apply()
        _selectedModel.value = model
    }

    fun setSelectedPersona(persona: PersonalityMode) {
        prefs.edit().putString(KEY_SELECTED_PERSONA, persona.id).apply()
        _selectedPersona.value = persona
    }

    fun setVoiceLanguage(language: VoiceLanguage) {
        prefs.edit().putString(KEY_VOICE_LANG, language.code).apply()
        _voiceLanguage.value = language
    }

    fun setVoicePitch(pitch: Float) {
        prefs.edit().putFloat(KEY_VOICE_PITCH, pitch).apply()
        _voicePitch.value = pitch
    }

    fun setVoiceSpeed(speed: Float) {
        prefs.edit().putFloat(KEY_VOICE_SPEED, speed).apply()
        _voiceSpeed.value = speed
    }

    fun setAutoSpeak(auto: Boolean) {
        prefs.edit().putBoolean(KEY_AUTO_SPEAK, auto).apply()
        _autoSpeak.value = auto
    }

    fun setHapticFeedback(haptic: Boolean) {
        prefs.edit().putBoolean(KEY_HAPTIC, haptic).apply()
        _hapticFeedback.value = haptic
    }

    companion object {
        private const val KEY_USER_NAME = "key_user_name"
        private const val KEY_CUSTOM_API_KEY = "key_custom_api_key"
        private const val KEY_SELECTED_MODEL = "key_selected_model"
        private const val KEY_SELECTED_PERSONA = "key_selected_persona"
        private const val KEY_VOICE_LANG = "key_voice_lang"
        private const val KEY_VOICE_PITCH = "key_voice_pitch"
        private const val KEY_VOICE_SPEED = "key_voice_speed"
        private const val KEY_AUTO_SPEAK = "key_auto_speak"
        private const val KEY_HAPTIC = "key_haptic"
    }
}
