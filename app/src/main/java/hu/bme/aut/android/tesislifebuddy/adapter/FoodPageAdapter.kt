package hu.bme.aut.android.tesislifebuddy.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import hu.bme.aut.android.tesislifebuddy.fragment.CardsFragment
import hu.bme.aut.android.tesislifebuddy.fragment.SearchFragment

class FoodPageAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
    override fun getItem(position: Int): Fragment = when(position) {
        0 -> CardsFragment()
        1 -> SearchFragment()
        else -> CardsFragment()
    }

    override fun getCount(): Int = NUM_PAGES

    companion object {
        const val NUM_PAGES = 2
    }
}