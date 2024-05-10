package com.example.visionblend

import Adapter.GroceryAdapter
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
//
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
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_categories)

        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        recyclerView.adapter = GroceryAdapter(groceryList)
        recyclerView.layoutManager = GridLayoutManager(this, 2)
        recyclerView.setHasFixedSize(true)
    }
}