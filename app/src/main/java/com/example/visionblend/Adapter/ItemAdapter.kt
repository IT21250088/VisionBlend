package com.example.visionblend

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.util.*
import kotlin.collections.ArrayList

class ItemAdapter(private val itemList: List<Item>) : RecyclerView.Adapter<ItemAdapter.ItemViewHolder>(), Filterable {

    var itemListFiltered = ArrayList(itemList)

    class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.item_image)
        val textView: TextView = itemView.findViewById(R.id.item_title)
        val priceView: TextView = itemView.findViewById(R.id.item_price) // Add this line
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.activity_item, parent, false)
        return ItemViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val currentItem = itemListFiltered[position]
        holder.imageView.setImageResource(currentItem.imageResource)
        holder.textView.text = currentItem.title
        holder.priceView.text = currentItem.price.toString() // Add this line
    }

    override fun getItemCount() = itemListFiltered.size

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(charSequence: CharSequence): FilterResults {
                val charString = charSequence.toString()
                itemListFiltered = if (charString.isEmpty()) {
                    ArrayList(itemList)
                } else {
                    val filteredList = ArrayList<Item>()
                    for (row in itemList) {
                        if (row.title.toLowerCase(Locale.ROOT).contains(charString.toLowerCase(Locale.ROOT))) {
                            filteredList.add(row)
                        }
                    }
                    filteredList
                }
                val filterResults = FilterResults()
                filterResults.values = itemListFiltered
                return filterResults
            }

            override fun publishResults(charSequence: CharSequence, filterResults: FilterResults) {
                itemListFiltered = filterResults.values as ArrayList<Item>
                notifyDataSetChanged()
            }
        }
    }
}