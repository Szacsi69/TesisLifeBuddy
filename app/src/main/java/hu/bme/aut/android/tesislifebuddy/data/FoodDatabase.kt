package hu.bme.aut.android.tesislifebuddy.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [FoodItem::class], version = 4)
abstract class FoodDatabase : RoomDatabase() {
    abstract fun foodItemDao(): FoodItemDao

    companion object {
        fun getDatabase(applicationContext: Context): FoodDatabase  {
            return Room.databaseBuilder(
                applicationContext,
                FoodDatabase::class.java,
                "food-db"
            ).fallbackToDestructiveMigration().build();
        }
    }
}