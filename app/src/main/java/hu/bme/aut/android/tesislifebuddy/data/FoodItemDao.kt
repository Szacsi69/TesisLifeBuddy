package hu.bme.aut.android.tesislifebuddy.data

import androidx.room.*

@Dao
interface FoodItemDao {

    @Query("SELECT * FROM fooditem")
    fun getAll(): List<FoodItem>

    @Insert
    fun insert(foodItem: FoodItem): Long

    @Delete
    fun deleteItem(foodItem: FoodItem)
}