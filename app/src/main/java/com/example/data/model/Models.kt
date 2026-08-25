package com.example.data.model

data class PantryItem(
    val id: Long = 0,
    val name: String,
    val category: PantryCategory,
    val quantity: Double,
    val unit: String,
    val expiryDateDaysLeft: Int,
    val minThreshold: Double = 1.0,
    val isLowStock: Boolean = false,
    val emoji: String = "🥦",
    val notes: String = "",
    val lastRestockedTimestamp: Long = System.currentTimeMillis()
)

enum class PantryCategory(val displayName: String, val emoji: String) {
    ALL("All", "🧺"),
    PRODUCE("Produce", "🥬"),
    DAIRY_EGGS("Dairy & Eggs", "🥛"),
    MEAT_SEAFOOD("Meat & Seafood", "🥩"),
    PANTRY("Pantry Staples", "🥫"),
    BAKERY("Bakery", "🍞"),
    BEVERAGES("Beverages", "🧃"),
    SNACKS("Snacks", "🥨"),
    SPICES_SAUCES("Spices & Sauces", "🧂")
}

data class GroceryProduct(
    val id: String,
    val name: String,
    val category: PantryCategory,
    val price: Double,
    val originalPrice: Double? = null,
    val unit: String,
    val description: String,
    val emoji: String,
    val tags: List<String> = emptyList(),
    val rating: Double = 4.8,
    val ratingCount: Int = 120,
    val calories: Int = 150,
    val inStock: Boolean = true,
    val origin: String = "Local Organic Farm"
)

data class CartItem(
    val product: GroceryProduct,
    val quantity: Int
) {
    val totalPrice: Double get() = product.price * quantity
}

enum class OrderStatus(val title: String, val description: String, val stepIndex: Int) {
    PLACED("Order Placed", "Store received your order", 0),
    PACKING("Shopper Packing", "Fresh items being carefully picked", 1),
    ON_THE_WAY("On the Way", "Driver is speeding to your address", 2),
    ARRIVING("Arriving Soon", "Driver is on your street", 3),
    DELIVERED("Delivered", "Enjoy your fresh groceries!", 4)
}

data class DeliveryDriver(
    val name: String = "Marcus Rivera",
    val rating: Double = 4.96,
    val totalDeliveries: Int = 1420,
    val vehicle: String = "Honda EV Scooter (Green)",
    val phone: String = "+1 (555) 382-9921",
    val currentSpeedMph: Int = 24,
    val avatarEmoji: String = "🛵"
)

data class Order(
    val id: String,
    val orderNumber: String,
    val timestamp: Long = System.currentTimeMillis(),
    val status: OrderStatus,
    val items: List<CartItem>,
    val subtotal: Double,
    val deliveryFee: Double,
    val tip: Double,
    val discount: Double,
    val total: Double,
    val deliveryAddress: String,
    val deliverySpeed: String = "Priority Express (15-20 min)",
    val driver: DeliveryDriver = DeliveryDriver(),
    val driverProgress: Float = 0.45f, // 0.0 to 1.0 along route
    val estimatedArrivalMinutes: Int = 12,
    val isPantrySynced: Boolean = false
)

data class RecipeIngredient(
    val name: String,
    val amount: String,
    val isOptional: Boolean = false,
    val isAvailableInPantry: Boolean = false,
    val matchingPantryItemName: String? = null
)

data class Recipe(
    val id: String,
    val title: String,
    val description: String,
    val prepTimeMinutes: Int,
    val cookTimeMinutes: Int,
    val servings: Int,
    val difficulty: String, // "Easy", "Medium", "Chef"
    val calories: Int,
    val cuisine: String,
    val emoji: String,
    val ingredients: List<RecipeIngredient>,
    val instructions: List<String>,
    val chefTips: List<String>,
    val tags: List<String>,
    val isAiGenerated: Boolean = false,
    val matchPercentage: Int = 0 // Calculated dynamically based on inventory
)
