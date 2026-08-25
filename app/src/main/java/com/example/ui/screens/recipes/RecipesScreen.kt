package com.example.ui.screens.recipes

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddShoppingCart
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Kitchen
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.R
import com.example.data.model.Recipe
import com.example.data.model.RecipeIngredient
import com.example.ui.theme.CitrusOrange
import com.example.ui.theme.FreshGreenPrimary
import com.example.ui.theme.GoldenAmber
import com.example.ui.theme.StatusGreen
import com.example.ui.viewmodel.FreshDropViewModel
import kotlinx.coroutines.delay

@Composable
fun RecipesScreen(
    viewModel: FreshDropViewModel,
    onNavigateToShop: () -> Unit,
    modifier: Modifier = Modifier
) {
    val recipes by viewModel.allRecipes.collectAsState()
    val selectedRecipe by viewModel.selectedRecipe.collectAsState()
    val isGenerating by viewModel.isGeneratingRecipe.collectAsState()

    var showAiGeneratorDialog by remember { mutableStateOf(false) }

    if (selectedRecipe != null) {
        RecipeDetailView(
            recipe = selectedRecipe!!,
            onBack = { viewModel.selectRecipe(null) },
            onAddMissingToCart = {
                viewModel.addMissingIngredientsToCart(selectedRecipe!!)
            },
            onNavigateToShop = onNavigateToShop
        )
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
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "AI Chef & Recipes",
                            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = "Gourmet dishes matched to your exact inventory",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    // Prompt AI Chef FAB/button
                    Button(
                        onClick = { showAiGeneratorDialog = true },
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = FreshGreenPrimary),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                        modifier = Modifier.testTag("ai_chef_generate_button")
                    ) {
                        Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = GoldenAmber, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("AI Create", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }
            }

            // Recipe Hero Banner
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                        .clip(RoundedCornerShape(20.dp)),
                    shape = RoundedCornerShape(20.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Box(modifier = Modifier.fillMaxWidth().height(150.dp)) {
                        Image(
                            painter = painterResource(id = R.drawable.hero_recipe_banner),
                            contentDescription = "Gourmet recipe cooking hero",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.horizontalGradient(
                                        colors = listOf(
                                            Color.Black.copy(alpha = 0.8f),
                                            Color.Black.copy(alpha = 0.4f),
                                            Color.Transparent
                                        )
                                    )
                                )
                        )
                        Column(
                            modifier = Modifier
                                .align(Alignment.CenterStart)
                                .padding(16.dp)
                        ) {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = FreshGreenPrimary
                            ) {
                                Text(
                                    text = "ZERO FOOD WASTE",
                                    color = Color.White,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Cook With What You Have",
                                color = Color.White,
                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold)
                            )
                            Text(
                                text = "AI analyzes your fridge & delivers missing ingredients",
                                color = Color.White.copy(alpha = 0.9f),
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }
            }

            // Section Title
            item {
                Text(
                    text = "Pantry-Matched Recipes (${recipes.size})",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }

            // Recipe Cards
            items(recipes, key = { it.id }) { recipe ->
                RecipeCard(
                    recipe = recipe,
                    onClick = { viewModel.selectRecipe(recipe) },
                    onAddMissing = { viewModel.addMissingIngredientsToCart(recipe) }
                )
            }
        }
    }

    if (showAiGeneratorDialog) {
        AiRecipeGeneratorDialog(
            isGenerating = isGenerating,
            onDismiss = { showAiGeneratorDialog = false },
            onGenerate = { mealType, dietary, maxTime, prompt ->
                viewModel.generateAiRecipe(mealType, dietary, maxTime, prompt)
                showAiGeneratorDialog = false
            }
        )
    }
}

