package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.PantryCategory
import com.example.data.model.PantryItem
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "pantry_items")
data class PantryItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val categoryName: String,
    val quantity: Double,
    val unit: String,
    val expiryDaysLeft: Int,
    val minThreshold: Double,
    val isLowStock: Boolean,
    val emoji: String,
    val notes: String,
    val lastRestockedTimestamp: Long
) {
    fun toDomain(): PantryItem = PantryItem(
        id = id,
        name = name,
        category = try { PantryCategory.valueOf(categoryName) } catch (e: Exception) { PantryCategory.PRODUCE },
        quantity = quantity,
        unit = unit,
        expiryDateDaysLeft = expiryDaysLeft,
        minThreshold = minThreshold,
        isLowStock = isLowStock || (quantity <= minThreshold),
        emoji = emoji,
        notes = notes,
        lastRestockedTimestamp = lastRestockedTimestamp
    )

    companion object {
        fun fromDomain(item: PantryItem): PantryItemEntity = PantryItemEntity(
            id = item.id,
            name = item.name,
            categoryName = item.category.name,
            quantity = item.quantity,
            unit = item.unit,
            expiryDaysLeft = item.expiryDateDaysLeft,
            minThreshold = item.minThreshold,
            isLowStock = item.isLowStock || (item.quantity <= item.minThreshold),
            emoji = item.emoji,
            notes = item.notes,
            lastRestockedTimestamp = item.lastRestockedTimestamp
        )
    }
}

@Dao
interface PantryDao {
    @Query("SELECT * FROM pantry_items ORDER BY expiryDaysLeft ASC")
    fun getAllPantryItems(): Flow<List<PantryItemEntity>>

    @Query("SELECT * FROM pantry_items WHERE isLowStock = 1 OR quantity <= minThreshold")
    fun getLowStockItems(): Flow<List<PantryItemEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItem(item: PantryItemEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<PantryItemEntity>)

    @Update
    suspend fun updateItem(item: PantryItemEntity)

    @Delete
    suspend fun deleteItem(item: PantryItemEntity)

    @Query("DELETE FROM pantry_items WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("SELECT COUNT(*) FROM pantry_items")
    suspend fun getCount(): Int
}
