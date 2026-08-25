package com.example.ui.screens.tracking

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeliveryDining
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Kitchen
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.R
import com.example.data.model.Order
import com.example.data.model.OrderStatus
import com.example.ui.theme.CitrusOrange
import com.example.ui.theme.FreshGreenPrimary
import com.example.ui.theme.GoldenAmber
import com.example.ui.theme.StatusBlue
import com.example.ui.theme.StatusGreen
import com.example.ui.viewmodel.FreshDropViewModel
import java.util.Locale

@Composable
fun LiveTrackingScreen(
    viewModel: FreshDropViewModel,
    onNavigateToShop: () -> Unit,
    onNavigateToPantry: () -> Unit,
    modifier: Modifier = Modifier
) {
    val activeOrder by viewModel.activeLiveOrder.collectAsState()
    val allOrders by viewModel.allOrders.collectAsState()

    var showChatDialog by remember { mutableStateOf(false) }
    var showCallDialog by remember { mutableStateOf(false) }

    val orderToTrack = activeOrder ?: allOrders.firstOrNull()

    if (orderToTrack == null) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(32.dp)
            ) {
                Text(text = "🛵", fontSize = 56.sp)
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "No Active Delivery Orders",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Add organic groceries to your basket to track real-time delivery and auto-restock your pantry.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
                Spacer(modifier = Modifier.height(20.dp))
                Button(
                    onClick = onNavigateToShop,
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = FreshGreenPrimary)
                ) {
                    Text("Explore Fresh Market")
                }
            }
        }
    } else {
        LazyColumn(
            modifier = modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 90.dp)
        ) {
            // Header
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Live Order Tracking",
                            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = "Order #${orderToTrack.orderNumber} • ${orderToTrack.deliverySpeed}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    // Simulation Fast Forward Button (for interactive preview)
                    if (orderToTrack.status != OrderStatus.DELIVERED) {
                        FilledTonalButton(
                            onClick = { viewModel.simulateAdvanceLiveOrder(orderToTrack.id) },
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            modifier = Modifier.testTag("fast_forward_sim_btn")
                        ) {
                            Icon(Icons.Default.FastForward, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Next Step", fontSize = 11.sp)
                        }
                    }
                }
            }

            // Interactive Live Map Canvas with Route Animation
            item {
                LiveMapCanvasCard(
                    order = orderToTrack,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                )
            }

            // Delivery Status & Stepper
            item {
                TrackingStatusStepperCard(order = orderToTrack)
            }

            // Delivered Banner & Auto-Sync to Pantry
            if (orderToTrack.status == OrderStatus.DELIVERED) {
                item {
                    DeliveredPantrySyncCard(
                        order = orderToTrack,
                        onSyncPantry = {
                            viewModel.syncDeliveredGroceriesToPantry(orderToTrack)
                        },
                        onNavigateToPantry = onNavigateToPantry
                    )
                }
            }

            // Courier / Driver Card
            item {
                DriverContactCard(
                    driver = orderToTrack.driver,
                    status = orderToTrack.status,
                    onOpenChat = { showChatDialog = true },
                    onOpenCall = { showCallDialog = true }
                )
            }

            // Order Items Summary
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Ordered Groceries (${orderToTrack.items.sumOf { it.quantity }})",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                            Text(
                                text = "$${String.format(Locale.US, "%.2f", orderToTrack.total)}",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold),
                                color = FreshGreenPrimary
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        orderToTrack.items.forEach { item ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(text = item.product.emoji, fontSize = 20.sp)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "${item.quantity}x ${item.product.name}",
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                                Text(
                                    text = "$${String.format(Locale.US, "%.2f", item.totalPrice)}",
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        HorizontalDivider()
                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.LocationOn, contentDescription = null, tint = CitrusOrange, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = orderToTrack.deliveryAddress,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }

    // Courier Chat Modal
    if (showChatDialog && orderToTrack != null) {
        DriverChatDialog(
            driverName = orderToTrack.driver.name,
            onDismiss = { showChatDialog = false }
        )
    }

    // Call Driver Modal
    if (showCallDialog && orderToTrack != null) {
        DriverCallDialog(
            driverName = orderToTrack.driver.name,
            driverPhone = orderToTrack.driver.phone,
            onDismiss = { showCallDialog = false }
        )
    }
}

