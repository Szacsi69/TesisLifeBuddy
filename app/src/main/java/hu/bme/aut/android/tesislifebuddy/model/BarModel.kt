package hu.bme.aut.android.tesislifebuddy.model

import androidx.lifecycle.MutableLiveData


class BarModel constructor(max: Int, cur: Float) {

     val maxValue: MutableLiveData<Int> = MutableLiveData<Int>()
     val currentValue: MutableLiveData<Float> = MutableLiveData<Float>()

    init {
        maxValue.value = max
        currentValue.value = cur
    }

    fun setMaxValue(value: Int) {
       maxValue.value = value
    }

    fun setCurrentValue(value: Float) {
        currentValue.value = value
    }

    fun getMaxValue(): Int {
        return maxValue.value ?: -1
    }
    fun getCurrentValue(): Float {
        return currentValue.value ?: 0F
    }

}