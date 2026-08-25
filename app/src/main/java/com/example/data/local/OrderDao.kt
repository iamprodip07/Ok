package com.example.data.local

import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "orders")
data class OrderEntity(
    @PrimaryKey val id: String,
    val orderNumber: String,
    val timestamp: Long,
    val statusName: String,
    val itemsJson: String,
    val subtotal: Double,
    val deliveryFee: Double,
    val tip: Double,
    val discount: Double,
    val total: Double,
    val deliveryAddress: String,
    val deliverySpeed: String,
    val driverName: String,
    val driverRating: Double,
    val driverPhone: String,
    val driverVehicle: String,
    val driverProgress: Float,
    val estimatedArrivalMinutes: Int,
    val isPantrySynced: Boolean
)

@Dao
interface OrderDao {
    @Query("SELECT * FROM orders ORDER BY timestamp DESC")
    fun getAllOrders(): Flow<List<OrderEntity>>

    @Query("SELECT * FROM orders WHERE statusName != 'DELIVERED' ORDER BY timestamp DESC LIMIT 1")
    fun getActiveOrder(): Flow<OrderEntity?>

    @Query("SELECT * FROM orders WHERE id = :id")
    suspend fun getOrderById(id: String): OrderEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrder(order: OrderEntity)

    @Update
    suspend fun updateOrder(order: OrderEntity)

    @Query("UPDATE orders SET statusName = :status, driverProgress = :progress, estimatedArrivalMinutes = :eta WHERE id = :orderId")
    suspend fun updateOrderStatus(orderId: String, status: String, progress: Float, eta: Int)

    @Query("UPDATE orders SET isPantrySynced = 1 WHERE id = :orderId")
    suspend fun markPantrySynced(orderId: String)
}