@Composable
fun LiveMapCanvasCard(
    order: Order,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseRadius by infiniteTransition.animateFloat(
        initialValue = 12f,
        targetValue = 28f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "pulseRadius"
    )
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 0.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "pulseAlpha"
    )

    Card(
        modifier = modifier
            .height(230.dp)
            .clip(RoundedCornerShape(22.dp)),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B))
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val width = size.width
                val height = size.height

                // Draw map grid lines (streets)
                val gridColor = Color(0xFF334155)
                val roadColor = Color(0xFF475569)

                // Background grid streets
                for (x in 0..width.toInt() step 60) {
                    drawLine(gridColor, Offset(x.toFloat(), 0f), Offset(x.toFloat(), height), strokeWidth = 1f)
                }
                for (y in 0..height.toInt() step 45) {
                    drawLine(gridColor, Offset(0f, y.toFloat()), Offset(width, y.toFloat()), strokeWidth = 1f)
                }

                // Major arterial avenue
                drawLine(roadColor, Offset(0f, height * 0.45f), Offset(width, height * 0.45f), strokeWidth = 8f)
                drawLine(roadColor, Offset(width * 0.55f, 0f), Offset(width * 0.55f, height), strokeWidth = 8f)

                // Route Path coordinates: Store -> Turn 1 -> Turn 2 -> Customer Home
                val storePoint = Offset(width * 0.15f, height * 0.75f)
                val waypoint1 = Offset(width * 0.35f, height * 0.75f)
                val waypoint2 = Offset(width * 0.35f, height * 0.30f)
                val waypoint3 = Offset(width * 0.78f, height * 0.30f)
                val homePoint = Offset(width * 0.78f, height * 0.65f)

                val routePath = Path().apply {
                    moveTo(storePoint.x, storePoint.y)
                    lineTo(waypoint1.x, waypoint1.y)
                    lineTo(waypoint2.x, waypoint2.y)
                    lineTo(waypoint3.x, waypoint3.y)
                    lineTo(homePoint.x, homePoint.y)
                }

                // Draw Route Background Path
                drawPath(
                    path = routePath,
                    color = Color(0xFF0F766E).copy(alpha = 0.4f),
                    style = Stroke(width = 8f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(15f, 10f), 0f))
                )

                // Calculate Driver position along the path based on order.driverProgress
                val progress = order.driverProgress.coerceIn(0f, 1f)
                val driverPoint = when {
                    progress <= 0.25f -> {
                        val localP = progress / 0.25f
                        Offset(storePoint.x + (waypoint1.x - storePoint.x) * localP, storePoint.y)
                    }
                    progress <= 0.50f -> {
                        val localP = (progress - 0.25f) / 0.25f
                        Offset(waypoint1.x, waypoint1.y + (waypoint2.y - waypoint1.y) * localP)
                    }
                    progress <= 0.75f -> {
                        val localP = (progress - 0.50f) / 0.25f
                        Offset(waypoint2.x + (waypoint3.x - waypoint2.x) * localP, waypoint2.y)
                    }
                    else -> {
                        val localP = (progress - 0.75f) / 0.25f
                        Offset(waypoint3.x, waypoint3.y + (homePoint.y - waypoint3.y) * localP)
                    }
                }

                // Draw Store Marker
                drawCircle(Color(0xFF0F766E), radius = 14f, center = storePoint)
                drawCircle(Color.White, radius = 6f, center = storePoint)

                // Draw Home Marker
                drawCircle(Color(0xFFEA580C), radius = 14f, center = homePoint)
                drawCircle(Color.White, radius = 6f, center = homePoint)

                // Draw Driver Marker & Pulse wave
                if (order.status != OrderStatus.DELIVERED) {
                    drawCircle(
                        color = Color(0xFF2DD4BF).copy(alpha = pulseAlpha),
                        radius = pulseRadius,
                        center = driverPoint
                    )
                    drawCircle(Color(0xFF2DD4BF), radius = 12f, center = driverPoint)
                    drawCircle(Color(0xFF0F172A), radius = 6f, center = driverPoint)
                }
            }

            // Overlay HUD Cards on top of map
            // Store Tag
            Surface(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(12.dp),
                shape = RoundedCornerShape(10.dp),
                color = Color(0xFF0F172A).copy(alpha = 0.85f)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Storefront, contentDescription = null, tint = FreshGreenPrimary, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "FreshDrop Hub", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }

            // Customer Home Tag
            Surface(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(12.dp),
                shape = RoundedCornerShape(10.dp),
                color = Color(0xFF0F172A).copy(alpha = 0.85f)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Home, contentDescription = null, tint = CitrusOrange, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "Home (4B)", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }

            // Top Telemetry Bar (Live Speed & ETA)
            Surface(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(10.dp),
                shape = RoundedCornerShape(16.dp),
                color = Color(0xFF0F172A).copy(alpha = 0.90f)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Speed, contentDescription = null, tint = Color(0xFF2DD4BF), modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${order.driver.currentSpeedMph} MPH",
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Box(modifier = Modifier.width(1.dp).height(14.dp).background(Color(0xFF475569)))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "🛵", fontSize = 13.sp)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (order.estimatedArrivalMinutes > 0) "ETA: ~${order.estimatedArrivalMinutes} min" else "Arrived!",
                            color = if (order.estimatedArrivalMinutes <= 2) CitrusOrange else Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TrackingStatusStepperCard(order: Order) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = order.status.title,
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = FreshGreenPrimary
            )
            Text(
                text = order.status.description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Stepper timeline
            val steps = OrderStatus.values()
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                steps.forEachIndexed { index, step ->
                    val isPastOrCurrent = index <= order.status.stepIndex
                    val isCurrent = index == order.status.stepIndex

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(
                                    if (isPastOrCurrent) FreshGreenPrimary else MaterialTheme.colorScheme.surfaceVariant
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isPastOrCurrent) {
                                Icon(
                                    Icons.Default.Check,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                            } else {
                                Text(
                                    text = "${index + 1}",
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = when (step) {
                                OrderStatus.PLACED -> "Placed"
                                OrderStatus.PACKING -> "Packing"
                                OrderStatus.ON_THE_WAY -> "En Route"
                                OrderStatus.ARRIVING -> "Near"
                                OrderStatus.DELIVERED -> "Done"
                            },
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal
                            ),
                            color = if (isPastOrCurrent) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DeliveredPantrySyncCard(
    order: Order,
    onSyncPantry: () -> Unit,
    onNavigateToPantry: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .testTag("delivered_sync_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "🎉", fontSize = 28.sp)
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "Groceries Successfully Delivered!",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        text = "Everything was safely placed at your doorstep.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            if (!order.isPantrySynced) {
                Button(
                    onClick = onSyncPantry,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = FreshGreenPrimary),
                    contentPadding = PaddingValues(vertical = 12.dp),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
                ) {
                    Icon(Icons.Default.Kitchen, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Add ${order.items.size} Items to My Pantry Inventory",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            } else {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = StatusGreen.copy(alpha = 0.15f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = StatusGreen)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Synced with Smart Pantry!",
                                fontWeight = FontWeight.Bold,
                                color = StatusGreen,
                                fontSize = 13.sp
                            )
                        }

                        TextButton(onClick = onNavigateToPantry) {
                            Text("Open Pantry →", color = FreshGreenPrimary, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DriverContactCard(
    driver: com.example.data.model.DeliveryDriver,
    status: OrderStatus,
    onOpenChat: () -> Unit,
    onOpenCall: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "🛵", fontSize = 28.sp)
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = driver.name,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Star, contentDescription = null, tint = GoldenAmber, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(2.dp))
                        Text(
                            text = "${driver.rating} (${driver.totalDeliveries} trips) • ${driver.vehicle}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                IconButton(
                    onClick = onOpenCall,
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(FreshGreenPrimary.copy(alpha = 0.12f))
                ) {
                    Icon(Icons.Default.Call, contentDescription = "Call Driver", tint = FreshGreenPrimary)
                }

                IconButton(
                    onClick = onOpenChat,
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(FreshGreenPrimary.copy(alpha = 0.12f))
                ) {
                    Icon(Icons.Default.Chat, contentDescription = "Message Driver", tint = FreshGreenPrimary)
                }
            }
        }
    }
}

@Composable
fun DriverChatDialog(
    driverName: String,
    onDismiss: () -> Unit
) {
    val messages = remember {
        mutableStateListOf(
            "Driver" to "Hi! I picked up your order from Whole Market. Everything looks super fresh!",
            "Driver" to "On my way to your address now on the Honda EV scooter. ETA ~10 mins."
        )
    }
    var inputText by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier.fillMaxWidth().height(480.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "🛵", fontSize = 24.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(text = driverName, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                            Text(text = "Online • Courier", style = MaterialTheme.typography.labelSmall, color = StatusGreen)
                        }
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                // Messages list
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(messages) { (sender, text) ->
                        val isMe = sender == "Me"
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = if (isMe) Arrangement.End else Arrangement.Start
                        ) {
                            Surface(
                                shape = RoundedCornerShape(
                                    topStart = 16.dp,
                                    topEnd = 16.dp,
                                    bottomStart = if (isMe) 16.dp else 4.dp,
                                    bottomEnd = if (isMe) 4.dp else 16.dp
                                ),
                                color = if (isMe) FreshGreenPrimary else MaterialTheme.colorScheme.surfaceVariant
                            ) {
                                Text(
                                    text = text,
                                    color = if (isMe) Color.White else MaterialTheme.colorScheme.onSurface,
                                    fontSize = 13.sp,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Input box
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = inputText,
                        onValueChange = { inputText = it },
                        placeholder = { Text("Message $driverName...") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(16.dp),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    IconButton(
                        onClick = {
                            if (inputText.isNotBlank()) {
                                messages.add("Me" to inputText)
                                inputText = ""
                            }
                        },
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(FreshGreenPrimary)
                    ) {
                        Icon(Icons.Default.Send, contentDescription = "Send", tint = Color.White)
                    }
                }
            }
        }
    }
}

@Composable
fun DriverCallDialog(
    driverName: String,
    driverPhone: String,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(FreshGreenPrimary.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Call, contentDescription = null, tint = FreshGreenPrimary, modifier = Modifier.size(32.dp))
                }

                Spacer(modifier = Modifier.height(14.dp))
                Text(
                    text = "Contact $driverName",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                    text = "Masked secure courier line: $driverPhone",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Cancel")
                    }
                    Button(
                        onClick = {
                            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$driverPhone"))
                            try {
                                context.startActivity(intent)
                            } catch (e: Exception) {
                                // Ignore in emulator
                            }
                            onDismiss()
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = FreshGreenPrimary)
                    ) {
                        Text("Call Driver")
                    }
                }
            }
        }
    }
}
