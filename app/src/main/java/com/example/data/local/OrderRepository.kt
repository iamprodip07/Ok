package com.example.data.local

import com.example.data.model.CartItem
import com.example.data.model.DeliveryDriver
import com.example.data.model.GroceryProduct
import com.example.data.model.Order
import com.example.data.model.OrderStatus
import com.example.data.model.PantryCategory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import java.util.UUID

class OrderRepository(
    private val orderDao: OrderDao,
    private val externalScope: CoroutineScope
) {
    private val _activeLiveOrder = MutableStateFlow<Order?>(null)
    val activeLiveOrder = _activeLiveOrder.asStateFlow()

    private var simulationJob: Job? = null

    val allOrders: Flow<List<Order>> = orderDao.getAllOrders().map { entities ->
        entities.map { entityToOrder(it) }
    }

    init {
        externalScope.launch {
            orderDao.getActiveOrder().collect { activeEntity ->
                if (activeEntity != null) {
                    val order = entityToOrder(activeEntity)
                    _activeLiveOrder.value = order
                    if (order.status != OrderStatus.DELIVERED && (simulationJob == null || !simulationJob!!.isActive)) {
                        startLiveTrackingSimulation(order.id)
                    }
                } else if (_activeLiveOrder.value == null) {
                    // Seed a demo active order if desired so user can test tracking immediately or start fresh
                }
            }
        }
    }

    suspend fun createOrder(
        cartItems: List<CartItem>,
        deliveryAddress: String,
        deliverySpeed: String,
        tip: Double,
        discount: Double
    ): Order {
        val subtotal = cartItems.sumOf { it.totalPrice }
        val deliveryFee = if (subtotal >= 35.0) 0.0 else 3.99
        val total = (subtotal + deliveryFee + tip - discount).coerceAtLeast(0.0)

        val orderId = UUID.randomUUID().toString()
        val orderNumber = "FD-${(10000..99999).random()}"

        val order = Order(
            id = orderId,
            orderNumber = orderNumber,
            timestamp = System.currentTimeMillis(),
            status = OrderStatus.PLACED,
            items = cartItems,
            subtotal = subtotal,
            deliveryFee = deliveryFee,
            tip = tip,
            discount = discount,
            total = total,
            deliveryAddress = deliveryAddress,
            deliverySpeed = deliverySpeed,
            driver = DeliveryDriver(
                name = listOf("Marcus Rivera", "Sophia Chen", "Alex Johnson", "Elena Rostova").random(),
                rating = 4.95,
                vehicle = "Honda Electric Cargo Scooter (Green)",
                phone = "+1 (555) 742-8819",
                currentSpeedMph = 22,
                avatarEmoji = "🛵"
            ),
            driverProgress = 0.05f,
            estimatedArrivalMinutes = if (deliverySpeed.contains("Priority")) 15 else 35,
            isPantrySynced = false
        )

        val entity = orderToEntity(order)
        orderDao.insertOrder(entity)
        _activeLiveOrder.value = order

        startLiveTrackingSimulation(orderId)
        return order
    }

    private fun startLiveTrackingSimulation(orderId: String) {
        simulationJob?.cancel()
        simulationJob = externalScope.launch(Dispatchers.Default) {
            // Step 1: PLACED
            delay(4000)
            updateTrackingState(orderId, OrderStatus.PACKING, 0.15f, 14, 0)

            // Step 2: PACKING -> ON_THE_WAY
            delay(6000)
            updateTrackingState(orderId, OrderStatus.ON_THE_WAY, 0.35f, 10, 24)

            // Progress while driving
            delay(5000)
            updateTrackingState(orderId, OrderStatus.ON_THE_WAY, 0.60f, 7, 28)

            delay(5000)
            updateTrackingState(orderId, OrderStatus.ON_THE_WAY, 0.82f, 4, 18)

            // Step 3: ARRIVING
            delay(5000)
            updateTrackingState(orderId, OrderStatus.ARRIVING, 0.95f, 1, 8)

            // Step 4: DELIVERED
            delay(5000)
            updateTrackingState(orderId, OrderStatus.DELIVERED, 1.0f, 0, 0)
        }
    }

    private suspend fun updateTrackingState(
        orderId: String,
        status: OrderStatus,
        progress: Float,
        etaMinutes: Int,
        speedMph: Int
    ) {
        val current = _activeLiveOrder.value ?: return
        if (current.id == orderId) {
            val updated = current.copy(
                status = status,
                driverProgress = progress,
                estimatedArrivalMinutes = etaMinutes,
                driver = current.driver.copy(currentSpeedMph = speedMph)
            )
            _activeLiveOrder.value = updated
            orderDao.updateOrder(orderToEntity(updated))
        }
    }

    suspend fun markPantrySynced(orderId: String) {
        orderDao.markPantrySynced(orderId)
        _activeLiveOrder.value = _activeLiveOrder.value?.takeIf { it.id == orderId }?.copy(isPantrySynced = true)
    }

    suspend fun simulateQuickAdvance(orderId: String) {
        val current = _activeLiveOrder.value ?: return
        if (current.id == orderId) {
            val nextStatus = when (current.status) {
                OrderStatus.PLACED -> OrderStatus.PACKING
                OrderStatus.PACKING -> OrderStatus.ON_THE_WAY
                OrderStatus.ON_THE_WAY -> OrderStatus.ARRIVING
                OrderStatus.ARRIVING -> OrderStatus.DELIVERED
                OrderStatus.DELIVERED -> OrderStatus.DELIVERED
            }
            val newProgress = when (nextStatus) {
                OrderStatus.PLACED -> 0.05f
                OrderStatus.PACKING -> 0.25f
                OrderStatus.ON_THE_WAY -> 0.65f
                OrderStatus.ARRIVING -> 0.92f
                OrderStatus.DELIVERED -> 1.0f
            }
            val newEta = when (nextStatus) {
                OrderStatus.PLACED -> 16
                OrderStatus.PACKING -> 13
                OrderStatus.ON_THE_WAY -> 8
                OrderStatus.ARRIVING -> 2
                OrderStatus.DELIVERED -> 0
            }
            updateTrackingState(orderId, nextStatus, newProgress, newEta, if (nextStatus == OrderStatus.ON_THE_WAY) 26 else 0)
        }
    }

    private fun orderToEntity(order: Order): OrderEntity {
        val jsonArray = JSONArray()
        order.items.forEach { item ->
            val obj = JSONObject()
            obj.put("id", item.product.id)
            obj.put("name", item.product.name)
            obj.put("category", item.product.category.name)
            obj.put("price", item.product.price)
            obj.put("unit", item.product.unit)
            obj.put("description", item.product.description)
            obj.put("emoji", item.product.emoji)
            obj.put("quantity", item.quantity)
            jsonArray.put(obj)
        }

        return OrderEntity(
            id = order.id,
            orderNumber = order.orderNumber,
            timestamp = order.timestamp,
            statusName = order.status.name,
            itemsJson = jsonArray.toString(),
            subtotal = order.subtotal,
            deliveryFee = order.deliveryFee,
            tip = order.tip,
            discount = order.discount,
            total = order.total,
            deliveryAddress = order.deliveryAddress,
            deliverySpeed = order.deliverySpeed,
            driverName = order.driver.name,
            driverRating = order.driver.rating,
            driverPhone = order.driver.phone,
            driverVehicle = order.driver.vehicle,
            driverProgress = order.driverProgress,
            estimatedArrivalMinutes = order.estimatedArrivalMinutes,
            isPantrySynced = order.isPantrySynced
        )
    }

    private fun entityToOrder(entity: OrderEntity): Order {
        val items = mutableListOf<CartItem>()
        try {
            val jsonArray = JSONArray(entity.itemsJson)
            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                val cat = try { PantryCategory.valueOf(obj.getString("category")) } catch (e: Exception) { PantryCategory.PRODUCE }
                val product = GroceryProduct(
                    id = obj.getString("id"),
                    name = obj.getString("name"),
                    category = cat,
                    price = obj.getDouble("price"),
                    unit = obj.getString("unit"),
                    description = obj.getString("description"),
                    emoji = obj.getString("emoji")
                )
                val qty = obj.getInt("quantity")
                items.add(CartItem(product, qty))
            }
        } catch (e: Exception) {
            // Fallback
        }

        val status = try { OrderStatus.valueOf(entity.statusName) } catch (e: Exception) { OrderStatus.PLACED }

        return Order(
            id = entity.id,
            orderNumber = entity.orderNumber,
            timestamp = entity.timestamp,
            status = status,
            items = items,
            subtotal = entity.subtotal,
            deliveryFee = entity.deliveryFee,
            tip = entity.tip,
            discount = entity.discount,
            total = entity.total,
            deliveryAddress = entity.deliveryAddress,
            deliverySpeed = entity.deliverySpeed,
            driver = DeliveryDriver(
                name = entity.driverName,
                rating = entity.driverRating,
                phone = entity.driverPhone,
                vehicle = entity.driverVehicle
            ),
            driverProgress = entity.driverProgress,
            estimatedArrivalMinutes = entity.estimatedArrivalMinutes,
            isPantrySynced = entity.isPantrySynced
        )
    }
}
