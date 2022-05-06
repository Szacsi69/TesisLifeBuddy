package hu.bme.aut.android.tesislifebuddy.network

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface NutritionAPI {

    @GET("/v2/search/instant?detailed=true")
    fun getNutritionixFoods(@Header("x-app-id") apiId: String,
                            @Header("x-app-key") apiKey: String,
                            @Query("query") foodName: String): Call<NutritionixFoods>
}