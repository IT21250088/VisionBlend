package com.example.visionblend

import com.example.visionblend.Adapter.GroceryAdapter
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

class Categories : AppCompatActivity() {

    private val groceryList = listOf(
        GroceryItem(R.drawable.fruits_and_vegetables, "Fruits and Vegetables"),
        GroceryItem(R.drawable.beverages, "Beverages"),
        GroceryItem(R.drawable.snacks, "Snacks"),
        GroceryItem(R.drawable.care_products, "Care Products"),
        GroceryItem(R.drawable.meat_and_fish, "Meat and Fish"),
        GroceryItem(R.drawable.diary_products, "Diary Products"),
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        // Retrieve the theme from shared preferences
        val sharedPref = getSharedPreferences("ThemePref", MODE_PRIVATE)
        val themeId = sharedPref.getInt("themeId", R.style.Theme_VisionBlend)
        // Set the theme
        setTheme(themeId)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_categories)

        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        recyclerView.adapter = GroceryAdapter(groceryList)
        recyclerView.layoutManager = GridLayoutManager(this, 2)
        recyclerView.setHasFixedSize(true)
    }
}