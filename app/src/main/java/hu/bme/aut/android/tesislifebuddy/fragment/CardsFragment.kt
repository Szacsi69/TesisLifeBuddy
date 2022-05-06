package hu.bme.aut.android.tesislifebuddy.fragment

import android.content.Context
import android.os.Bundle
import android.view.*
import android.view.animation.AnimationUtils
import android.view.animation.AnimationUtils.loadLayoutAnimation
import android.view.animation.LayoutAnimationController
import androidx.appcompat.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import hu.bme.aut.android.tesislifebuddy.FoodAddedListener
import hu.bme.aut.android.tesislifebuddy.dialog.NewCardFragment
import hu.bme.aut.android.tesislifebuddy.R
import hu.bme.aut.android.tesislifebuddy.TesiApplication
import hu.bme.aut.android.tesislifebuddy.adapter.FoodListAdapter
import hu.bme.aut.android.tesislifebuddy.data.FoodItem
import hu.bme.aut.android.tesislifebuddy.databinding.FragmentCardsBinding
import hu.bme.aut.android.tesislifebuddy.dialog.AddToCountersFragment
import hu.bme.aut.android.tesislifebuddy.dialog.ItemSearchFragment
import kotlin.concurrent.thread
import kotlin.random.Random

class CardsFragment: Fragment(), NewCardFragment.NewCardCreatedListener, ItemSearchFragment.ItemSearchedListener, FoodListAdapter.FoodItemInteractionListener {
    private lateinit var binding: FragmentCardsBinding

    private lateinit var listener: FoodAddedListener
    private lateinit var adapter: FoodListAdapter

    companion object {
        const val FOOD_NAME_MAX_LENGTH = 15
        const val FOOD_QUANTITY_MAX_LENGTH = 28
        const val FOOD_BRAND_MAX_LENGTH = 32
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        try {
            listener = activity as FoodAddedListener
        } catch (e: ClassCastException) {
            throw RuntimeException(e)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstnceState: Bundle?): View? {
        binding = FragmentCardsBinding.inflate(inflater, container, false)
        setHasOptionsMenu(true)
        initRecyclerView()

        return binding.root
    }

    private fun initRecyclerView() {
        adapter = FoodListAdapter(this, context)
        binding.rvCards.layoutManager = LinearLayoutManager(activity)

        adapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onItemRangeMoved(fromPosition: Int, toPosition: Int, itemCount: Int) {
                super.onItemRangeMoved(fromPosition, toPosition, itemCount)
                val layoutManager: LinearLayoutManager = binding.rvCards.layoutManager as LinearLayoutManager
                layoutManager.scrollToPositionWithOffset(0, 0)

            }
        })
        binding.rvCards.adapter = adapter
    }

    override fun onResume() {
        super.onResume()
        loadItemsInBackground()
    }

    private fun loadItemsInBackground() {
        thread {
            val items = TesiApplication.database.foodItemDao().getAll()
            activity?.runOnUiThread {
                adapter.update(items)
            }
        }
    }

    override fun onAddBtnClicked(calories: Float, protein: Float) {
        listener.onFoodAdded(calories, protein)
    }

    override fun onItemLongClick(foodItem: FoodItem, view: View): Boolean {
        val popup = PopupMenu(requireActivity(), view)
        popup.inflate(R.menu.menu_card_item)
        popup.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.delete_card -> onCardDeleted(foodItem)
            }
            false
        }

        popup.show()
        return false
    }

    override fun onNewCardCreated(name: String, quantity: String, calorie: Float, protein: Float, brand: String) {
        val foodItem = FoodItem(Random.nextLong(), name, quantity, calorie, protein, brand)
        adapter.addItem(foodItem)
        thread {
            TesiApplication.database.foodItemDao().insert(foodItem)
        }
    }

    private fun onCardDeleted(foodItem: FoodItem) {
        adapter.removeItem(foodItem)
        thread {
            TesiApplication.database.foodItemDao().deleteItem(foodItem)
        }
    }

    override fun onItemSearched(name: String) {
        adapter.popSearchedItems(name)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_cards,menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.new_card -> {
                val newCardFragment = NewCardFragment()
                newCardFragment.setTargetFragment(this,1)
                fragmentManager?.let { newCardFragment.show(it, "TAG") }
                true
            }
            R.id.add_to_counters -> {
                val addToCountersFragment = AddToCountersFragment()
                fragmentManager?.let { addToCountersFragment.show(it, "TAG") }
                true
            }
            R.id.search_item -> {
                val itemSearchFragment = ItemSearchFragment()
                itemSearchFragment.setTargetFragment(this, 1)
                fragmentManager?.let {itemSearchFragment.show(it, "TAG")}
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}