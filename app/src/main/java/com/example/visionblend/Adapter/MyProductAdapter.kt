package com.example.visionblend.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.visionblend.Model.ProductModel
import kotlinx.coroutines.InternalCoroutinesApi
import com.bumptech.glide.Glide
import com.example.visionblend.Home

class MyProductAdapter(
    private val context: Home,
    private val list: List<ProductModel>
): RecyclerView.Adapter<MyProductAdapter.MyProductViewHolder>() {

        class MyProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

            var imageView: ImageView? = null
            var textName: TextView? = null
            var textPrice: TextView? = null

            init {
                imageView = itemView.findViewById<View>(com.example.visionblend.R.id.ivImage) as ImageView
                textName = itemView.findViewById<View>(com.example.visionblend.R.id.Name1) as TextView
                textPrice = itemView.findViewById<View>(com.example.visionblend.R.id.Price) as TextView
            }

        }

    @OptIn(InternalCoroutinesApi::class)
    override fun onCreateViewHolder(parent: ViewGroup,viewType: Int): MyProductViewHolder {
        return MyProductViewHolder(LayoutInflater.from(context).inflate(com.example.visionblend.R.layout.recyclecardview, parent, false))
    }
    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: MyProductViewHolder, position: Int){
        Glide.with(context)
            .load(list[position].image)
            .into(holder.imageView!!)
        holder.textName!!.text = StringBuilder().append(list[position].name)
        holder.textPrice!!.text = StringBuilder().append(list[position].price)

    }

}