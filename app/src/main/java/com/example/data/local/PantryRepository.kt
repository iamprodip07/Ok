package com.example.data.local

import com.example.data.model.CartItem
import com.example.data.model.PantryCategory
import com.example.data.model.PantryItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PantryRepository(private val pantryDao: PantryDao) {

    val allPantryItems: Flow<List<PantryItem>> = pantryDao.getAllPantryItems().map { list ->
        list.map { it.toDomain() }
    }

    val lowStockItems: Flow<List<PantryItem>> = pantryDao.getLowStockItems().map { list ->
        list.map { it.toDomain() }
    }

    suspend fun insertItem(item: PantryItem): Long {
        return pantryDao.insertItem(PantryItemEntity.fromDomain(item))
    }

    suspend fun updateItem(item: PantryItem) {
        pantryDao.updateItem(PantryItemEntity.fromDomain(item))
    }

    suspend fun deleteItem(item: PantryItem) {
        pantryDao.deleteById(item.id)
    }

    suspend fun deleteById(id: Long) {
        pantryDao.deleteById(id)
    }

    suspend fun adjustQuantity(item: PantryItem, delta: Double) {
        val newQuantity = (item.quantity + delta).coerceAtLeast(0.0)
        val updated = item.copy(
            quantity = newQuantity,
            isLowStock = newQuantity <= item.minThreshold
        )
        updateItem(updated)
    }

    suspend fun addGroceriesFromDelivery(items: List<CartItem>) {
        items.forEach { cartItem ->
            val product = cartItem.product
            val addedQuantity = cartItem.quantity.toDouble()
            val expiryDays = when (product.category) {
                PantryCategory.PRODUCE -> 5
                PantryCategory.DAIRY_EGGS -> 10
                PantryCategory.MEAT_SEAFOOD -> 3
                PantryCategory.BAKERY -> 4
                PantryCategory.BEVERAGES -> 14
                PantryCategory.PANTRY -> 120
                PantryCategory.SPICES_SAUCES -> 180
                else -> 7
            }

            val pantryItem = PantryItem(
                name = product.name,
                category = product.category,
                quantity = addedQuantity,
                unit = product.unit,
                expiryDateDaysLeft = expiryDays,
                minThreshold = 1.0,
                isLowStock = false,
                emoji = product.emoji,
                notes = "Delivered via FreshDrop order",
                lastRestockedTimestamp = System.currentTimeMillis()
            )
            pantryDao.insertItem(PantryItemEntity.fromDomain(pantryItem))
        }
    }
}
