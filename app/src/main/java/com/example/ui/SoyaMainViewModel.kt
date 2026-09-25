package com.example.ui

import android.app.Application
import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.PreferencesManager
import com.example.data.local.SoyaDatabase
import com.example.data.local.entities.AutomationEntity
import com.example.data.local.entities.ChatMessageEntity
import com.example.data.local.entities.MemoryEntity
import com.example.data.model.DeviceTelemetry
import com.example.data.model.PersonalityMode
import com.example.data.model.VoiceLanguage
import com.example.data.repository.SoyaBrainRepository
import com.example.tools.DeviceAutomationManager
import com.example.voice.SoyaSpeechRecognizer
import com.example.voice.SoyaVoiceEngine
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SoyaMainViewModel(application: Application) : AndroidViewModel(application) {

    private val context: Context = application.applicationContext
    val preferencesManager = PreferencesManager(context)
    val database = SoyaDatabase.getDatabase(context)
    val deviceAutomationManager = DeviceAutomationManager(context)
    val brainRepository = SoyaBrainRepository(context, database, deviceAutomationManager)

    // Voice Engine
    val voiceEngine = SoyaVoiceEngine(context, viewModelScope)

    // Speech Recognizer
    private var speechRecognizer: SoyaSpeechRecognizer? = null

    // Room DB Flows
    val chatMessages: StateFlow<List<ChatMessageEntity>> = database.chatDao().getAllMessages()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val memoryVault: StateFlow<List<MemoryEntity>> = database.memoryDao().getAllMemories()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val automationRoutines: StateFlow<List<AutomationEntity>> = database.automationDao().getAllRoutines()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Telemetry State
    private val _telemetry = MutableStateFlow(deviceAutomationManager.getDeviceTelemetry())
    val telemetry: StateFlow<DeviceTelemetry> = _telemetry.asStateFlow()

    // UI Processing States
    private val _isProcessing = MutableStateFlow(false)
    val isProcessing: StateFlow<Boolean> = _isProcessing.asStateFlow()

    private val _isListening = MutableStateFlow(false)
    val isListening: StateFlow<Boolean> = _isListening.asStateFlow()

    private val _audioAmplitude = MutableStateFlow(0f)
    val audioAmplitude: StateFlow<Float> = _audioAmplitude.asStateFlow()

    private val _inputQuery = MutableStateFlow("")
    val inputQuery: StateFlow<String> = _inputQuery.asStateFlow()

    private val _toastMessage = MutableStateFlow<String?>(null)
    val toastMessage: StateFlow<String?> = _toastMessage.asStateFlow()

    init {
        // Init speech recognizer
        speechRecognizer = SoyaSpeechRecognizer(
            context = context,
            onResult = { recognizedText ->
                _isListening.value = false
                _audioAmplitude.value = 0f
                if (recognizedText.isNotBlank()) {
                    triggerHaptic()
                    sendMessage(recognizedText)
                }
            },
            onPartialResult = { partial ->
                _inputQuery.value = partial
            }
        )

        // Observe Voice Engine speaking state
        viewModelScope.launch {
            voiceEngine.isSpeaking.collect { speaking ->
                if (speaking) {
                    _audioAmplitude.value = voiceEngine.speakingAmplitude.value
                } else if (!_isListening.value) {
                    _audioAmplitude.value = 0f
                }
            }
        }

        // Start periodic telemetry refresh
        viewModelScope.launch(Dispatchers.Default) {
            while (true) {
                _telemetry.value = deviceAutomationManager.getDeviceTelemetry()
                delay(10000)
            }
        }
    }

    fun onQueryChanged(newQuery: String) {
        _inputQuery.value = newQuery
    }

    fun toggleVoiceListening() {
        if (_isListening.value) {
            stopListening()
        } else {
            startListening()
        }
    }

    fun startListening() {
        voiceEngine.stop()
        triggerHaptic()
        _isListening.value = true
        val lang = preferencesManager.voiceLanguage.value
        speechRecognizer?.startListening(lang)

        // Monitor amplitude
        viewModelScope.launch {
            speechRecognizer?.rmsAmplitude?.collect { amp ->
                if (_isListening.value) {
                    _audioAmplitude.value = amp
                }
            }
        }
    }

    fun stopListening() {
        speechRecognizer?.stopListening()
        _isListening.value = false
        _audioAmplitude.value = 0f
    }

    fun sendMessage(text: String = _inputQuery.value) {
        val trimmed = text.trim()
        if (trimmed.isEmpty() || _isProcessing.value) return

        _inputQuery.value = ""
        _isProcessing.value = true

        viewModelScope.launch {
            try {
                val apiKey = preferencesManager.getEffectiveApiKey()
                val model = preferencesManager.selectedModel.value
                val persona = preferencesManager.selectedPersona.value
                val lang = preferencesManager.voiceLanguage.value
                val userName = preferencesManager.userName.value

                val result = brainRepository.processUserPrompt(
                    prompt = trimmed,
                    apiKey = apiKey,
                    modelName = model,
                    persona = persona,
                    voiceLanguage = lang,
                    userName = userName
                )

                // If auto speak is enabled, synthesize Hindi/English speech
                if (preferencesManager.autoSpeak.value) {
                    voiceEngine.speak(result.replyText, lang)
                }

                _telemetry.value = deviceAutomationManager.getDeviceTelemetry()
            } catch (e: Exception) {
                _toastMessage.value = "Error: ${e.message}"
            } finally {
                _isProcessing.value = false
            }
        }
    }

    fun speakMessage(text: String) {
        triggerHaptic()
        voiceEngine.speak(text, preferencesManager.voiceLanguage.value)
    }

    fun stopSpeaking() {
        voiceEngine.stop()
    }

    fun runRoutine(routine: AutomationEntity) {
        triggerHaptic()
        sendMessage(routine.triggerPhrase)
    }

    fun executeFlashlight(turnOn: Boolean) {
        val msg = deviceAutomationManager.toggleFlashlight(turnOn)
        _toastMessage.value = msg
        if (preferencesManager.autoSpeak.value) {
            voiceEngine.speak(msg)
        }
    }

    fun executeQuickTimer(minutes: Int) {
        val msg = deviceAutomationManager.setTimerOrAlarm(minutes, "SOYA Quick Session")
        _toastMessage.value = msg
    }

    fun executePasswordGenerate() {
        sendMessage("एक मजबूत 16-अक्षर का पासवर्ड जनरेट करो")
    }

    fun executeSecurityAudit() {
        sendMessage("डिवाइस का Security Audit Scan चलाओ")
    }

    fun clearChatHistory() {
        viewModelScope.launch(Dispatchers.IO) {
            database.chatDao().clearHistory()
            _toastMessage.value = "Chat history cleared"
        }
    }

    fun toggleFavorite(id: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            database.chatDao().toggleFavorite(id)
        }
    }

    fun saveMemory(key: String, value: String, category: String) {
        if (key.isBlank() || value.isBlank()) return
        viewModelScope.launch(Dispatchers.IO) {
            database.memoryDao().insertMemory(
                MemoryEntity(
                    keyTag = key.trim(),
                    factValue = value.trim(),
                    category = category.trim().ifEmpty { "General" }
                )
            )
            _toastMessage.value = "Memory saved to vault"
        }
    }

    fun deleteMemory(id: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            database.memoryDao().deleteMemoryById(id)
            _toastMessage.value = "Memory deleted"
        }
    }

    fun setVoiceConfig(lang: VoiceLanguage, pitch: Float, speed: Float) {
        preferencesManager.setVoiceLanguage(lang)
        preferencesManager.setVoicePitch(pitch)
        preferencesManager.setVoiceSpeed(speed)
        voiceEngine.setVoiceConfig(lang, pitch, speed)
    }

    fun clearToast() {
        _toastMessage.value = null
    }

    private fun triggerHaptic() {
        if (!preferencesManager.hapticFeedback.value) return
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                val vibrator = vibratorManager?.defaultVibrator
                vibrator?.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK))
            } else {
                @Suppress("DEPRECATION")
                val v = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
                v?.vibrate(30)
            }
        } catch (e: Exception) {
            // ignore
        }
    }

    override fun onCleared() {
        super.onCleared()
        speechRecognizer?.destroy()
        voiceEngine.release()
    }
}
