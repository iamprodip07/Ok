package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.DeliveryDining
import androidx.compose.material.icons.filled.Kitchen
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.DeliveryDining
import androidx.compose.material.icons.outlined.Kitchen
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.ShoppingBag
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.OrderStatus
import com.example.ui.components.ActiveDeliveryBanner
import com.example.ui.screens.pantry.PantryScreen
import com.example.ui.screens.profile.ProfileScreen
import com.example.ui.screens.recipes.RecipesScreen
import com.example.ui.screens.shop.ShopScreen
import com.example.ui.screens.tracking.LiveTrackingScreen
import com.example.ui.theme.CitrusOrange
import com.example.ui.theme.FreshGreenPrimary
import com.example.ui.theme.GoldenAmber
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.FreshDropViewModel

enum class MainTab(
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val testTag: String
) {
    SHOP("Shop", Icons.Filled.ShoppingBag, Icons.Outlined.ShoppingBag, "nav_shop_tab"),
    PANTRY("Pantry", Icons.Filled.Kitchen, Icons.Outlined.Kitchen, "nav_pantry_tab"),
    RECIPES("AI Chef", Icons.Filled.AutoAwesome, Icons.Outlined.AutoAwesome, "nav_recipes_tab"),
    TRACKING("Live Track", Icons.Filled.DeliveryDining, Icons.Outlined.DeliveryDining, "nav_tracking_tab"),
    PROFILE("Profile", Icons.Filled.Person, Icons.Outlined.Person, "nav_profile_tab")
}

class MainActivity : ComponentActivity() {
    private val viewModel: FreshDropViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                FreshDropApp(viewModel = viewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FreshDropApp(viewModel: FreshDropViewModel) {
    var currentTab by remember { mutableStateOf(MainTab.SHOP) }
    val snackbarHostState = remember { SnackbarHostState() }

    val toastMessage by viewModel.userMessageToast.collectAsState()
    val activeLiveOrder by viewModel.activeLiveOrder.collectAsState()
    val cartItems by viewModel.cartItems.collectAsState()
    val lowStockItems by viewModel.lowStockItems.collectAsState()

    val cartItemCount = cartItems.sumOf { it.quantity }
    val lowStockCount = lowStockItems.size

    LaunchedEffect(toastMessage) {
        toastMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearToast()
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = FreshGreenPrimary,
                            modifier = Modifier.size(32.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(text = "🥗", fontSize = 18.sp)
                            }
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "FreshDrop",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    letterSpacing = (-0.5).sp
                                ),
                                color = FreshGreenPrimary
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 6.dp
            ) {
                MainTab.values().forEach { tab ->
                    val isSelected = currentTab == tab
                    val hasActiveDelivery = tab == MainTab.TRACKING && activeLiveOrder != null && activeLiveOrder?.status != OrderStatus.DELIVERED

                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { currentTab = tab },
                        icon = {
                            BadgedBox(
                                badge = {
                                    if (tab == MainTab.SHOP && cartItemCount > 0) {
                                        Badge(
                                            containerColor = CitrusOrange,
                                            contentColor = Color.White
                                        ) {
                                            Text("$cartItemCount")
                                        }
                                    } else if (tab == MainTab.PANTRY && lowStockCount > 0) {
                                        Badge(
                                            containerColor = GoldenAmber,
                                            contentColor = Color.White
                                        ) {
                                            Text("$lowStockCount")
                                        }
                                    } else if (hasActiveDelivery) {
                                        Badge(
                                            containerColor = CitrusOrange,
                                            modifier = Modifier.size(8.dp)
                                        )
                                    }
                                }
                            ) {
                                Icon(
                                    imageVector = if (isSelected) tab.selectedIcon else tab.unselectedIcon,
                                    contentDescription = tab.title,
                                    tint = if (isSelected) FreshGreenPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        },
                        label = {
                            Text(
                                text = tab.title,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                ),
                                color = if (isSelected) FreshGreenPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            indicatorColor = FreshGreenPrimary.copy(alpha = 0.15f)
                        ),
                        modifier = Modifier.testTag(tab.testTag)
                    )
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Persistent Floating Delivery Banner on all tabs except Tracking tab
            if (currentTab != MainTab.TRACKING) {
                ActiveDeliveryBanner(
                    order = activeLiveOrder,
                    onNavigateToTracking = { currentTab = MainTab.TRACKING }
                )
            }

            // Screen Content
            AnimatedContent(
                targetState = currentTab,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "ScreenTransition"
            ) { targetTab ->
                when (targetTab) {
                    MainTab.SHOP -> ShopScreen(
                        viewModel = viewModel,
                        onNavigateToTracking = {
                            currentTab = MainTab.TRACKING
                        }
                    )
                    MainTab.PANTRY -> PantryScreen(
                        viewModel = viewModel,
                        onNavigateToRecipes = {
                            currentTab = MainTab.RECIPES
                        }
                    )
                    MainTab.RECIPES -> RecipesScreen(
                        viewModel = viewModel,
                        onNavigateToShop = {
                            currentTab = MainTab.SHOP
                        }
                    )
                    MainTab.TRACKING -> LiveTrackingScreen(
                        viewModel = viewModel,
                        onNavigateToShop = {
                            currentTab = MainTab.SHOP
                        },
                        onNavigateToPantry = {
                            currentTab = MainTab.PANTRY
                        }
                    )
                    MainTab.PROFILE -> ProfileScreen(
                        viewModel = viewModel,
                        onNavigateToTracking = {
                            currentTab = MainTab.TRACKING
                        }
                    )
                }
            }
        }
    }
}
