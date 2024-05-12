package com.example.visionblend.Adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.visionblend.Beverages
import com.example.visionblend.CareProducts
import com.example.visionblend.DiaryProducts
import com.example.visionblend.FruitsAndVegetables
import com.example.visionblend.GroceryItem
import com.example.visionblend.MeatAndFish
import com.example.visionblend.R
import com.example.visionblend.Snacks


class GroceryAdapter(private val groceryList: List<GroceryItem>) : RecyclerView.Adapter<GroceryAdapter.GroceryViewHolder>() {

    class GroceryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.imageView)
        val textView1: TextView = itemView.findViewById(R.id.textView1)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroceryViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.category_items, parent, false)
        return GroceryViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: GroceryViewHolder, position: Int) {
        val currentItem = groceryList[position]
        holder.imageView.setImageResource(currentItem.image)
        holder.textView1.text = currentItem.title

        holder.itemView.setOnClickListener {
            val context = it.context
            when (currentItem.title) {
                "Fruits and Vegetables" -> context.startActivity(Intent(context, FruitsAndVegetables::class.java))
                "Beverages" -> context.startActivity(Intent(context, Beverages::class.java))
                "Snacks" -> context.startActivity(Intent(context, Snacks::class.java))
                "Care Products" -> context.startActivity(Intent(context, CareProducts::class.java))
                "Meat and Fish" -> context.startActivity(Intent(context, MeatAndFish::class.java))
                "Diary Products" -> context.startActivity(Intent(context, DiaryProducts::class.java))
            }
        }
    }

    override fun getItemCount() = groceryList.size
}