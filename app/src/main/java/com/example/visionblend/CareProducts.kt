package com.example.visionblend

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

class CareProducts : AppCompatActivity() {


    private val itemList = listOf(
        Item(R.drawable.aveena, "Aveena", 1.99),
        Item(R.drawable.dove, "Dove", 0.99),
        Item(R.drawable.neotragena, "Neotragena", 3.99),
        Item(R.drawable.liz_earal, "Liz Earal", 2.99),
        Item(R.drawable.olay, "Olay", 2.49),
        Item(R.drawable.olay1, "Olay", 2.49),
        Item(R.drawable.golw_screen, "Glow Screen", 4.99),
        Item(R.drawable.nivea, "Nivea", 1.29),

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