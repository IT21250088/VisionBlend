package com.example.visionblend

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

class Snacks : AppCompatActivity() {
//

    private val itemList = listOf(
        Item(R.drawable.bugles, "Bugles", 1.99),
        Item(R.drawable.chex_mix, "Cheetos", 0.99),
        Item(R.drawable.cheez_it, "Cheez-It", 1.99),
        Item(R.drawable.chips, "Chips", 0.99),
        Item(R.drawable.muddy_bites, "Muddy Bites", 2.99),
        Item(R.drawable.nacho_chees, "Nacho Cheese", 0.99),
        Item(R.drawable.harvest_snaps, "Harvest Snaps", 1.99),
        Item(R.drawable.ritz_bites, "Ritz Bites", 1.99),
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