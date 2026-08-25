package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [PantryItemEntity::class, OrderEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun pantryDao(): PantryDao
    abstract fun orderDao(): OrderDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "freshdrop_database"
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(object : Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            // Seed default pantry items
                            INSTANCE?.let { database ->
                                scope.launch(Dispatchers.IO) {
                                    populateInitialPantry(database.pantryDao())
                                }
                            }
                        }
                    })
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private suspend fun populateInitialPantry(dao: PantryDao) {
            val initialItems = listOf(
                PantryItemEntity(
                    name = "Organic Hass Avocados",
                    categoryName = "PRODUCE",
                    quantity = 2.0,
                    unit = "pcs",
                    expiryDaysLeft = 3,
                    minThreshold = 1.0,
                    isLowStock = false,
                    emoji = "🥑",
                    notes = "Ripe, use soon for guacamole or toast",
                    lastRestockedTimestamp = System.currentTimeMillis() - 86400000L * 2
                ),
                PantryItemEntity(
                    name = "Whole Milk (Organic 2%)",
                    categoryName = "DAIRY_EGGS",
                    quantity = 0.5,
                    unit = "gallon",
                    expiryDaysLeft = 4,
                    minThreshold = 1.0,
                    isLowStock = true,
                    emoji = "🥛",
                    notes = "Running low - add to cart",
                    lastRestockedTimestamp = System.currentTimeMillis() - 86400000L * 4
                ),
                PantryItemEntity(
                    name = "Free-Range Large Eggs",
                    categoryName = "DAIRY_EGGS",
                    quantity = 6.0,
                    unit = "eggs",
                    expiryDaysLeft = 8,
                    minThreshold = 4.0,
                    isLowStock = false,
                    emoji = "🥚",
                    notes = "Grade A pasture raised",
                    lastRestockedTimestamp = System.currentTimeMillis() - 86400000L * 3
                ),
                PantryItemEntity(
                    name = "San Marzano Cherry Tomatoes",
                    categoryName = "PRODUCE",
                    quantity = 1.0,
                    unit = "box (10oz)",
                    expiryDaysLeft = 5,
                    minThreshold = 1.0,
                    isLowStock = false,
                    emoji = "🍅",
                    notes = "Sweet and juicy",
                    lastRestockedTimestamp = System.currentTimeMillis() - 86400000L * 1
                ),
                PantryItemEntity(
                    name = "Artisan Sourdough Loaf",
                    categoryName = "BAKERY",
                    quantity = 0.3,
                    unit = "loaf",
                    expiryDaysLeft = 2,
                    minThreshold = 0.5,
                    isLowStock = true,
                    emoji = "🍞",
                    notes = "Almost finished",
                    lastRestockedTimestamp = System.currentTimeMillis() - 86400000L * 3
                ),
                PantryItemEntity(
                    name = "Italian Bronze-Cut Rigatoni",
                    categoryName = "PANTRY",
                    quantity = 2.0,
                    unit = "boxes (16oz)",
                    expiryDaysLeft = 90,
                    minThreshold = 1.0,
                    isLowStock = false,
                    emoji = "🍝",
                    notes = "Semolina durum wheat",
                    lastRestockedTimestamp = System.currentTimeMillis() - 86400000L * 10
                ),
                PantryItemEntity(
                    name = "Extra Virgin Olive Oil",
                    categoryName = "SPICES_SAUCES",
                    quantity = 0.7,
                    unit = "bottle (750ml)",
                    expiryDaysLeft = 120,
                    minThreshold = 0.2,
                    isLowStock = false,
                    emoji = "🫒",
                    notes = "Cold pressed Sicilian",
                    lastRestockedTimestamp = System.currentTimeMillis() - 86400000L * 15
                ),
                PantryItemEntity(
                    name = "Fresh Sweet Basil",
                    categoryName = "PRODUCE",
                    quantity = 1.0,
                    unit = "bunch",
                    expiryDaysLeft = 2,
                    minThreshold = 1.0,
                    isLowStock = false,
                    emoji = "🌿",
                    notes = "Aromatic organic herb",
                    lastRestockedTimestamp = System.currentTimeMillis() - 86400000L * 2
                ),
                PantryItemEntity(
                    name = "Aged Parmesan Cheese (Parmigiano)",
                    categoryName = "DAIRY_EGGS",
                    quantity = 1.0,
                    unit = "wedge (8oz)",
                    expiryDaysLeft = 25,
                    minThreshold = 0.5,
                    isLowStock = false,
                    emoji = "🧀",
                    notes = "24-month aged",
                    lastRestockedTimestamp = System.currentTimeMillis() - 86400000L * 5
                ),
                PantryItemEntity(
                    name = "Garlic Bulbs",
                    categoryName = "PRODUCE",
                    quantity = 3.0,
                    unit = "heads",
                    expiryDaysLeft = 21,
                    minThreshold = 1.0,
                    isLowStock = false,
                    emoji = "🧄",
                    notes = "California heirloom",
                    lastRestockedTimestamp = System.currentTimeMillis() - 86400000L * 7
                )
            )
            dao.insertAll(initialItems)
        }
    }
}
