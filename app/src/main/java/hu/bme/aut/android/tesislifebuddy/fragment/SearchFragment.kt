package hu.bme.aut.android.tesislifebuddy.fragment

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import hu.bme.aut.android.tesislifebuddy.FoodAddedListener
import hu.bme.aut.android.tesislifebuddy.adapter.SearchedFoodListAdapter
import hu.bme.aut.android.tesislifebuddy.data.FoodItem
import hu.bme.aut.android.tesislifebuddy.databinding.FragmentSearchBinding
import hu.bme.aut.android.tesislifebuddy.network.NutritionInteractor
import hu.bme.aut.android.tesislifebuddy.network.NutritionixFoods
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.google.android.material.snackbar.Snackbar
import hu.bme.aut.android.tesislifebuddy.TesiApplication
import java.math.RoundingMode
import java.text.DecimalFormat
import kotlin.concurrent.thread
import kotlin.random.Random

class SearchFragment: Fragment(), SearchedFoodListAdapter.SearchedFoodItemInteractionListener {
    private lateinit var binding: FragmentSearchBinding

    private lateinit var adapter: SearchedFoodListAdapter
    private lateinit var listener: FoodAddedListener

    private var searchedFoods: NutritionixFoods? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)

        try {
            listener = activity as FoodAddedListener
        } catch (e: ClassCastException) {
            throw RuntimeException(e)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstnceState: Bundle?): View? {
        binding = FragmentSearchBinding.inflate(inflater, container, false)
        initRecyclerView()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnSearch.setOnClickListener {
            val imm =
                context?.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(binding.etSearchInput.windowToken, 0)
            binding.etSearchInput.clearFocus()

            if (binding.etSearchInput.text.toString() != "") {
                loadNutritionData(binding.etSearchInput.text.toString())
            }
        }
    }

    private fun initRecyclerView() {
        adapter = SearchedFoodListAdapter(this, context)
        binding.rvSearch.layoutManager = LinearLayoutManager(activity)

        adapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onItemRangeChanged(positionStart: Int, itemCount: Int) {
                super.onItemRangeChanged(positionStart, itemCount)
                val layoutManager: LinearLayoutManager = binding.rvSearch.layoutManager as LinearLayoutManager
                layoutManager.scrollToPositionWithOffset(0, 0)
            }
        })

        binding.rvSearch.adapter = adapter
    }

    private fun updateRecyclerView(items: List<FoodItem>) {
        adapter.update(items)
    }

    private fun onDataGot() {
        val foodItems = convertNutritionixToFoodItems()
        updateRecyclerView(foodItems)
    }

    private fun loadNutritionData(foodName: String) {
        NutritionInteractor.getNutritionixFoods(foodName).enqueue(object: Callback<NutritionixFoods?> {
            override fun onResponse(call: Call<NutritionixFoods?>, response: Response<NutritionixFoods?>) {
                if (response.isSuccessful) {
                    searchedFoods = response.body()
                    onDataGot()
                }
                else {
                    Toast.makeText(context, "Error: " + response.message(), Toast.LENGTH_LONG).show()
                }
            }
            override fun onFailure(call: Call<NutritionixFoods?>, throwable: Throwable) {
                throwable.printStackTrace()
                Toast.makeText(context, "HTTP Request failed.", Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun convertNutritionixToFoodItems(): MutableList<FoodItem> {
        val foodItems: MutableList<FoodItem> = mutableListOf()
        val convertible = searchedFoods
        convertible?.let {
            val df = DecimalFormat("#.##")
            df.roundingMode = RoundingMode.HALF_UP
            for (b in convertible.common) {
                val name = b.food_name
                var quantity: String = "?"
                if (b.serving_unit != null && b.serving_weight_grams != null)
                    quantity = b.serving_unit + " / " + df.format(b.serving_weight_grams) + "g"
                else if (b.serving_unit == null && b.serving_weight_grams != null)
                    quantity = df.format(b.serving_weight_grams) + "g"
                else if (b.serving_unit != null && b.serving_weight_grams == null)
                    quantity = b.serving_unit
                else if (b.serving_unit == null && b.serving_weight_grams == null)
                    quantity = "?"
                var calories: Float = 0F
                var protein: Float = 0F

                var foundCalorie: Boolean = false
                var foundProtein: Boolean = false
                for(f in b.full_nutrients) {
                    if (foundCalorie && foundProtein) break;
                    if (f.attr_id == NutritionInteractor.CALORIE_ATTR_ID) {
                        calories = f.value
                        foundCalorie = true
                    }
                    else if (f.attr_id == NutritionInteractor.PROTEIN_ATTR_ID) {
                        protein = f.value
                        foundProtein = true
                    }
                }

                if(name != null && calories != 0F && protein != 0F)
                    foodItems.add(FoodItem(null, name, quantity, calories, protein, "-"))
            }

            for (b in convertible.branded) {
                val name = b.food_name
                var quantity: String = "?"
                if (b.serving_unit != null && b.serving_weight_grams != null)
                    quantity = b.serving_unit + " / " + df.format(b.serving_weight_grams) + "g"
                else if (b.serving_unit == null && b.serving_weight_grams != null)
                    quantity = df.format(b.serving_weight_grams) + "g"
                else if (b.serving_unit != null && b.serving_weight_grams == null)
                    quantity = b.serving_unit
                else if (b.serving_unit == null && b.serving_weight_grams == null)
                    quantity = "?"
                var calories: Float = 0F
                var protein: Float = 0F

                var foundCalorie: Boolean = false
                var foundProtein: Boolean = false
                for(f in b.full_nutrients) {
                    if (foundCalorie && foundProtein) break;
                    if (f.attr_id == NutritionInteractor.CALORIE_ATTR_ID) {
                        calories = f.value
                        foundCalorie = true
                    }
                    else if (f.attr_id == NutritionInteractor.PROTEIN_ATTR_ID) {
                        protein = f.value
                        foundProtein = true
                    }
                }

                val brand = b.brand_name

                if(name != null && calories != 0F && protein != 0F && brand != null)
                foodItems.add(FoodItem(null, name, quantity, calories, protein, brand))
            }
        }
        return foodItems
    }

    override fun onAddBtnClicked(calories: Float, protein: Float) {
        listener.onFoodAdded(calories, protein)
    }

    override fun onMakeCardBtnClicked(foodItem: FoodItem) {
        if (foodItem.name.length > CardsFragment.FOOD_NAME_MAX_LENGTH) Snackbar.make(binding.root, "Az étel név paramétere túl hosszú, manuálisan vehető csak fel a kártyák közé.", Snackbar.LENGTH_LONG).show()
        else if (foodItem.quantity.length > CardsFragment.FOOD_QUANTITY_MAX_LENGTH) Snackbar.make(binding.root, "Az étel mennyiség paramétere túl hosszú, manuálisan vehető csak fel a kártyák közé.", Snackbar.LENGTH_LONG).show()
        else if (foodItem.brand.length > CardsFragment.FOOD_BRAND_MAX_LENGTH) Snackbar.make(binding.root, "Az étel brand paamétere túl hosszú, manuálisan vehető csak fel a kártyák közé.", Snackbar.LENGTH_LONG).show()
        else {
            val newFoodItem = FoodItem(
                Random.nextLong(),
                foodItem.name,
                foodItem.quantity,
                foodItem.calories,
                foodItem.protein,
                foodItem.brand
            )
            thread {
                TesiApplication.database.foodItemDao().insert(newFoodItem)
            }
        }
    }
}