package com.example.visionblend.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.visionblend.GroceryItem
import com.example.visionblend.R

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
    }

    override fun getItemCount() = groceryList.size
}