package hu.bme.aut.android.tesislifebuddy

interface FoodAddedListener {
    fun onFoodAdded(calories: Float, protein: Float)
}