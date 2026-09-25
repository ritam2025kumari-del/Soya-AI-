package com.example.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "chat_messages")
data class ChatMessageEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val sender: String, // "USER" or "SOYA"
    val content: String,
    val timestamp: Long = System.currentTimeMillis(),
    val language: String = "hi", // "hi" or "en"
    val toolCallInfo: String? = null,
    val isSpoken: Boolean = false,
    val isFavorite: Boolean = false
)

@Entity(tableName = "memory_vault")
data class MemoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val keyTag: String,
    val factValue: String,
    val category: String = "General",
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "automation_routines")
data class AutomationEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val hindiTitle: String,
    val triggerPhrase: String,
    val description: String,
    val actionType: String,
    val iconKey: String,
    val isEnabled: Boolean = true
)
