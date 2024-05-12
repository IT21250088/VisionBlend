package com.example.visionblend

import Item
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

class CareProducts : AppCompatActivity() {

    private val itemList = listOf(
        Item(R.drawable.nivea, "Nivea", 1.99),
        Item(R.drawable.dove, "Dove", 0.99),
        Item((R.drawable.golw_screen), "Glow Screen", 1.99),
        Item(R.drawable.liz_earal, "Liz Earal", 1.99),
        Item(R.drawable.aveena, "Aveena", 1.99),
        Item(R.drawable.olay, "Olay", 1.99),
        Item(R.drawable.olay1, "Olay", 1.99),
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        // Retrieve the theme from shared preferences
        val sharedPref = getSharedPreferences("ThemePref", MODE_PRIVATE)
        val themeId = sharedPref.getInt("themeId", R.style.Theme_VisionBlend)
        // Set the theme
        setTheme(themeId)

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