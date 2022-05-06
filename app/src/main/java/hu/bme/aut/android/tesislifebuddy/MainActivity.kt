package hu.bme.aut.android.tesislifebuddy

import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.content.SharedPreferences
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import androidx.lifecycle.Observer
import hu.bme.aut.android.tesislifebuddy.databinding.ActivityMainBinding
import hu.bme.aut.android.tesislifebuddy.dialog.SetMaxValuesFragment
import hu.bme.aut.android.tesislifebuddy.model.BarModel
import hu.bme.aut.android.tesislifebuddy.service.NotificationService
import hu.bme.aut.android.tesislifebuddy.view.BarView

class MainActivity : AppCompatActivity(), SetMaxValuesFragment.MaxValuesChangedListener {

    private lateinit var binding : ActivityMainBinding

    private var cBar: BarModel = BarModel(0, 0F)
    private var pBar: BarModel = BarModel(0, 0F)

    private var notificationServiceBinder: NotificationService.ServiceNotificationBinder? = null
    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(componentName: ComponentName, binder: IBinder) {
            notificationServiceBinder = binder as NotificationService.ServiceNotificationBinder
        }

        override fun onServiceDisconnected(componentName: ComponentName) {
            notificationServiceBinder = null
        }
    }

    companion object {
        var serviceStarted: Boolean = false


        const val PREF_BAR_VALUES = "BAR_VALUES"
        const val KEY_MAX_CALORIE_VALUE = "max_calorie"
        const val KEY_CURRENT_CALORIE_VALUE = "current_calorie"
        const val KEY_MAX_PROTEIN_VALUE = "max_protein"
        const val KEY_CURRENT_PROTEIN_VALUE = "current_protein"

        const val PREF_START_SERVICE = "START_SERVICE"
        const val KEY_START_SERVICE = "service_started"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        loadBarsFromPreferences()

        serviceStarted = getServiceStartedPrefValue()

        binding.calorieBar.setColor(Color.RED)
        setUpBarModelViewConnection(cBar, binding.calorieBar, binding.calorieValue)

        binding.proteinBar.setColor(Color.GREEN)
        setUpBarModelViewConnection(pBar, binding.proteinBar, binding.proteinValue)

        binding.btnFood.setOnClickListener {
            val foodIntent = Intent(this, FoodActivity::class.java)
            startActivity(foodIntent)
        }
        binding.btnNull.setOnClickListener {
            cBar.setCurrentValue(0F)
            pBar.setCurrentValue(0F)
            saveChanges()
        }

        setContentView(binding.root)
    }

    private fun getServiceStartedPrefValue(): Boolean {
        val sp: SharedPreferences = getSharedPreferences(PREF_START_SERVICE, MODE_PRIVATE)
        return sp.getBoolean(KEY_START_SERVICE, false)
    }

    private fun updateServiceStartedPrefValue(newValue: Boolean) {
        val sp: SharedPreferences = getSharedPreferences(PREF_START_SERVICE, MODE_PRIVATE)
        val editor: SharedPreferences.Editor = sp.edit()
        editor.putBoolean(KEY_START_SERVICE, newValue)
        editor.apply()
    }

    private fun loadBarsFromPreferences() {
        val sp: SharedPreferences = getSharedPreferences(PREF_BAR_VALUES, MODE_PRIVATE)
        val maxCalorie = sp.getInt(KEY_MAX_CALORIE_VALUE, 2500)
        val currentCalorie = sp.getFloat(KEY_CURRENT_CALORIE_VALUE, 0F)
        val maxProtein = sp.getInt(KEY_MAX_PROTEIN_VALUE, 130)
        val currentProtein = sp.getFloat(KEY_CURRENT_PROTEIN_VALUE, 0F)

        cBar.setMaxValue(maxCalorie)
        cBar.setCurrentValue(currentCalorie)

        pBar.setMaxValue(maxProtein)
        pBar.setCurrentValue(currentProtein)
    }

    private fun saveChanges() {
        val sp: SharedPreferences = getSharedPreferences(PREF_BAR_VALUES, MODE_PRIVATE)
        val editor: SharedPreferences.Editor = sp.edit()
        editor.putInt(KEY_MAX_CALORIE_VALUE, cBar.getMaxValue())
        editor.putFloat(KEY_CURRENT_CALORIE_VALUE, cBar.getCurrentValue())
        editor.putInt(KEY_MAX_PROTEIN_VALUE, pBar.getMaxValue())
        editor.putFloat(KEY_CURRENT_PROTEIN_VALUE, pBar.getCurrentValue())
        editor.apply()

        if (serviceStarted) {
            val notification = getString(R.string.notification, cBar.currentValue.value, cBar.maxValue.value, pBar.currentValue.value, pBar.maxValue.value)
            notificationServiceBinder?.service?.updateNotification(notification)
        }
    }

    private fun deletePreferences() {
        val sp: SharedPreferences = getSharedPreferences(PREF_BAR_VALUES, MODE_PRIVATE)
        val editor: SharedPreferences.Editor = sp.edit()
        editor.clear()
        editor.apply()
    }

    private fun setUpBarModelViewConnection(bar: BarModel, bview: BarView, textv: TextView) {
        bview.update(bar)
        val observerMax = Observer<Int> { _ ->
            textv.text = getString(R.string.calorie_protein_values, bar.getCurrentValue(), bar.getMaxValue())
            bview.update(bar)
        }
        bar.maxValue.observe(this, observerMax)

        val observerCurrent = Observer<Float> { _ ->
            textv.text = getString(R.string.calorie_protein_values, bar.getCurrentValue(), bar.getMaxValue())
            bview.update(bar)
        }
        bar.currentValue.observe(this, observerCurrent)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.set_max_values -> {
                SetMaxValuesFragment().show(supportFragmentManager, "TAG")
                true
            }
            R.id.notification_on_off -> {
                val notification = getString(R.string.notification, cBar.currentValue.value, cBar.maxValue.value, pBar.currentValue.value, pBar.maxValue.value)
                startOrStopNotificationService(notification)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onMaxValuesChanged(cMax: Int, pMax: Int) {
        cBar.setMaxValue(cMax)
        pBar.setMaxValue(pMax)

        saveChanges()
    }

    private fun startOrStopNotificationService(notification: String) {

        val serviceIntent = Intent(applicationContext, NotificationService::class.java)

        if(!serviceStarted) {
            serviceIntent.putExtra("NOTIFICATION", notification)
            applicationContext.startService(serviceIntent)
            applicationContext.bindService(serviceIntent, serviceConnection, 0)

            updateServiceStartedPrefValue(true)
        }
        else {
            applicationContext.stopService(serviceIntent)
            updateServiceStartedPrefValue(false)
        }
        serviceStarted = getServiceStartedPrefValue()
    }

    override fun onStop() {
        super.onStop()
        if(notificationServiceBinder != null) {
            applicationContext.unbindService(serviceConnection)
        }
    }

    override fun onStart() {
        super.onStart()
        loadBarsFromPreferences()
        if (serviceStarted) {
            val serviceIntent = Intent(applicationContext, NotificationService::class.java)
            applicationContext.bindService(serviceIntent, serviceConnection, 0)
        }
    }
}