@Composable
fun RecipeCard(
    recipe: Recipe,
    onClick: () -> Unit,
    onAddMissing: () -> Unit
) {
    val inPantryCount = recipe.ingredients.count { it.isAvailableInPantry }
    val totalCount = recipe.ingredients.size
    val isPerfectMatch = recipe.matchPercentage >= 95

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .clip(RoundedCornerShape(18.dp))
            .clickable { onClick() }
            .testTag("recipe_card_${recipe.id}"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Match pill
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = if (isPerfectMatch) StatusGreen.copy(alpha = 0.15f) else GoldenAmber.copy(alpha = 0.15f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isPerfectMatch) "✨ ${recipe.matchPercentage}% Pantry Match (Ready to Cook)" else "📦 ${recipe.matchPercentage}% In Pantry ($inPantryCount/$totalCount items)",
                            color = if (isPerfectMatch) StatusGreen else GoldenAmber,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                    }
                }

                if (recipe.isAiGenerated) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = FreshGreenPrimary.copy(alpha = 0.15f)
                    ) {
                        Text(
                            text = "AI Chef Special",
                            color = FreshGreenPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = recipe.emoji, fontSize = 32.sp)
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = recipe.title,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "${recipe.prepTimeMinutes + recipe.cookTimeMinutes} mins • ${recipe.cuisine} • ${recipe.calories} kcal • ${recipe.difficulty}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = recipe.description,
                        style = MaterialTheme.typography.bodySmall,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Ingredients In-Stock / Missing Summary
            val missingIngredients = recipe.ingredients.filter { !it.isAvailableInPantry }
            if (missingIngredients.isNotEmpty()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Missing: ${missingIngredients.take(2).joinToString(", ") { it.name }}${if (missingIngredients.size > 2) " +${missingIngredients.size - 2} more" else ""}",
                        style = MaterialTheme.typography.labelSmall,
                        color = CitrusOrange,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    FilledTonalButton(
                        onClick = onAddMissing,
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                        modifier = Modifier.height(28.dp)
                    ) {
                        Icon(Icons.Default.AddShoppingCart, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Get Missing", fontSize = 11.sp)
                    }
                }
            } else {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = StatusGreen, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "You have 100% of the ingredients in your kitchen!",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = StatusGreen
                    )
                }
            }
        }
    }
}

