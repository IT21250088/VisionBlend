package com.example.visionblend

import Adapter.GroceryAdapter
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.visionblend.GroceryItem
import com.example.visionblend.R

class MainActivity : AppCompatActivity() {

    private val groceryList = listOf(
        GroceryItem(R.drawable.beverages, "Beverages", "Fresh apples"),
        GroceryItem(R.drawable.snacks, "Snacks", "Fresh bananas"),
        GroceryItem(R.drawable.fruits_and_vegetables, "Fruits and Vegetables", "Fresh oranges"),
        GroceryItem(R.drawable.care_products, "Care Products", "Fresh milk"),
        GroceryItem(R.drawable.meat_and_fish, "Meat and Fish", "Fresh water"),
        GroceryItem(R.drawable.diary_products, "Diary Products", "Fresh bread"),
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        recyclerView.adapter = GroceryAdapter(groceryList)
        recyclerView.layoutManager = GridLayoutManager(this, 2)
        recyclerView.setHasFixedSize(true)
    }
}