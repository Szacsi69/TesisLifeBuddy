package hu.bme.aut.android.tesislifebuddy.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "fooditem")
data class FoodItem(
    @ColumnInfo(name = "id") @PrimaryKey(autoGenerate = true) val id: Long? = null,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "quantity") val quantity: String,
    @ColumnInfo(name = "calories") val calories: Float,
    @ColumnInfo(name = "protein") val protein: Float,
    @ColumnInfo(name = "brand") val brand: String
)