@Composable
fun RecipeDetailView(
    recipe: Recipe,
    onBack: () -> Unit,
    onAddMissingToCart: () -> Unit,
    onNavigateToShop: () -> Unit
) {
    var servingsMultiplier by remember { mutableIntStateOf(recipe.servings) }
    var isCookingMode by remember { mutableStateOf(false) }
    val checkedSteps = remember { mutableStateListOf<Int>() }

    // Cooking timer state
    var timerSeconds by remember { mutableIntStateOf(recipe.cookTimeMinutes * 60) }
    var isTimerRunning by remember { mutableStateOf(false) }

    LaunchedEffect(isTimerRunning) {
        while (isTimerRunning && timerSeconds > 0) {
            delay(1000)
            timerSeconds--
        }
        if (timerSeconds == 0) isTimerRunning = false
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 90.dp)
    ) {
        // Top Back Row
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                }
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = FreshGreenPrimary.copy(alpha = 0.12f)
                ) {
                    Text(
                        text = "${recipe.matchPercentage}% In Your Pantry",
                        color = FreshGreenPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
        }

        // Header Title & Meta
        item {
            Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(RoundedCornerShape(18.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = recipe.emoji, fontSize = 42.sp)
                }
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = recipe.title,
                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = recipe.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(12.dp))

                // Stats Pills
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    RecipeMetaPill(icon = "⏱", label = "Prep", value = "${recipe.prepTimeMinutes}m", modifier = Modifier.weight(1f))
                    RecipeMetaPill(icon = "🍳", label = "Cook", value = "${recipe.cookTimeMinutes}m", modifier = Modifier.weight(1f))
                    RecipeMetaPill(icon = "🔥", label = "Calories", value = "${recipe.calories}", modifier = Modifier.weight(1f))
                    RecipeMetaPill(icon = "📊", label = "Level", value = recipe.difficulty, modifier = Modifier.weight(1f))
                }
            }
        }

        // Interactive Cooking Timer Bar
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Timer, contentDescription = null, tint = FreshGreenPrimary)
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Cook Timer: ${timerSeconds / 60}:${String.format("%02d", timerSeconds % 60)}",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Text(
                                text = if (isTimerRunning) "Timer is counting down..." else "Ready to time your cooking",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                            )
                        }
                    }

                    Row {
                        IconButton(
                            onClick = { isTimerRunning = !isTimerRunning },
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(FreshGreenPrimary)
                        ) {
                            Icon(
                                imageVector = if (isTimerRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
                                contentDescription = "Timer toggle",
                                tint = Color.White
                            )
                        }
                    }
                }
            }
        }

        // Ingredients Section
        item {
            Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Ingredients (${recipe.ingredients.size})",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )

                    // Servings selector
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Servings: $servingsMultiplier", style = MaterialTheme.typography.labelSmall)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                recipe.ingredients.forEach { ingredient ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 3.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (ingredient.isAvailableInPantry) MaterialTheme.colorScheme.surface else CitrusOrange.copy(alpha = 0.08f)
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = if (ingredient.isAvailableInPantry) Icons.Default.CheckCircle else Icons.Default.Warning,
                                    contentDescription = null,
                                    tint = if (ingredient.isAvailableInPantry) StatusGreen else CitrusOrange,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = ingredient.name,
                                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
                                    )
                                    Text(
                                        text = if (ingredient.isAvailableInPantry) "In Pantry" else "Missing from pantry",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = if (ingredient.isAvailableInPantry) StatusGreen else CitrusOrange
                                    )
                                }
                            }

                            Text(
                                text = ingredient.amount,
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                color = FreshGreenPrimary
                            )
                        }
                    }
                }

                // Add missing ingredients action
                val missing = recipe.ingredients.filter { !it.isAvailableInPantry }
                if (missing.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = onAddMissingToCart,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = CitrusOrange)
                    ) {
                        Icon(Icons.Default.AddShoppingCart, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Add ${missing.size} Missing Ingredients to Grocery Cart")
                    }
                }
            }
        }

        // Cooking Instructions Step-by-Step
        item {
            Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp)) {
                Text(
                    text = "Step-by-Step Cooking Guide",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
                Spacer(modifier = Modifier.height(8.dp))

                recipe.instructions.forEachIndexed { index, step ->
                    val isChecked = checkedSteps.contains(index)
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clickable {
                                if (isChecked) checkedSteps.remove(index) else checkedSteps.add(index)
                            },
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isChecked) StatusGreen.copy(alpha = 0.08f) else MaterialTheme.colorScheme.surface
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(CircleShape)
                                    .background(if (isChecked) StatusGreen else FreshGreenPrimary),
                                contentAlignment = Alignment.Center
                            ) {
                                if (isChecked) {
                                    Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                                } else {
                                    Text(text = "${index + 1}", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                }
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Text(
                                text = step,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }
        }

        // Chef Tips Section
        if (recipe.chefTips.isNotEmpty()) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 8.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = GoldenAmber)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Chef's Secret Tips",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        recipe.chefTips.forEach { tip ->
                            Text(
                                text = "• $tip",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSecondaryContainer,
                                modifier = Modifier.padding(vertical = 2.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun RecipeMetaPill(icon: String, label: String, value: String, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceVariant
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = icon, fontSize = 16.sp)
            Text(text = value, style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold))
            Text(text = label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
fun AiRecipeGeneratorDialog(
    isGenerating: Boolean,
    onDismiss: () -> Unit,
    onGenerate: (mealType: String, dietary: String, maxTime: Int, prompt: String) -> Unit
) {
    var mealType by remember { mutableStateOf("Dinner") }
    var dietary by remember { mutableStateOf("None") }
    var maxTime by remember { mutableIntStateOf(25) }
    var customPrompt by remember { mutableStateOf("") }

    val mealTypes = listOf("Breakfast", "Lunch", "Dinner", "Snack & Dessert")
    val dietaryOptions = listOf("None", "Vegetarian", "Vegan", "Keto", "High-Protein", "Gluten-Free")
    val timeOptions = listOf(15, 25, 45, 60)

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = GoldenAmber)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "AI Chef Generator",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Text(
                    text = "Gemini AI will craft a custom recipe prioritized around your current pantry items.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Meal Type
                Text(text = "Meal Type", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold))
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.padding(vertical = 4.dp)
                ) {
                    items(mealTypes) { type ->
                        FilterChip(
                            selected = mealType == type,
                            onClick = { mealType = type },
                            label = { Text(type) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Dietary
                Text(text = "Dietary Preference", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold))
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.padding(vertical = 4.dp)
                ) {
                    items(dietaryOptions) { option ->
                        FilterChip(
                            selected = dietary == option,
                            onClick = { dietary = option },
                            label = { Text(option) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Max Cook Time
                Text(text = "Max Cooking Time: $maxTime mins", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold))
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.padding(vertical = 4.dp)
                ) {
                    items(timeOptions) { time ->
                        FilterChip(
                            selected = maxTime == time,
                            onClick = { maxTime = time },
                            label = { Text("< $time min") }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Custom Request
                OutlinedTextField(
                    value = customPrompt,
                    onValueChange = { customPrompt = it },
                    placeholder = { Text("e.g. Italian pasta with spicy garlic kick...") },
                    label = { Text("Special Request / Cravings (Optional)") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    maxLines = 2
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = { onGenerate(mealType, dietary, maxTime, customPrompt) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("ai_chef_submit_generate"),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = FreshGreenPrimary),
                    enabled = !isGenerating
                ) {
                    if (isGenerating) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(22.dp),
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Chef is cooking recipe with Gemini...")
                    } else {
                        Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = GoldenAmber)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Generate Pantry Recipe ✨", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
