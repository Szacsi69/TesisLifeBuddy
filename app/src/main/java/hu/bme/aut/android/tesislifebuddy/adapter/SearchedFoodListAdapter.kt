package hu.bme.aut.android.tesislifebuddy.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import hu.bme.aut.android.tesislifebuddy.R
import hu.bme.aut.android.tesislifebuddy.data.FoodItem
import hu.bme.aut.android.tesislifebuddy.databinding.ItemSearchedFoodBinding

class SearchedFoodListAdapter(private val listener: SearchedFoodItemInteractionListener, private val ctx: Context?) : ListAdapter<FoodItem, SearchedFoodListAdapter.FoodViewHolder>(itemCallback)/*RecyclerView.Adapter<FoodListAdapter.FoodViewHolder>()*/ {

    private var items = emptyList<FoodItem>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = FoodViewHolder(
        ItemSearchedFoodBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: FoodViewHolder, position: Int) {
        val foodItem = currentList[position]

        holder.binding.foodName.text = foodItem.name
        holder.binding.foodQuantity.text = holder.binding.root.context.getString(R.string.quantity_value, foodItem.quantity)
        holder.binding.calorieValue.text = holder.binding.root.context.getString(R.string.calorie_value, foodItem.calories)
        holder.binding.proteinValue.text = holder.binding.root.context.getString(R.string.protein_value, foodItem.protein)
        holder.binding.brandName.text = holder.binding.root.context.getString(R.string.brand_value, foodItem.brand)

        holder.binding.btnAdd.setOnClickListener {
            listener.onAddBtnClicked(foodItem.calories, foodItem.protein)
        }
        holder.binding.btnMakeCard.setOnClickListener {
            listener.onMakeCardBtnClicked(foodItem)
        }
        if (ctx != null) {
            val animation = AnimationUtils.loadAnimation(ctx, R.anim.item_fall_down)
            holder.binding.root.startAnimation(animation)
        }
    }

    fun update(foodItems: List<FoodItem>) {
        items = foodItems
        submitList(items)
    }

    interface SearchedFoodItemInteractionListener {
        fun onAddBtnClicked(calories: Float, protein: Float)
        fun onMakeCardBtnClicked(foodItem: FoodItem)
    }

    inner class FoodViewHolder(val binding: ItemSearchedFoodBinding) : RecyclerView.ViewHolder(binding.root)

    companion object{
        object itemCallback : DiffUtil.ItemCallback<FoodItem>(){
            override fun areItemsTheSame(oldItem: FoodItem, newItem: FoodItem): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: FoodItem, newItem: FoodItem): Boolean {
                return oldItem == newItem
            }
        }
    }
}