package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.local.dao.AutomationDao
import com.example.data.local.dao.ChatDao
import com.example.data.local.dao.MemoryDao
import com.example.data.local.entities.AutomationEntity
import com.example.data.local.entities.ChatMessageEntity
import com.example.data.local.entities.MemoryEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        ChatMessageEntity::class,
        MemoryEntity::class,
        AutomationEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class SoyaDatabase : RoomDatabase() {
    abstract fun chatDao(): ChatDao
    abstract fun memoryDao(): MemoryDao
    abstract fun automationDao(): AutomationDao

    companion object {
        @Volatile
        private var INSTANCE: SoyaDatabase? = null

        fun getDatabase(context: Context): SoyaDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SoyaDatabase::class.java,
                    "soya_assistant_database"
                )
                .addCallback(DatabaseCallback())
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback : Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    CoroutineScope(Dispatchers.IO).launch {
                        populateInitialData(database)
                    }
                }
            }

            suspend fun populateInitialData(database: SoyaDatabase) {
                // Initial memories for personal context
                val initialMemories = listOf(
                    MemoryEntity(
                        keyTag = "Assistant Identity",
                        factValue = "SOYA AI - Personal Intelligence Companion with Hindi & English Voice",
                        category = "Identity"
                    ),
                    MemoryEntity(
                        keyTag = "Preferred Greeting",
                        factValue = "नमस्ते! मैं आपकी पर्सनल AI असिस्टेंट सोया हूँ।",
                        category = "Preferences"
                    ),
                    MemoryEntity(
                        keyTag = "Primary Mode",
                        factValue = "Multi-modal Voice & Automation Assistant",
                        category = "Capabilities"
                    )
                )
                for (mem in initialMemories) {
                    database.memoryDao().insertMemory(mem)
                }

                // Initial automation routines
                val initialRoutines = listOf(
                    AutomationEntity(
                        id = "morning_brief",
                        title = "Morning Briefing",
                        hindiTitle = "सुबह का दैनिक ब्रीफिंग",
                        triggerPhrase = "सुबह की शुरुआत करो / Good morning",
                        description = "Checks time, date, battery level, device health, and shares daily motivation in Hindi.",
                        actionType = "TELEMETRY_BRIEF",
                        iconKey = "WbSunny"
                    ),
                    AutomationEntity(
                        id = "security_audit",
                        title = "Cybersecurity Shield",
                        hindiTitle = "सिस्टम सुरक्षा ऑडिट",
                        triggerPhrase = "Security check karo / Run security scan",
                        description = "Scans device security metrics, password strength analyzer, and system recommendations.",
                        actionType = "SECURITY_SCAN",
                        iconKey = "Shield"
                    ),
                    AutomationEntity(
                        id = "night_protocol",
                        title = "Night Rest Protocol",
                        hindiTitle = "रात्रि शांति प्रोटोकॉल",
                        triggerPhrase = "Good night Soya / सोने की तैयारी",
                        description = "Prepares summary of completed tasks, battery check, and a relaxing calming wish.",
                        actionType = "NIGHT_SUMMARY",
                        iconKey = "NightsStay"
                    ),
                    AutomationEntity(
                        id = "focus_boost",
                        title = "25m Pomodoro Focus",
                        hindiTitle = "25 मिनट फोकस टाइमर",
                        triggerPhrase = "Focus timer start karo",
                        description = "Sets timer reminder and shares productivity mantra.",
                        actionType = "TIMER_FOCUS",
                        iconKey = "Timer"
                    )
                )
                database.automationDao().insertAll(initialRoutines)

                // Initial welcome message
                database.chatDao().insertMessage(
                    ChatMessageEntity(
                        sender = "SOYA",
                        content = "नमस्ते! मैं आपकी पर्सनल AI असिस्टेंट **SOYA** हूँ। 🌟\n\nआप मुझसे हिंदी (Hindi) या English में बात कर सकते हैं। नीचे दिए गए वॉइस माइक को दबाएँ या कोई भी कमांड चुनें!",
                        language = "hi",
                        toolCallInfo = "⚡ Voice Engine & Core Brain Online"
                    )
                )
            }
        }
    }
}
