package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.local.GroceryCatalog
import com.example.data.local.OrderRepository
import com.example.data.local.PantryRepository
import com.example.data.local.RecipeRepository
import com.example.data.model.CartItem
import com.example.data.model.GroceryProduct
import com.example.data.model.Order
import com.example.data.model.OrderStatus
import com.example.data.model.PantryCategory
import com.example.data.model.PantryItem
import com.example.data.model.Recipe
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class FreshDropViewModel(application: Application) : AndroidViewModel(application) {

    private val database = AppDatabase.getDatabase(application, viewModelScope)
    private val pantryRepository = PantryRepository(database.pantryDao())
    private val orderRepository = OrderRepository(database.orderDao(), viewModelScope)
    private val recipeRepository = RecipeRepository()

    // Grocery Catalog state
    val products = GroceryCatalog.products

    // Search and category filters
    private val _shopSearchQuery = MutableStateFlow("")
    val shopSearchQuery = _shopSearchQuery.asStateFlow()

    private val _selectedShopCategory = MutableStateFlow(PantryCategory.ALL)
    val selectedShopCategory = _selectedShopCategory.asStateFlow()

    // Pantry State
    val allPantryItems: StateFlow<List<PantryItem>> = pantryRepository.allPantryItems
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val lowStockItems: StateFlow<List<PantryItem>> = pantryRepository.lowStockItems
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _pantrySearchQuery = MutableStateFlow("")
    val pantrySearchQuery = _pantrySearchQuery.asStateFlow()

    private val _selectedPantryCategory = MutableStateFlow(PantryCategory.ALL)
    val selectedPantryCategory = _selectedPantryCategory.asStateFlow()

    // Cart State
    private val _cartItems = MutableStateFlow<List<CartItem>>(emptyList())
    val cartItems = _cartItems.asStateFlow()

    private val _appliedPromoCode = MutableStateFlow<String?>(null)
    val appliedPromoCode = _appliedPromoCode.asStateFlow()

    private val _discountAmount = MutableStateFlow(0.0)
    val discountAmount = _discountAmount.asStateFlow()

    private val _selectedTip = MutableStateFlow(3.0)
    val selectedTip = _selectedTip.asStateFlow()

    private val _deliverySpeed = MutableStateFlow("Priority Express (15-20 min)")
    val deliverySpeed = _deliverySpeed.asStateFlow()

    private val _deliveryAddress = MutableStateFlow("742 Evergreen Terrace, Apt 4B")
    val deliveryAddress = _deliveryAddress.asStateFlow()

    // Orders & Tracking State
    val activeLiveOrder: StateFlow<Order?> = orderRepository.activeLiveOrder
    val allOrders: StateFlow<List<Order>> = orderRepository.allOrders
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Recipe State
    private val _customRecipes = MutableStateFlow<List<Recipe>>(emptyList())
    val allRecipes: StateFlow<List<Recipe>> = combine(
        allPantryItems,
        _customRecipes
    ) { pantry, customList ->
        val fullList = customList + recipeRepository.defaultRecipes
        fullList.map { recipe ->
            recipeRepository.calculatePantryMatch(recipe, pantry)
        }.sortedByDescending { it.matchPercentage }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), recipeRepository.defaultRecipes)

    private val _selectedRecipe = MutableStateFlow<Recipe?>(null)
    val selectedRecipe = _selectedRecipe.asStateFlow()

    private val _isGeneratingRecipe = MutableStateFlow(false)
    val isGeneratingRecipe = _isGeneratingRecipe.asStateFlow()

    private val _userMessageToast = MutableStateFlow<String?>(null)
    val userMessageToast = _userMessageToast.asStateFlow()

    fun showToast(message: String) {
        _userMessageToast.value = message
    }

    fun clearToast() {
        _userMessageToast.value = null
    }

    // --- Shop / Cart Actions ---

    fun setShopSearchQuery(query: String) {
        _shopSearchQuery.value = query
    }

    fun setShopCategory(category: PantryCategory) {
        _selectedShopCategory.value = category
    }

    fun addToCart(product: GroceryProduct) {
        val current = _cartItems.value.toMutableList()
        val index = current.indexOfFirst { it.product.id == product.id }
        if (index >= 0) {
            current[index] = current[index].copy(quantity = current[index].quantity + 1)
        } else {
            current.add(CartItem(product, 1))
        }
        _cartItems.value = current
        showToast("Added ${product.name} to cart")
    }

    fun removeFromCart(product: GroceryProduct) {
        val current = _cartItems.value.toMutableList()
        val index = current.indexOfFirst { it.product.id == product.id }
        if (index >= 0) {
            if (current[index].quantity > 1) {
                current[index] = current[index].copy(quantity = current[index].quantity - 1)
            } else {
                current.removeAt(index)
            }
        }
        _cartItems.value = current
    }

    fun updateCartQuantity(product: GroceryProduct, quantity: Int) {
        val current = _cartItems.value.toMutableList()
        val index = current.indexOfFirst { it.product.id == product.id }
        if (quantity <= 0) {
            if (index >= 0) current.removeAt(index)
        } else {
            if (index >= 0) {
                current[index] = current[index].copy(quantity = quantity)
            } else {
                current.add(CartItem(product, quantity))
            }
        }
        _cartItems.value = current
    }

    fun clearCart() {
        _cartItems.value = emptyList()
        _appliedPromoCode.value = null
        _discountAmount.value = 0.0
    }

    fun setTip(amount: Double) {
        _selectedTip.value = amount
    }

    fun setDeliverySpeed(speed: String) {
        _deliverySpeed.value = speed
    }

    fun setDeliveryAddress(address: String) {
        _deliveryAddress.value = address
    }

    fun applyPromoCode(code: String): Boolean {
        if (code.trim().equals("FRESH50", ignoreCase = true)) {
            _appliedPromoCode.value = "FRESH50"
            _discountAmount.value = 5.00
            showToast("$5 discount applied with code FRESH50!")
            return true
        } else if (code.trim().equals("FREEDROP", ignoreCase = true)) {
            _appliedPromoCode.value = "FREEDROP"
            _discountAmount.value = 3.99
            showToast("Free Delivery unlocked!")
            return true
        }
        showToast("Invalid promo code. Try FRESH50 or FREEDROP")
        return false
    }

    fun placeOrder(onOrderCreated: (Order) -> Unit) {
        val items = _cartItems.value
        if (items.isEmpty()) return

        viewModelScope.launch {
            val order = orderRepository.createOrder(
                cartItems = items,
                deliveryAddress = _deliveryAddress.value,
                deliverySpeed = _deliverySpeed.value,
                tip = _selectedTip.value,
                discount = _discountAmount.value
            )
            clearCart()
            showToast("Order #${order.orderNumber} placed! Live tracking started 🚀")
            onOrderCreated(order)
        }
    }

    // --- Pantry Actions ---

    fun setPantrySearchQuery(query: String) {
        _pantrySearchQuery.value = query
    }

    fun setPantryCategory(category: PantryCategory) {
        _selectedPantryCategory.value = category
    }

    fun addPantryItem(
        name: String,
        category: PantryCategory,
        quantity: Double,
        unit: String,
        expiryDays: Int,
        emoji: String = "🥦",
        notes: String = ""
    ) {
        viewModelScope.launch {
            val item = PantryItem(
                name = name,
                category = category,
                quantity = quantity,
                unit = unit,
                expiryDateDaysLeft = expiryDays,
                emoji = emoji,
                notes = notes
            )
            pantryRepository.insertItem(item)
            showToast("Added $name to your pantry")
        }
    }

    fun updatePantryQuantity(item: PantryItem, delta: Double) {
        viewModelScope.launch {
            pantryRepository.adjustQuantity(item, delta)
        }
    }

    fun deletePantryItem(item: PantryItem) {
        viewModelScope.launch {
            pantryRepository.deleteItem(item)
            showToast("Removed ${item.name} from pantry")
        }
    }

    fun restockPantryItemToCart(pantryItem: PantryItem) {
        // Find matching product in catalog or create on the fly
        val matchedProduct = products.find { it.name.contains(pantryItem.name, ignoreCase = true) || pantryItem.name.contains(it.name, ignoreCase = true) }
            ?: GroceryProduct(
                id = "custom_${pantryItem.id}",
                name = pantryItem.name,
                category = pantryItem.category,
                price = 3.99,
                unit = pantryItem.unit,
                description = "Restocked from pantry low-stock alert",
                emoji = pantryItem.emoji
            )
        addToCart(matchedProduct)
    }

    // --- Order / Live Tracking Actions ---

    fun syncDeliveredGroceriesToPantry(order: Order) {
        viewModelScope.launch {
            pantryRepository.addGroceriesFromDelivery(order.items)
            orderRepository.markPantrySynced(order.id)
            showToast("🎉 Added ${order.items.size} grocery items to your Pantry!")
        }
    }

    fun simulateAdvanceLiveOrder(orderId: String) {
        viewModelScope.launch {
            orderRepository.simulateQuickAdvance(orderId)
        }
    }

    // --- Recipe Actions ---

    fun selectRecipe(recipe: Recipe?) {
        _selectedRecipe.value = recipe
    }

    fun addMissingIngredientsToCart(recipe: Recipe) {
        val missing = recipe.ingredients.filter { !it.isAvailableInPantry }
        var count = 0
        missing.forEach { ing ->
            val product = products.find { it.name.contains(ing.name, ignoreCase = true) || ing.name.contains(it.name, ignoreCase = true) }
                ?: GroceryProduct(
                    id = "ing_${ing.name.hashCode()}",
                    name = ing.name,
                    category = PantryCategory.PRODUCE,
                    price = 2.99,
                    unit = ing.amount,
                    description = "Required for recipe: ${recipe.title}",
                    emoji = "🛒"
                )
            addToCart(product)
            count++
        }
        showToast("Added $count missing ingredients to your Cart! 🛒")
    }

    fun generateAiRecipe(
        mealType: String,
        dietary: String,
        maxTimeMinutes: Int,
        customPrompt: String
    ) {
        viewModelScope.launch {
            _isGeneratingRecipe.value = true
            val pantryList = allPantryItems.value
            val recipe = recipeRepository.generateRecipeWithGemini(
                pantryItems = pantryList,
                mealType = mealType,
                dietary = dietary,
                maxTimeMinutes = maxTimeMinutes,
                customPrompt = customPrompt
            )
            _isGeneratingRecipe.value = false
            if (recipe != null) {
                _customRecipes.value = listOf(recipe) + _customRecipes.value
                _selectedRecipe.value = recipe
                showToast("✨ AI Chef crafted: ${recipe.title}")
            } else {
                showToast("Could not generate recipe. Please try again!")
            }
        }
    }
}
