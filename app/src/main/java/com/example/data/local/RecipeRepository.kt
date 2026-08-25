package com.example.data.local

import com.example.BuildConfig
import com.example.data.model.PantryItem
import com.example.data.model.Recipe
import com.example.data.model.RecipeIngredient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class RecipeRepository {

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    val defaultRecipes = listOf(
        Recipe(
            id = "rec_1",
            title = "Creamy Tomato Basil Rigatoni",
            description = "Silky Italian pasta with blistered San Marzano cherry tomatoes, fragrant sweet basil, extra virgin olive oil, and aged Parmigiano Reggiano.",
            prepTimeMinutes = 10,
            cookTimeMinutes = 15,
            servings = 4,
            difficulty = "Easy",
            calories = 420,
            cuisine = "Italian",
            emoji = "🍝",
            ingredients = listOf(
                RecipeIngredient("Italian Bronze-Cut Rigatoni", "12 oz"),
                RecipeIngredient("San Marzano Cherry Tomatoes", "1 box (10 oz)"),
                RecipeIngredient("Fresh Sweet Basil", "1 bunch"),
                RecipeIngredient("California Heirloom Garlic", "4 cloves"),
                RecipeIngredient("Cold-Pressed Extra Virgin Olive Oil", "3 tbsp"),
                RecipeIngredient("Aged Parmigiano Reggiano DOP", "1/2 cup grated"),
                RecipeIngredient("European Style Grass-Fed Butter", "2 tbsp", isOptional = true)
            ),
            instructions = listOf(
                "Bring a large pot of salted water to a rolling boil. Cook rigatoni until al dente (about 10 minutes). Reserve 1/2 cup pasta water before draining.",
                "While pasta cooks, heat olive oil in a wide skillet over medium heat. Add sliced garlic and cook for 1 minute until fragrant.",
                "Add cherry tomatoes and a pinch of salt. Cook until tomatoes burst and form a rich glossy sauce (about 6-8 minutes).",
                "Toss drained rigatoni directly into the skillet with butter and reserved pasta water. Stir vigorously over high heat to emulsify.",
                "Turn off heat. Fold in torn fresh basil and shower generously with freshly grated Parmigiano Reggiano. Serve immediately."
            ),
            chefTips = listOf(
                "Always salt your pasta water generously—it should taste like the sea.",
                "Emulsifying hot pasta water with butter and olive oil creates that restaurant-quality silky sheen."
            ),
            tags = listOf("Vegetarian", "Quick 25-Min", "Comfort Food", "Pantry Classic")
        ),
        Recipe(
            id = "rec_2",
            title = "Gourmet Avocado Toast with Poached Egg",
            description = "Toasted artisan sourdough rubbed with garlic, topped with mashed Hass avocado, lime zest, chili flakes, and a golden runny poached egg.",
            prepTimeMinutes = 5,
            cookTimeMinutes = 5,
            servings = 2,
            difficulty = "Easy",
            calories = 310,
            cuisine = "California Cafe",
            emoji = "🥑",
            ingredients = listOf(
                RecipeIngredient("Organic Hass Avocados", "2 ripe"),
                RecipeIngredient("Artisan Sourdough Country Boule", "2 thick slices"),
                RecipeIngredient("Pasture-Raised Free Range Eggs", "2 large"),
                RecipeIngredient("Fresh Eureka Lemons", "1/2 lemon (juiced)"),
                RecipeIngredient("Cold-Pressed Extra Virgin Olive Oil", "1 tsp"),
                RecipeIngredient("California Heirloom Garlic", "1 clove"),
                RecipeIngredient("Red Chili Flakes & Flaky Sea Salt", "To taste", isOptional = true)
            ),
            instructions = listOf(
                "Toast sourdough slices until deeply golden and crisp.",
                "Lightly rub the warm crust with a halved raw garlic clove for subtle aroma.",
                "In a bowl, coarsely mash avocados with fresh lemon juice, olive oil, salt, and pepper.",
                "Bring a small pot of water to a gentle simmer. Swirl water to create a whirlpool, gently slide cracked egg into the center, and poach for 3 minutes.",
                "Spread avocado generously on toasted sourdough, crown with the warm poached egg, and finish with flaky sea salt and chili flakes."
            ),
            chefTips = listOf(
                "Fresh pasture-raised eggs hold their whites tightly during poaching without needing vinegar.",
                "Leave the avocado slightly chunky rather than smooth for superior texture contrast."
            ),
            tags = listOf("High-Protein", "Breakfast", "Under 15 Mins", "Vegetarian")
        ),
        Recipe(
            id = "rec_3",
            title = "Pan-Seared Garlic Butter Salmon",
            description = "Crispy skin wild Alaskan salmon basted in foaming grass-fed butter, smashed garlic cloves, fresh lemon juice, and baby spinach.",
            prepTimeMinutes = 5,
            cookTimeMinutes = 12,
            servings = 2,
            difficulty = "Medium",
            calories = 460,
            cuisine = "Mediterranean",
            emoji = "🐟",
            ingredients = listOf(
                RecipeIngredient("Wild Caught Alaskan Salmon Fillets", "2 fillets (12 oz)"),
                RecipeIngredient("European Style Grass-Fed Butter", "3 tbsp"),
                RecipeIngredient("California Heirloom Garlic", "3 cloves smashed"),
                RecipeIngredient("Fresh Eureka Lemons", "1 lemon"),
                RecipeIngredient("Organic Crisp Baby Spinach", "3 cups"),
                RecipeIngredient("Cold-Pressed Extra Virgin Olive Oil", "1 tbsp")
            ),
            instructions = listOf(
                "Pat salmon fillets completely dry with paper towels. Season both sides generously with sea salt and black pepper.",
                "Heat olive oil in a heavy stainless or cast-iron skillet over medium-high heat until shimmering.",
                "Place salmon skin-side down. Press down gently with a spatula for 10 seconds. Cook undisturbed for 5 minutes until skin is crackling and crispy.",
                "Flip salmon. Add grass-fed butter and smashed garlic to the pan. Spoon the foaming garlic butter over the salmon for 3-4 minutes.",
                "Toss baby spinach and a squeeze of fresh lemon into the pan for 1 minute until wilted. Plate salmon over greens."
            ),
            chefTips = listOf(
                "Moisture is the enemy of crispy skin—ensure the fish is bone dry before it hits the pan.",
                "Basting with butter imparts a rich nutty flavor and cooks the top gently without drying it out."
            ),
            tags = listOf("Keto", "High-Protein", "Gluten-Free", "Omega-3 Rich")
        ),
        Recipe(
            id = "rec_4",
            title = "Spinach & Herb Frittata",
            description = "Fluffy pasture-raised baked eggs folded with tender baby spinach, aromatic basil, aged parmesan, and a dash of whole milk.",
            prepTimeMinutes = 8,
            cookTimeMinutes = 15,
            servings = 3,
            difficulty = "Easy",
            calories = 240,
            cuisine = "Brunch",
            emoji = "🍳",
            ingredients = listOf(
                RecipeIngredient("Pasture-Raised Free Range Eggs", "6 large"),
                RecipeIngredient("Organic Whole Milk (1 Gallon)", "1/4 cup"),
                RecipeIngredient("Organic Crisp Baby Spinach", "2 cups"),
                RecipeIngredient("Fresh Sweet Basil", "1/4 cup chopped"),
                RecipeIngredient("Aged Parmigiano Reggiano DOP", "1/3 cup grated"),
                RecipeIngredient("European Style Grass-Fed Butter", "1 tbsp"),
                RecipeIngredient("California Heirloom Garlic", "1 clove minced")
            ),
            instructions = listOf(
                "Preheat oven to 375°F (190°C). Whisk eggs, milk, salt, pepper, and half the parmesan cheese in a bowl.",
                "Melt butter in an oven-safe skillet over medium heat. Sauté garlic and spinach until wilted (about 2 minutes).",
                "Pour whisked egg mixture into the skillet. Scatter torn fresh basil on top and let edges set for 2 minutes.",
                "Sprinkle remaining parmesan on top and transfer the skillet to the oven.",
                "Bake for 10-12 minutes until center is puffed and golden. Slice into wedges."
            ),
            chefTips = listOf(
                "Adding a splash of whole milk prevents the eggs from becoming rubbery when baked.",
                "Can be refrigerated and enjoyed cold or warm for up to 3 days."
            ),
            tags = listOf("Keto", "High-Protein", "Meal-Prep Friendly", "Gluten-Free")
        ),
        Recipe(
            id = "rec_5",
            title = "Fiesta Black Bean & Avocado Bowl",
            description = "Seasoned warm black beans over fragrant jasmine rice, topped with ripe avocado, blistered tomatoes, cilantro, and lemon dressing.",
            prepTimeMinutes = 10,
            cookTimeMinutes = 15,
            servings = 2,
            difficulty = "Easy",
            calories = 380,
            cuisine = "Mexican-Fusion",
            emoji = "🥗",
            ingredients = listOf(
                RecipeIngredient("Organic Black Beans", "1 can (15 oz)"),
                RecipeIngredient("Organic Jasmine Rice", "1 cup cooked"),
                RecipeIngredient("Organic Hass Avocados", "1 diced"),
                RecipeIngredient("San Marzano Cherry Tomatoes", "1 cup halved"),
                RecipeIngredient("Fresh Eureka Lemons", "1 lemon"),
                RecipeIngredient("Cold-Pressed Extra Virgin Olive Oil", "2 tbsp"),
                RecipeIngredient("California Heirloom Garlic", "2 cloves minced")
            ),
            instructions = listOf(
                "Cook jasmine rice according to package directions.",
                "In a saucepan, heat 1 tbsp olive oil, minced garlic, and black beans with their juice. Simmer on low for 8 minutes until thickened.",
                "In a small bowl, toss cherry tomatoes with remaining olive oil, lemon juice, salt, and pepper.",
                "Assemble bowls: layer fragrant rice, ladle warm seasoned black beans, top with diced avocado and marinated tomatoes.",
                "Drizzle extra lemon dressing and serve warm."
            ),
            chefTips = listOf(
                "Simmering black beans with garlic creates a velvety natural sauce.",
                "Add a pinch of smoked paprika or cumin for an authentic Mexican flair."
            ),
            tags = listOf("Vegan", "Plant-Based", "High-Fiber", "Gluten-Free")
        )
    )

    fun calculatePantryMatch(recipe: Recipe, pantryItems: List<PantryItem>): Recipe {
        val pantryItemNames = pantryItems.filter { it.quantity > 0 }.map { it.name.lowercase() }

        var matchedCount = 0
        val updatedIngredients = recipe.ingredients.map { ingredient ->
            val isMatched = pantryItemNames.any { pantryName ->
                isIngredientMatch(ingredient.name.lowercase(), pantryName)
            }
            if (isMatched) matchedCount++
            ingredient.copy(isAvailableInPantry = isMatched)
        }

        val percentage = if (recipe.ingredients.isNotEmpty()) {
            ((matchedCount.toDouble() / recipe.ingredients.size.toDouble()) * 100).toInt()
        } else 0

        return recipe.copy(
            ingredients = updatedIngredients,
            matchPercentage = percentage
        )
    }

    private fun isIngredientMatch(ingredientName: String, pantryName: String): Boolean {
        val ingWords = ingredientName.split(" ", "(", ")", ",", "-").filter { it.length > 2 }
        val pntWords = pantryName.split(" ", "(", ")", ",", "-").filter { it.length > 2 }

        val directContains = pantryName.contains(ingredientName) || ingredientName.contains(pantryName)
        if (directContains) return true

        val overlap = ingWords.any { word ->
            pntWords.contains(word) && !listOf("organic", "fresh", "count", "pack", "whole", "large", "style").contains(word)
        }
        return overlap
    }

    suspend fun generateRecipeWithGemini(
        pantryItems: List<PantryItem>,
        mealType: String = "Dinner",
        dietary: String = "Any",
        maxTimeMinutes: Int = 30,
        customPrompt: String = ""
    ): Recipe? = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        val inventoryNames = pantryItems.filter { it.quantity > 0 }.joinToString(", ") { "${it.name} (${it.quantity} ${it.unit})" }

        val prompt = """
            You are an expert master chef. Create a delicious, realistic gourmet recipe using the user's available pantry ingredients as the primary components.
            
            USER'S CURRENT PANTRY INVENTORY:
            $inventoryNames
            
            USER PREFERENCES:
            - Meal Type: $mealType
            - Dietary Preference: $dietary
            - Maximum Cooking Time: $maxTimeMinutes minutes
            ${if (customPrompt.isNotBlank()) "- Special Request: $customPrompt" else ""}
            
            Return ONLY a valid JSON object matching this exact schema:
            {
              "title": "Recipe Title",
              "description": "Short mouthwatering description (1-2 sentences)",
              "prepTimeMinutes": 10,
              "cookTimeMinutes": 20,
              "servings": 2,
              "difficulty": "Easy",
              "calories": 450,
              "cuisine": "Mediterranean",
              "emoji": "🍲",
              "ingredients": [
                {"name": "Ingredient Name", "amount": "1 cup", "isOptional": false}
              ],
              "instructions": [
                "Step 1 instruction...",
                "Step 2 instruction..."
              ],
              "chefTips": [
                "Tip 1...",
                "Tip 2..."
              ],
              "tags": ["High-Protein", "Pantry-Ready"]
            }
        """.trimIndent()

        if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
            // Fallback generation if key not configured
            return@withContext defaultRecipes.firstOrNull()?.copy(
                id = "ai_${System.currentTimeMillis()}",
                title = "Chef's Pantry Special: $mealType",
                isAiGenerated = true
            )
        }

        try {
            val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent?key=$apiKey"
            val requestBodyJson = JSONObject().apply {
                put("contents", JSONArray().apply {
                    put(JSONObject().apply {
                        put("parts", JSONArray().apply {
                            put(JSONObject().apply {
                                put("text", prompt)
                            })
                        })
                    })
                })
                put("generationConfig", JSONObject().apply {
                    put("responseMimeType", "application/json")
                    put("temperature", 0.7)
                })
            }

            val request = Request.Builder()
                .url(url)
                .post(requestBodyJson.toString().toRequestBody("application/json".toMediaType()))
                .build()

            val response = okHttpClient.newCall(request).execute()
            if (!response.isSuccessful) {
                return@withContext null
            }

            val responseText = response.body?.string() ?: return@withContext null
            val rootJson = JSONObject(responseText)
            val candidates = rootJson.optJSONArray("candidates") ?: return@withContext null
            val firstCandidate = candidates.optJSONObject(0) ?: return@withContext null
            val content = firstCandidate.optJSONObject("content") ?: return@withContext null
            val parts = content.optJSONArray("parts") ?: return@withContext null
            val text = parts.optJSONObject(0)?.optString("text") ?: return@withContext null

            val recipeJson = JSONObject(text)
            val ingredientsList = mutableListOf<RecipeIngredient>()
            val ingArray = recipeJson.optJSONArray("ingredients")
            if (ingArray != null) {
                for (i in 0 until ingArray.length()) {
                    val obj = ingArray.getJSONObject(i)
                    ingredientsList.add(
                        RecipeIngredient(
                            name = obj.getString("name"),
                            amount = obj.optString("amount", "As needed"),
                            isOptional = obj.optBoolean("isOptional", false)
                        )
                    )
                }
            }

            val instructionsList = mutableListOf<String>()
            val instArray = recipeJson.optJSONArray("instructions")
            if (instArray != null) {
                for (i in 0 until instArray.length()) {
                    instructionsList.add(instArray.getString(i))
                }
            }

            val tipsList = mutableListOf<String>()
            val tipsArray = recipeJson.optJSONArray("chefTips")
            if (tipsArray != null) {
                for (i in 0 until tipsArray.length()) {
                    tipsList.add(tipsArray.getString(i))
                }
            }

            val tagsList = mutableListOf<String>()
            val tagsArray = recipeJson.optJSONArray("tags")
            if (tagsArray != null) {
                for (i in 0 until tagsArray.length()) {
                    tagsList.add(tagsArray.getString(i))
                }
            }

            Recipe(
                id = "ai_${System.currentTimeMillis()}",
                title = recipeJson.optString("title", "Custom Pantry Creation"),
                description = recipeJson.optString("description", "A custom recipe tailored for your ingredients."),
                prepTimeMinutes = recipeJson.optInt("prepTimeMinutes", 10),
                cookTimeMinutes = recipeJson.optInt("cookTimeMinutes", 20),
                servings = recipeJson.optInt("servings", 2),
                difficulty = recipeJson.optString("difficulty", "Easy"),
                calories = recipeJson.optInt("calories", 400),
                cuisine = recipeJson.optString("cuisine", "Chef Special"),
                emoji = recipeJson.optString("emoji", "🍲"),
                ingredients = ingredientsList,
                instructions = instructionsList,
                chefTips = tipsList,
                tags = tagsList,
                isAiGenerated = true
            )
        } catch (e: Exception) {
            null
        }
    }
}
