package com.example.data.repository

import android.content.Context
import android.util.Log
import com.example.data.local.SoyaDatabase
import com.example.data.local.entities.ChatMessageEntity
import com.example.data.local.entities.MemoryEntity
import com.example.data.model.DeviceTelemetry
import com.example.data.model.GeminiContent
import com.example.data.model.GeminiGenerationConfig
import com.example.data.model.GeminiPart
import com.example.data.model.GeminiRequest
import com.example.data.model.PersonalityMode
import com.example.data.model.VoiceLanguage
import com.example.data.remote.GeminiClient
import com.example.tools.DeviceAutomationManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Locale

class SoyaBrainRepository(
    private val context: Context,
    private val database: SoyaDatabase,
    private val deviceAutomationManager: DeviceAutomationManager
) {
    private val chatDao = database.chatDao()
    private val memoryDao = database.memoryDao()
    private val automationDao = database.automationDao()

    suspend fun processUserPrompt(
        prompt: String,
        apiKey: String,
        modelName: String,
        persona: PersonalityMode,
        voiceLanguage: VoiceLanguage,
        userName: String
    ): SoyaBrainResult = withContext(Dispatchers.IO) {
        val trimmedPrompt = prompt.trim()
        if (trimmedPrompt.isEmpty()) {
            return@withContext SoyaBrainResult(
                replyText = "कृपया कुछ बोलें या टाइप करें। मैं सुनने के लिए तैयार हूँ!",
                toolBadge = null,
                actionExecuted = null
            )
        }

        // 1. Save User Message to Database
        chatDao.insertMessage(
            ChatMessageEntity(
                sender = "USER",
                content = trimmedPrompt,
                language = if (voiceLanguage == VoiceLanguage.HINDI_INDIA) "hi" else "en"
            )
        )

        // 2. Local Intent & Tool Execution Check (Fast Action Engine)
        val localToolResult = tryExecuteLocalIntent(trimmedPrompt, userName)
        if (localToolResult != null) {
            chatDao.insertMessage(
                ChatMessageEntity(
                    sender = "SOYA",
                    content = localToolResult.replyText,
                    language = if (containsDevanagari(localToolResult.replyText)) "hi" else "en",
                    toolCallInfo = localToolResult.toolBadge
                )
            )
            return@withContext localToolResult
        }

        // 3. Prepare AI Prompt with Context, Telemetry, and Memory Vault
        val telemetry = deviceAutomationManager.getDeviceTelemetry()
        val memories = memoryDao.getMemoriesList()
        val recentMessages = chatDao.getRecentMessages(10).reversed()

        val memorySummary = if (memories.isNotEmpty()) {
            memories.take(10).joinToString("\n") { "• ${it.keyTag}: ${it.factValue}" }
        } else {
            "No custom facts saved yet."
        }

        val systemInstructionText = """
            You are SOYA AI, an extraordinary, deeply intelligent, warm, and loyal personal AI assistant for ${userName}.
            
            [PERSONA & CORE DIRECTIVE]
            ${persona.systemPromptFragment}
            
            [LANGUAGE DIRECTIVES]
            - Current Selected Language Mode: ${voiceLanguage.displayName}
            - If the user communicates in Hindi or requests Hindi, reply in authentic, natural, expressive, polite Hindi (Devanagari script). Use natural conversational phrases.
            - If the user communicates in Hinglish, reply in cool, modern, fluent Hinglish.
            - If the user communicates in English, reply in articulate, intelligent English.
            - Always maintain high emotional intelligence, clarity, and respect.
            
            [LIVE DEVICE TELEMETRY]
            - Device: ${telemetry.deviceModel}
            - Local Time: ${telemetry.currentFormattedTime}
            - Date: ${telemetry.currentFormattedDate}
            - Battery: ${telemetry.batteryPercent}% (${if (telemetry.isCharging) "Charging ⚡" else "On Battery"})
            - Network: ${telemetry.networkType}
            - Free RAM: ${telemetry.availableMemoryMb} MB
            - Free Storage: ${telemetry.freeStorageGb} GB
            
            [LONG-TERM MEMORY VAULT]
            $memorySummary
            
            [CAPABILITIES]
            - You can provide automation routines, security advice, coding solutions, calculations, daily summaries, reminders, and conversational support.
            - Keep responses structured, pleasant to listen to via voice, and engaging.
        """.trimIndent()

        // 4. Build Request Contents
        val contentList = mutableListOf<GeminiContent>()
        for (msg in recentMessages) {
            val role = if (msg.sender == "USER") "user" else "model"
            contentList.add(
                GeminiContent(
                    role = role,
                    parts = listOf(GeminiPart(text = msg.content))
                )
            )
        }
        if (contentList.isEmpty() || contentList.last().role != "user") {
            contentList.add(
                GeminiContent(
                    role = "user",
                    parts = listOf(GeminiPart(text = trimmedPrompt))
                )
            )
        }

        val request = GeminiRequest(
            contents = contentList,
            systemInstruction = GeminiContent(
                parts = listOf(GeminiPart(text = systemInstructionText))
            ),
            generationConfig = GeminiGenerationConfig(
                temperature = 0.7f,
                topP = 0.95f,
                maxOutputTokens = 2048
            )
        )

        // 5. Call Gemini API
        var aiReply: String
        var toolBadge: String? = "🧠 Gemini Core Brain"

        try {
            if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
                // Fallback offline intelligence mode with rich response
                aiReply = generateOfflineIntelligentResponse(trimmedPrompt, userName, telemetry, persona, voiceLanguage)
                toolBadge = "⚡ Soya Offline Brain (API Key Pending)"
            } else {
                val response = GeminiClient.apiService.generateContent(
                    model = modelName.ifEmpty { "gemini-3.5-flash" },
                    apiKey = apiKey,
                    request = request
                )

                if (response.isSuccessful) {
                    val candidate = response.body()?.candidates?.firstOrNull()
                    val text = candidate?.content?.parts?.firstOrNull()?.text
                    if (!text.isNullOrBlank()) {
                        aiReply = text.trim()
                        toolBadge = "✨ Gemini AI [${modelName}]"
                    } else {
                        aiReply = "नमस्ते! मैं आपके अनुरोध पर विचार कर रही हूँ। कृपया दोबारा पूछें।"
                    }
                } else {
                    val errBody = response.errorBody()?.string() ?: ""
                    Log.w("SoyaBrainRepository", "Gemini API error ($response.code()): $errBody")
                    aiReply = generateOfflineIntelligentResponse(trimmedPrompt, userName, telemetry, persona, voiceLanguage)
                    toolBadge = "⚡ Soya Fallback Engine"
                }
            }
        } catch (e: Exception) {
            Log.e("SoyaBrainRepository", "Exception calling Gemini API", e)
            aiReply = generateOfflineIntelligentResponse(trimmedPrompt, userName, telemetry, persona, voiceLanguage)
            toolBadge = "⚡ Soya Smart Engine (${e.localizedMessage ?: "Offline"})"
        }

        // 6. Check if user asked to remember something
        checkAndSaveAutoMemory(trimmedPrompt, aiReply)

        // 7. Save SOYA response to Room Database
        chatDao.insertMessage(
            ChatMessageEntity(
                sender = "SOYA",
                content = aiReply,
                language = if (containsDevanagari(aiReply)) "hi" else "en",
                toolCallInfo = toolBadge
            )
        )

        SoyaBrainResult(
            replyText = aiReply,
            toolBadge = toolBadge,
            actionExecuted = null
        )
    }

    private suspend fun tryExecuteLocalIntent(prompt: String, userName: String): SoyaBrainResult? {
        val lower = prompt.lowercase(Locale.ROOT)

        // 1. Battery / बैटरी Check
        if (lower.contains("battery") || lower.contains("बैटरी") || lower.contains("चार्ज") || lower.contains("charge")) {
            val t = deviceAutomationManager.getDeviceTelemetry()
            val text = if (containsDevanagari(prompt)) {
                "🔋 **डिवाइस बैटरी स्टेटस:**\n• बैटरी लेवल: **${t.batteryPercent}%**\n• चार्जिंग स्थिति: **${if (t.isCharging) "चार्ज हो रहा है (Charging ⚡)" else "बैटरी पर चल रहा है (Discharging)"}**\n• डिवाइस: ${t.deviceModel}"
            } else {
                "🔋 **Device Battery Status:**\n• Current Level: **${t.batteryPercent}%**\n• State: **${if (t.isCharging) "Charging ⚡" else "On Battery"}**\n• Device: ${t.deviceModel}"
            }
            return SoyaBrainResult(
                replyText = text,
                toolBadge = "⚡ Hardware Telemetry Scanner",
                actionExecuted = "BATTERY_CHECK"
            )
        }

        // 2. Flashlight / टॉर्च
        if (lower.contains("torch on") || lower.contains("flashlight on") || lower.contains("टॉर्च चालू") || lower.contains("टॉर्च ऑन")) {
            val res = deviceAutomationManager.toggleFlashlight(true)
            return SoyaBrainResult(
                replyText = res,
                toolBadge = "💡 Flashlight Hardware Control",
                actionExecuted = "FLASHLIGHT_ON"
            )
        }
        if (lower.contains("torch off") || lower.contains("flashlight off") || lower.contains("टॉर्च बंद") || lower.contains("टॉर्च ऑफ")) {
            val res = deviceAutomationManager.toggleFlashlight(false)
            return SoyaBrainResult(
                replyText = res,
                toolBadge = "💡 Flashlight Hardware Control",
                actionExecuted = "FLASHLIGHT_OFF"
            )
        }

        // 3. Security Audit / Scan
        if (lower.contains("security scan") || lower.contains("सुरक्षा") || lower.contains("security audit") || lower.contains("safety check")) {
            val audit = deviceAutomationManager.runSecurityAudit()
            val text = buildString {
                append("🛡️ **SOYA Cybersecurity Audit Report**\n")
                append("• सुरक्षा स्कोर (Security Score): **${audit.score}/100** (${audit.status})\n")
                append("• स्थिति: ${audit.hindiStatus}\n\n")
                if (audit.issues.isNotEmpty()) {
                    append("**सुधार योग्य बिंदु (Issues):**\n")
                    audit.issues.forEach { append("  ⚠️ $it\n") }
                    append("\n")
                }
                append("**सिफारिशें (Recommendations):**\n")
                audit.recommendations.forEach { append("  ✅ $it\n") }
            }
            return SoyaBrainResult(
                replyText = text,
                toolBadge = "🛡️ Cybersecurity Sentinel Audit",
                actionExecuted = "SECURITY_AUDIT"
            )
        }

        // 4. Password Generator / पासवर्ड बनाओ
        if (lower.contains("password") || lower.contains("पासवर्ड") || lower.contains("passcode")) {
            val pwd = deviceAutomationManager.generateSecurePassword(16)
            val text = "🔐 **जनरेट किया गया सुरक्षित पासवर्ड (Generated Password):**\n\n```\n$pwd\n```\n\n💡 यह 16-करैक्टर का हाई-एन्ट्रॉपी पासवर्ड है (अपरकेस, लोअरकेस, नंबर्स और सिंबल्स के साथ)।"
            return SoyaBrainResult(
                replyText = text,
                toolBadge = "🔐 Cryptographic Password Engine",
                actionExecuted = "PASSWORD_GENERATE"
            )
        }

        // 5. Morning Briefing / सुबह की शुरुआत
        if (lower.contains("morning brief") || lower.contains("good morning") || lower.contains("शुभ प्रभात") || lower.contains("नमस्ते")) {
            val t = deviceAutomationManager.getDeviceTelemetry()
            val text = "🌅 **शुभ प्रभात, $userName जी! (Good Morning!)**\n\n" +
                    "📅 **तारीख:** ${t.currentFormattedDate}\n" +
                    "⏰ **समय:** ${t.currentFormattedTime}\n" +
                    "🔋 **बैटरी:** ${t.batteryPercent}% (${if (t.isCharging) "चार्जिंग चालू है ⚡" else "अनप्लग्ड"})\n" +
                    "📶 **नेटवर्क:** ${t.networkType}\n\n" +
                    "✨ *आज का विचार:* 'हर नई सुबह एक नया अवसर है। अपने लक्ष्यों की ओर पूरे विश्वास के साथ बढ़ें!'\n\n" +
                    "आज मैं आपके किन कार्यों में सहायता करूँ?"
            return SoyaBrainResult(
                replyText = text,
                toolBadge = "🌅 Daily Morning Executive Brief",
                actionExecuted = "MORNING_BRIEF"
            )
        }

        // 6. Camera / कैमरा खोलो
        if (lower.contains("open camera") || lower.contains("कैमरा खोलो") || lower.contains("फोटो खींचो")) {
            val ok = deviceAutomationManager.launchCamera()
            val text = if (ok) "📸 कैमरा चालू किया जा रहा है..." else "कैमरा खोलने में असमर्थ।"
            return SoyaBrainResult(
                replyText = text,
                toolBadge = "📸 System Intent: Camera",
                actionExecuted = "OPEN_CAMERA"
            )
        }

        // 7. Timer / टाइमर
        if (lower.contains("timer") || lower.contains("टाइमर") || lower.contains("अलार्म")) {
            val digits = Regex("\\d+").find(lower)?.value?.toIntOrNull() ?: 10
            val res = deviceAutomationManager.setTimerOrAlarm(digits, "SOYA Focus Session")
            return SoyaBrainResult(
                replyText = res,
                toolBadge = "⏳ Clock & Timer Manager",
                actionExecuted = "SET_TIMER"
            )
        }

        return null
    }

    private suspend fun checkAndSaveAutoMemory(prompt: String, reply: String) {
        val lower = prompt.lowercase(Locale.ROOT)
        if (lower.contains("याद रखो") || lower.contains("remember that") || lower.contains("my favorite") || lower.contains("मेरा नाम") || lower.contains("i live in")) {
            val key = if (lower.contains("name") || lower.contains("नाम")) "User Name"
            else if (lower.contains("city") || lower.contains("शहर") || lower.contains("live in")) "Home City"
            else if (lower.contains("favorite") || lower.contains("पसंद")) "Preference"
            else "Personal Fact"

            memoryDao.insertMemory(
                MemoryEntity(
                    keyTag = key,
                    factValue = prompt.replace(Regex("(?i)remember that|याद रखो कि|याद रखो|remember"), "").trim(),
                    category = "User Context"
                )
            )
        }
    }

    private fun generateOfflineIntelligentResponse(
        prompt: String,
        userName: String,
        telemetry: DeviceTelemetry,
        persona: PersonalityMode,
        voiceLanguage: VoiceLanguage
    ): String {
        val lower = prompt.lowercase(Locale.ROOT)
        val isHindi = containsDevanagari(prompt) || voiceLanguage == VoiceLanguage.HINDI_INDIA

        if (lower.contains("who are you") || lower.contains("तुम कौन हो") || lower.contains("तुम्हारा नाम क्या है") || lower.contains("who made you")) {
            return if (isHindi) {
                "नमस्ते $userName जी! मैं **SOYA AI** हूँ — आपकी बुद्धिमान पर्सनल AI असिस्टेंट। मैं हिंदी और अंग्रेज़ी दोनों में संवाद करने, डिवाइस टेलीमेट्री जाँचने, सुरक्षा ऑडिट करने और आपके दैनिक कार्यों को स्वचालित करने में सक्षम हूँ!"
            } else {
                "Hello $userName! I am **SOYA AI**, your intelligent personal assistant. I feature full multi-lingual voice communication in Hindi and English, device telemetry, automation routines, and cybersecurity analysis!"
            }
        }

        if (lower.contains("help") || lower.contains("मदद") || lower.contains("क्या कर सकते हो") || lower.contains("features")) {
            return if (isHindi) {
                "🌟 **SOYA AI की प्रमुख क्षमताएँ:**\n\n" +
                "1. 🎙️ **प्राकृतिक हिंदी व अंग्रेज़ी आवाज़:** सहज संवाद और वॉइस रिकॉग्निशन।\n" +
                "2. 🔋 **डिवाइस ऑटोमेशन:** बैटरी, रैम, टॉर्च, कैमरा और टाइमर कंट्रोल।\n" +
                "3. 🛡️ **साइबर सिक्योरिटी शील्ड:** सिस्टम सुरक्षा ऑडिट और पासवर्ड जनरेटर।\n" +
                "4. 🧠 **मेमोरी वॉल्ट:** आपके व्यक्तिगत संदर्भ और प्राथमिकताओं को सहेजना।\n" +
                "5. ⚙️ **दैनिक रूटीन:** सुबह का ब्रीफिंग और प्रोडक्टिविटी टूल्स।"
            } else {
                "🌟 **SOYA AI Capabilities:**\n\n" +
                "1. 🎙️ **Natural Hindi & English Voice:** Seamless speech and audio synthesis.\n" +
                "2. 🔋 **Device Telemetry & Automation:** Battery, RAM, torch, timer & camera actions.\n" +
                "3. 🛡️ **Cybersecurity Sentinel:** Device safety audit & cryptographic password generator.\n" +
                "4. 🧠 **Memory Vault:** Long-term user preferences & context persistence.\n" +
                "5. ⚙️ **Automation Hub:** Morning briefing, night protocol & focus timers."
            }
        }

        return if (isHindi) {
            "जी $userName! मैंने आपका संदेश प्राप्त कर लिया है: '$prompt'।\n\nSOYA AI का Core Brain पूरी तरह सक्रिय है। क्या आप कोई विशिष्ट डिवाइस कमांड, सुरक्षा ऑडिट, या दैनिक रूटीन चलाना चाहते हैं?"
        } else {
            "Understood, $userName! I received: '$prompt'.\n\nAll SOYA AI subsystems are operational. Would you like to run a system telemetry check, security audit, or execute an automation routine?"
        }
    }

    private fun containsDevanagari(text: String): Boolean {
        for (char in text) {
            val code = char.code
            if (code in 0x0900..0x097F) return true
        }
        return false
    }
}

data class SoyaBrainResult(
    val replyText: String,
    val toolBadge: String?,
    val actionExecuted: String?
)
