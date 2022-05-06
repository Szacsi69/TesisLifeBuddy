package hu.bme.aut.android.tesislifebuddy.network

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

object NutritionInteractor {
    private val nutritionApi: NutritionAPI;

    private const val BASE_URL = "https://trackapi.nutritionix.com"
    private const val API_ID = "f38e5c42"
    private const val API_KEY = "08ffc8586ac757118061986a1b9c0a86"

    const val CALORIE_ATTR_ID = 208
    const val PROTEIN_ATTR_ID = 203

    init {
        val moshi = Moshi.Builder()
            .addLast(KotlinJsonAdapterFactory())
            .build()
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()

        this.nutritionApi = retrofit.create(NutritionAPI::class.java)
    }

    fun getNutritionixFoods(foodName: String): Call<NutritionixFoods> {
        return nutritionApi.getNutritionixFoods(API_ID, API_KEY, foodName)
    }
}