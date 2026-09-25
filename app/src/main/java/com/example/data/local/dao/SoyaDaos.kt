package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entities.AutomationEntity
import com.example.data.local.entities.ChatMessageEntity
import com.example.data.local.entities.MemoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ChatDao {
    @Query("SELECT * FROM chat_messages ORDER BY timestamp ASC")
    fun getAllMessages(): Flow<List<ChatMessageEntity>>

    @Query("SELECT * FROM chat_messages ORDER BY timestamp DESC LIMIT :limit")
    suspend fun getRecentMessages(limit: Int): List<ChatMessageEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: ChatMessageEntity): Long

    @Query("DELETE FROM chat_messages")
    suspend fun clearHistory()

    @Query("UPDATE chat_messages SET isFavorite = NOT isFavorite WHERE id = :id")
    suspend fun toggleFavorite(id: Long)

    @Delete
    suspend fun deleteMessage(message: ChatMessageEntity)
}

@Dao
interface MemoryDao {
    @Query("SELECT * FROM memory_vault ORDER BY timestamp DESC")
    fun getAllMemories(): Flow<List<MemoryEntity>>

    @Query("SELECT * FROM memory_vault ORDER BY timestamp DESC")
    suspend fun getMemoriesList(): List<MemoryEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMemory(memory: MemoryEntity): Long

    @Update
    suspend fun updateMemory(memory: MemoryEntity)

    @Delete
    suspend fun deleteMemory(memory: MemoryEntity)

    @Query("DELETE FROM memory_vault WHERE id = :id")
    suspend fun deleteMemoryById(id: Long)
}

@Dao
interface AutomationDao {
    @Query("SELECT * FROM automation_routines")
    fun getAllRoutines(): Flow<List<AutomationEntity>>

    @Query("SELECT * FROM automation_routines WHERE isEnabled = 1")
    suspend fun getActiveRoutines(): List<AutomationEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(routines: List<AutomationEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(routine: AutomationEntity)

    @Update
    suspend fun update(routine: AutomationEntity)

    @Query("UPDATE automation_routines SET isEnabled = :isEnabled WHERE id = :id")
    suspend fun setRoutineEnabled(id: String, isEnabled: Boolean)
}
