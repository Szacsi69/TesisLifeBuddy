package hu.bme.aut.android.tesislifebuddy

import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import hu.bme.aut.android.tesislifebuddy.adapter.FoodPageAdapter
import hu.bme.aut.android.tesislifebuddy.databinding.ActivityFoodBinding
import hu.bme.aut.android.tesislifebuddy.service.NotificationService

class FoodActivity : AppCompatActivity(), FoodAddedListener {

    private lateinit var binding: ActivityFoodBinding

    private var notificationServiceBinder: NotificationService.ServiceNotificationBinder? = null
    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(componentName: ComponentName, binder: IBinder) {
            notificationServiceBinder = binder as NotificationService.ServiceNotificationBinder
        }

        override fun onServiceDisconnected(componentName: ComponentName) {
            notificationServiceBinder = null
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFoodBinding.inflate(layoutInflater)
        binding.vpProfile.adapter = FoodPageAdapter(supportFragmentManager)
        setContentView(binding.root)
    }

    override fun onFoodAdded(calories: Float, protein: Float) {
        val sp: SharedPreferences = getSharedPreferences(MainActivity.PREF_BAR_VALUES, AppCompatActivity.MODE_PRIVATE)
        val currentCalorie = sp.getFloat(MainActivity.KEY_CURRENT_CALORIE_VALUE, 0F)
        val currentProtein = sp.getFloat(MainActivity.KEY_CURRENT_PROTEIN_VALUE, 0F)

        val newCalorie = currentCalorie + calories
        val newProtein = currentProtein + protein

        val editor: SharedPreferences.Editor = sp.edit()
        editor.putFloat(MainActivity.KEY_CURRENT_CALORIE_VALUE, newCalorie)
        editor.putFloat(MainActivity.KEY_CURRENT_PROTEIN_VALUE, newProtein)
        editor.apply()

        if (MainActivity.serviceStarted) {
            val maxCalorie = sp.getInt(MainActivity.KEY_MAX_CALORIE_VALUE, 0)
            val maxProtein = sp.getInt(MainActivity.KEY_MAX_PROTEIN_VALUE, 0)

            val notification =
                getString(R.string.notification, newCalorie, maxCalorie, newProtein, maxProtein)
            notificationServiceBinder?.service?.updateNotification(notification)
        }
    }

    override fun onStop() {
        super.onStop()
        if(notificationServiceBinder != null) {
            applicationContext.unbindService(serviceConnection)
        }
    }

    override fun onStart() {
        super.onStart()
        if (MainActivity.serviceStarted) {
            val serviceIntent = Intent(applicationContext, NotificationService::class.java)
            applicationContext.bindService(serviceIntent, serviceConnection, 0)
        }
    }
}