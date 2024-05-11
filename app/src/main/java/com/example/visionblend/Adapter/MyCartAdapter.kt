package com.example.visionblend.Adapter

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.compose.ui.geometry.times
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.visionblend.Cart
import com.example.visionblend.Events.UpdateCartEvent
import com.example.visionblend.Interface.IrecycleClickInterface
import com.example.visionblend.Model.CartModel
import com.example.visionblend.R
import com.google.firebase.database.FirebaseDatabase
import org.greenrobot.eventbus.EventBus
import java.text.FieldPosition
import kotlin.time.times

class MyCartAdapter (
    private val context: Context,
    private val cartModelList: MutableList<CartModel>) : RecyclerView.Adapter<MyCartAdapter.MyCartViewHolder>() {
    class MyCartViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var btn_minus: ImageView? = null
        var btn_plus: ImageView? = null
        var btn_delete: ImageView? = null
        var img_product: ImageView? = null
        var txt_product_name: TextView? = null
        var txt_price: TextView? = null
        var txt_quantity: TextView? = null

        init {
            btn_minus = itemView.findViewById(R.id.btnMinus) as ImageView
            btn_plus = itemView.findViewById(R.id.btnPlus) as ImageView
            btn_delete = itemView.findViewById(R.id.btnDelete) as ImageView
            img_product = itemView.findViewById(R.id.iImage1) as ImageView
            txt_product_name = itemView.findViewById(R.id.txtname) as TextView
            txt_price = itemView.findViewById(R.id.txtPrice) as TextView
            txt_quantity = itemView.findViewById(R.id.txtQuantity) as TextView
        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyCartViewHolder {
        return MyCartViewHolder(
            LayoutInflater.from(context)
                .inflate(R.layout.cart_item, parent, false)
        )

    }

    override fun getItemCount(): Int {
        return cartModelList.size
    }

    override fun onBindViewHolder(holder: MyCartViewHolder, position: Int) {
        Glide.with(context)
            .load(cartModelList[position].image)
            .into(holder.img_product!!)
        holder.txt_product_name!!.text = StringBuilder().append(cartModelList[position].name)
        holder.txt_price!!.text = StringBuilder().append(cartModelList[position].price)
        holder.txt_quantity!!.text = StringBuilder().append(cartModelList[position].quantity)

        //Event
        holder.btn_minus!!.setOnClickListener { view -> minusCartItem(holder,cartModelList[position]) }
        holder.btn_plus!!.setOnClickListener { view -> plusCartItem(holder,cartModelList[position]) }
        holder.btn_delete!!.setOnClickListener {
            val dialog = AlertDialog.Builder(context)
                .setTitle("Delete Item")
                .setMessage("Do you want to delete this item?")
                .setNegativeButton("CANCEL"){dialogInterface, _ -> dialogInterface.dismiss()}
                .setPositiveButton("DELETE"){dialogInterface, _ ->
                    deleteCartItem(holder,cartModelList[position])
                    dialogInterface.dismiss()

                    notifyItemRemoved(position)
                    FirebaseDatabase.getInstance().getReference("Cart")
                        .child("UNIQUE_USER_ID") // You can use FirebaseAuth to get current user ID
                        .child(cartModelList[position].key!!)
                        .removeValue()
                        .addOnSuccessListener { EventBus.getDefault().postSticky(UpdateCartEvent()) }
                }
                .create()
            dialog.show()
        }

    }

    private fun deleteCartItem(holder: MyCartViewHolder, cartModel: CartModel) {
        // Remove the cart item from cartModelList
        val position = cartModelList.indexOf(cartModel)
        cartModelList.removeAt(position)
        notifyItemRemoved(position)

        // Update the Firebase database
        FirebaseDatabase.getInstance().getReference("Cart")
            .child("UNIQUE_USER_ID") // You can use FirebaseAuth to get current user ID
            .child(cartModel.key!!)
            .removeValue()
            .addOnSuccessListener {
                EventBus.getDefault().postSticky(UpdateCartEvent())
                (context as Cart).calculateTotalPrice() // Update the total price in the Cart activity
            }
    }

    private fun plusCartItem(holder: MyCartViewHolder, cartModel: CartModel) {
        cartModel.quantity += 1
        cartModel.totalPrice = cartModel.quantity * cartModel.price!!.toDouble()
        holder.txt_quantity!!.text = StringBuilder("").append(cartModel.quantity)
        updateFirebase(cartModel)
    }


    private fun minusCartItem(holder: MyCartViewHolder, cartModel: CartModel) {
        if (cartModel.quantity > 1) {
            cartModel.quantity -= 1
            cartModel.totalPrice = cartModel.quantity * cartModel.price!!.toDouble()
            holder.txt_quantity!!.text = StringBuilder("").append(cartModel.quantity)
            updateFirebase(cartModel)
        }
    }




    private fun updateFirebase(cartModel: CartModel) {
        FirebaseDatabase.getInstance().getReference("Cart")
            .child("UNIQUE_USER_ID") // You can use FirebaseAuth to get current user ID
            .child(cartModel.key!!)
            .setValue(cartModel)
            .addOnSuccessListener {
                EventBus.getDefault().postSticky(UpdateCartEvent())
                (context as Cart).calculateTotalPrice() // Call calculateTotalPrice after updating Firebase
            }
    }

}








