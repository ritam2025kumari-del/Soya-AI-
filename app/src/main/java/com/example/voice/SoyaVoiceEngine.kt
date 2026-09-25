package com.example.voice

import android.content.Context
import android.media.AudioAttributes
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import com.example.data.model.VoiceLanguage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.Locale
import java.util.regex.Pattern

class SoyaVoiceEngine(
    private val context: Context,
    private val coroutineScope: CoroutineScope
) : TextToSpeech.OnInitListener {

    private var tts: TextToSpeech? = null
    private var isInitialized = false

    private val _isSpeaking = MutableStateFlow(false)
    val isSpeaking: StateFlow<Boolean> = _isSpeaking.asStateFlow()

    private val _speakingAmplitude = MutableStateFlow(0f)
    val speakingAmplitude: StateFlow<Float> = _speakingAmplitude.asStateFlow()

    private val _isHindiSupported = MutableStateFlow(true)
    val isHindiSupported: StateFlow<Boolean> = _isHindiSupported.asStateFlow()

    private var currentLanguage = VoiceLanguage.HINDI_INDIA
    private var currentPitch = 1.05f
    private var currentSpeed = 0.95f
    private var waveSimulationJob: Job? = null

    init {
        tts = TextToSpeech(context.applicationContext, this)
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            tts?.let { engine ->
                engine.setAudioAttributes(
                    AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                        .setUsage(AudioAttributes.USAGE_ASSISTANT)
                        .build()
                )

                // Check Hindi support
                val hindiLocale = Locale("hi", "IN")
                val hindiResult = engine.isLanguageAvailable(hindiLocale)
                _isHindiSupported.value = (hindiResult >= TextToSpeech.LANG_AVAILABLE)

                engine.language = hindiLocale
                engine.setPitch(currentPitch)
                engine.setSpeechRate(currentSpeed)

                engine.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                    override fun onStart(utteranceId: String?) {
                        _isSpeaking.value = true
                        startWaveSimulation()
                    }

                    override fun onDone(utteranceId: String?) {
                        _isSpeaking.value = false
                        stopWaveSimulation()
                    }

                    override fun onError(utteranceId: String?) {
                        _isSpeaking.value = false
                        stopWaveSimulation()
                    }

                    override fun onError(utteranceId: String?, errorCode: Int) {
                        _isSpeaking.value = false
                        stopWaveSimulation()
                        Log.w("SoyaVoiceEngine", "TTS error code: $errorCode")
                    }
                })

                isInitialized = true
            }
        } else {
            Log.e("SoyaVoiceEngine", "Failed to initialize TextToSpeech ($status)")
        }
    }

    fun setVoiceConfig(language: VoiceLanguage, pitch: Float, speed: Float) {
        currentLanguage = language
        currentPitch = pitch
        currentSpeed = speed

        tts?.let { engine ->
            engine.setPitch(pitch)
            engine.setSpeechRate(speed)
            applyLocale(language.localeTag)
        }
    }

    private fun applyLocale(localeTag: String) {
        tts?.let { engine ->
            val parts = localeTag.split("-")
            val targetLocale = if (parts.size >= 2) Locale(parts[0], parts[1]) else Locale(parts[0])
            val result = engine.isLanguageAvailable(targetLocale)
            if (result >= TextToSpeech.LANG_AVAILABLE) {
                engine.language = targetLocale
            } else {
                // Fallback to default
                engine.language = Locale.getDefault()
            }
        }
    }

    fun speak(text: String, overrideLang: VoiceLanguage? = null) {
        if (!isInitialized || tts == null) return

        // Clean markdown symbols (**, __, ##, etc.) for smoother natural speech
        val cleanSpeech = cleanMarkdownForSpeech(text)
        if (cleanSpeech.isBlank()) return

        tts?.let { engine ->
            // Auto detect Hindi script to use authentic Hindi TTS phonetics
            val hasDevanagari = containsDevanagari(cleanSpeech)
            val langToUse = overrideLang ?: if (hasDevanagari) VoiceLanguage.HINDI_INDIA else currentLanguage

            applyLocale(langToUse.localeTag)
            engine.setPitch(currentPitch)
            engine.setSpeechRate(currentSpeed)

            val params = Bundle().apply {
                putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "SOYA_${System.currentTimeMillis()}")
            }

            engine.speak(cleanSpeech, TextToSpeech.QUEUE_FLUSH, params, "SOYA_${System.currentTimeMillis()}")
        }
    }

    fun stop() {
        tts?.stop()
        _isSpeaking.value = false
        stopWaveSimulation()
    }

    private fun startWaveSimulation() {
        waveSimulationJob?.cancel()
        waveSimulationJob = coroutineScope.launch(Dispatchers.Default) {
            var phase = 0.0
            while (isActive && _isSpeaking.value) {
                phase += 0.3
                val amp = (kotlin.math.sin(phase).toFloat() * 0.4f + 0.6f) * (0.5f + (Math.random() * 0.5f).toFloat())
                _speakingAmplitude.value = amp.coerceIn(0.1f, 1f)
                delay(60)
            }
            _speakingAmplitude.value = 0f
        }
    }

    private fun stopWaveSimulation() {
        waveSimulationJob?.cancel()
        waveSimulationJob = null
        _speakingAmplitude.value = 0f
    }

    private fun containsDevanagari(text: String): Boolean {
        for (char in text) {
            val code = char.code
            if (code in 0x0900..0x097F) return true
        }
        return false
    }

    private fun cleanMarkdownForSpeech(raw: String): String {
        return raw
            .replace(Pattern.compile("```[\\s\\S]*?```").toRegex(), " Code block omitted. ")
            .replace(Pattern.compile("`.*?`").toRegex(), " ")
            .replace(Pattern.compile("\\[.*?\\]\\(.*?\\)").toRegex(), " link ")
            .replace(Regex("[*#_~>•\\-\\[\\](){}]"), " ")
            .replace(Regex("\\s+"), " ")
            .trim()
    }

    fun release() {
        stop()
        tts?.shutdown()
        tts = null
    }
}
