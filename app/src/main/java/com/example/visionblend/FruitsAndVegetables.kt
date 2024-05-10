package com.example.visionblend

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

class FruitsAndVegetables : AppCompatActivity() {


    private val itemList = listOf(
        Item(R.drawable.apple, "Apple", 1.99),
        Item(R.drawable.banana, "Banana", 0.99),
        Item(R.drawable.watermelon, "Watermelon", 3.99),
        Item(R.drawable.orange, "Orange", 2.99),
        Item(R.drawable.mango, "Mango", 2.49),
        Item(R.drawable.pineapple, "Pineapple", 4.99),
        Item(R.drawable.potato, "Potato", 0.79),
        Item(R.drawable.tomato, "Tomato", 1.29),
        Item(R.drawable.onion, "Onion", 0.89),
        Item(R.drawable.carrot, "Carrot", 0.69),
        Item(R.drawable.cucumber, "Cucumber", 1.09),
        Item(R.drawable.cabbage, "Cabbage", 1.59),
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fruits_and_vegetables)

        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        recyclerView.adapter = ItemAdapter(itemList)
        recyclerView.layoutManager = GridLayoutManager(this, 2)
        recyclerView.setHasFixedSize(true)

        val searchView: SearchView = findViewById(R.id.searchView)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                (recyclerView.adapter as ItemAdapter).filter.filter(newText)
                return false
            }
        })
    }
}