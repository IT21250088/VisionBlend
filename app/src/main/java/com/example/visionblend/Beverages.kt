package com.example.visionblend

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

class Beverages : AppCompatActivity() {

    private val itemList = listOf(
        Item(R.drawable.kizz, "Kizz", 1.99),
        Item(R.drawable.coca_cola, "Coca Cola", 0.99),
        Item(R.drawable.fanta, "Fanta", 0.99),
        Item(R.drawable.fanta2, "Fanta", 0.99),
        Item(R.drawable.mix_fruit_nectar, "Mix Fruit Nectar", 1.99),
        Item(R.drawable.redbull, "RedBull", 2.99),
        Item(R.drawable.sprite, "Sprite", 0.99),
        Item(R.drawable.redbull1, "RedBull", 2.99),
        Item(R.drawable.monster, "Monster", 2.99),

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