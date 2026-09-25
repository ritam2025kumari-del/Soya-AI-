package com.example.data.model

enum class PersonalityMode(
    val id: String,
    val title: String,
    val hindiTitle: String,
    val subtitle: String,
    val iconName: String,
    val systemPromptFragment: String
) {
    OMNI(
        id = "omni",
        title = "Omni Companion",
        hindiTitle = "स्मार्ट ऑल-राउंडर",
        subtitle = "Empathetic, intelligent, witty, speaks fluent Hindi & English naturally.",
        iconName = "AutoAwesome",
        systemPromptFragment = "You are SOYA AI, an extraordinary personal companion. You are deeply intelligent, warm, witty, polite and highly capable. If the user asks in Hindi, reply in natural, conversational, friendly Hindi (Devanagari script or clean Hinglish depending on their prompt). If asked in English, reply in crisp, articulate English. You can seamlessly switch between Hindi and English."
    ),
    SHUDDH_HINDI(
        id = "hindi_guru",
        title = "Natural Hindi Voice AI",
        hindiTitle = "शुद्ध एवं बोलचाल हिंदी",
        subtitle = "Speaks beautiful conversational Hindi, respectful, culturally attuned.",
        iconName = "RecordVoiceOver",
        systemPromptFragment = "You are SOYA AI, speaking primarily in natural, warm, respectful and expressive Hindi. Use polite terms like 'नमस्ते!', 'जी बिल्कुल!', 'मैं आपकी कैसे सहायता कर सकती हूँ?'. Make your Hindi natural, modern and pleasant to listen to when spoken aloud by Android TextToSpeech. Avoid overly complex Sanskrit words unless requested; keep it conversational Hindi spoken across India."
    ),
    HINGLISH_TECH(
        id = "tech_genius",
        title = "Hinglish Tech Genius",
        hindiTitle = "हिंग्लिश टेक जीनियस",
        subtitle = "Coding, architecture, desktop & Android automation, tech troubleshooter.",
        iconName = "Code",
        systemPromptFragment = "You are SOYA AI - Tech Genius. You speak smooth, energetic Hinglish and English. You excel at programming (Kotlin, Python, JavaScript, Rust, Bash), Linux commands, desktop automation, Android system tools, APIs, debugging, and system architecture. Provide concise, high-impact technical answers with code blocks when needed."
    ),
    CYBER_SENTINEL(
        id = "cyber_security",
        title = "Cybersecurity Sentinel",
        hindiTitle = "साइबर सुरक्षा रक्षक",
        subtitle = "Security audits, privacy advisor, password strength & system safety.",
        iconName = "Security",
        systemPromptFragment = "You are SOYA AI - Cyber Sentinel. You specialize in cybersecurity, privacy preservation, threat modeling, safe coding, mobile device security, phishing defense, cryptography, and network security. You analyze security risks and give practical, proactive safety guidelines in both English and Hindi."
    ),
    AUTOMATION_COMMANDER(
        id = "automation_commander",
        title = "Automation Commander",
        hindiTitle = "ऑटोमेशन कमांडर",
        subtitle = "Routines, productivity schedules, daily briefs, workflow optimization.",
        iconName = "Bolt",
        systemPromptFragment = "You are SOYA AI - Automation Commander. You are razor-focused on productivity, daily schedules, routine execution, battery & device status analysis, unit conversions, and rapid task completion. You format responses clearly with action items and bullet points."
    )
}

enum class VoiceLanguage(
    val code: String,
    val displayName: String,
    val localeTag: String,
    val sampleText: String
) {
    HINDI_INDIA(
        code = "hi_IN",
        displayName = "Hindi (हिंदी - India)",
        localeTag = "hi-IN",
        sampleText = "नमस्ते! मैं सोया हूँ, आपकी पर्सनल AI असिस्टेंट। आज मैं आपकी क्या मदद करूँ?"
    ),
    HINGLISH(
        code = "hi_EN",
        displayName = "Hinglish (Hindi + English)",
        localeTag = "hi-IN",
        sampleText = "Hello! Main Soya hoon. Aapka personal AI companion ready hai. Kya command run karein?"
    ),
    ENGLISH_INDIA(
        code = "en_IN",
        displayName = "English (India)",
        localeTag = "en-IN",
        sampleText = "Greetings! I am SOYA, your personal AI assistant. All systems are operational."
    ),
    ENGLISH_US(
        code = "en_US",
        displayName = "English (US)",
        localeTag = "en-US",
        sampleText = "Hi there! I'm SOYA AI. Ready to assist with your tasks, code, and daily routines."
    )
}

data class DeviceTelemetry(
    val batteryPercent: Int = 100,
    val isCharging: Boolean = false,
    val networkType: String = "Wi-Fi",
    val availableMemoryMb: Long = 2048,
    val freeStorageGb: Double = 16.0,
    val currentFormattedTime: String = "",
    val currentFormattedDate: String = "",
    val deviceModel: String = ""
)

data class AutomationRoutine(
    val id: String,
    val title: String,
    val hindiTitle: String,
    val triggerPhrase: String,
    val description: String,
    val actionType: String,
    val iconKey: String
)

data class MemoryFact(
    val id: Long = 0,
    val key: String,
    val value: String,
    val category: String,
    val timestamp: Long = System.currentTimeMillis()
)
