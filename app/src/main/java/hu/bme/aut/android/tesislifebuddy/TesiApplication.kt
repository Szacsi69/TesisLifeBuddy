package hu.bme.aut.android.tesislifebuddy

import android.app.Application
import hu.bme.aut.android.tesislifebuddy.data.FoodDatabase

class TesiApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        database = FoodDatabase.getDatabase(applicationContext)
    }

    companion object {
        lateinit var database: FoodDatabase
    }
}