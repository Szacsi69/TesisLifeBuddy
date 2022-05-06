package hu.bme.aut.android.tesislifebuddy.network

data class BrandedFood(
    val food_name: String?,
    val serving_unit: String?,
    val brand_name: String?,
    val serving_weight_grams: Double?,
    val full_nutrients: List<Nutrition>)