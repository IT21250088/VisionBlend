package com.example.visionblend

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.visionblend.Adapter.MyCartAdapter
import com.example.visionblend.Interface.ICartLoadInterface
import com.example.visionblend.Model.CartModel
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class Cart : AppCompatActivity(), ICartLoadInterface {

    var cartLoadInterface:ICartLoadInterface?=null
    private lateinit var recyclerCart: RecyclerView
    private lateinit var txtTotal: TextView
    private var cartModelList: MutableList<CartModel> = mutableListOf()
    override fun onCreate(savedInstanceState: Bundle?) {
        // Retrieve the theme from shared preferences
        val sharedPref = getSharedPreferences("ThemePref", MODE_PRIVATE)
        val themeId = sharedPref.getInt("themeId", R.style.Theme_VisionBlend)
        // Set the theme
        setTheme(themeId)

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_cart)
        init()
        lordCartFromFirebase()
    }

    private fun lordCartFromFirebase() {
        val cartModels: MutableList<CartModel> = ArrayList()
        FirebaseDatabase.getInstance().getReference("Cart")
            .child("UNIQUE_USER_ID")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                @SuppressLint("SuspiciousIndentation")
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        for (cartSnapshot in snapshot.children) {
                            val cartModel = cartSnapshot.getValue(CartModel::class.java)
                            cartModel!!.key = cartSnapshot.key
                            cartModels.add(cartModel)
                        }
                        cartLoadInterface!!.onCartLoadSuccess(cartModels)
                    }
                    else
                        cartLoadInterface!!.onCartLoadFailed("Cart Empty")
                }

                override fun onCancelled(error: DatabaseError) {
                    cartLoadInterface!!.onCartLoadFailed(error.message)
                }
            })

    }

    private fun init() {
        cartLoadInterface = this
        txtTotal = findViewById(R.id.txtTotal) // Initialize txtTotal here
        recyclerCart = findViewById(R.id.recycler_cart)

        val layoutManager = LinearLayoutManager(this)
        recyclerCart.layoutManager = layoutManager
        recyclerCart.addItemDecoration(DividerItemDecoration(this, layoutManager.orientation))
    }

    fun calculateTotalPrice() {
        var sum = 0.0
        for (cartModel in cartModelList) {
            sum += cartModel.totalPrice
        }
        txtTotal.text = StringBuilder("Total: $").append(sum)
    }


    override fun onCartLoadSuccess(cartModelList: List<CartModel>?) {
        if (cartModelList != null) {
            this.cartModelList = cartModelList.toMutableList() // Convert to MutableList
            val adapter = MyCartAdapter(this, this.cartModelList)
            recyclerCart.adapter = adapter
            calculateTotalPrice()
        }
    }


    override fun onCartLoadFailed(message: String?) {
        Snackbar.make(findViewById(android.R.id.content),message!!, Snackbar.LENGTH_LONG).show()
    }

}