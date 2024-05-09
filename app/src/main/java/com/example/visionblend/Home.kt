package com.example.visionblend

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide.init
import com.example.visionblend.Adapter.MyProductAdapter
import com.example.visionblend.Interface.ProductLoadInterface
import com.example.visionblend.Model.ProductModel
import com.example.visionblend.utils.SpacesItemDecoration
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class Home : AppCompatActivity(), ProductLoadInterface {

    lateinit var productLoadInterface: ProductLoadInterface
    lateinit var recycler_product: RecyclerView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        init()
        loadProductFromFirebase()
    }

    private fun loadProductFromFirebase() {
        val productModels: MutableList<ProductModel> = ArrayList()
        FirebaseDatabase.getInstance().getReference("product")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                @SuppressLint("SuspiciousIndentation")
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        for (productSnapshot in snapshot.children) {
                            val productModel = productSnapshot.getValue(ProductModel::class.java)
                            productModel!!.key = productSnapshot.key
                            productModels.add(productModel)
                        }
                        productLoadInterface.onProductLoadSuccess(productModels)
                    }
                    else
                    productLoadInterface.onProductLoadFailed("Product not found")
                }

                override fun onCancelled(error: DatabaseError) {
                    productLoadInterface.onProductLoadFailed(error.message)
                }
            })
    }

    private fun init() {
    productLoadInterface = this
    recycler_product = findViewById(R.id.recyclerView)

        val gridLayoutManager = GridLayoutManager(this,2)
        recycler_product.layoutManager = gridLayoutManager
        recycler_product.addItemDecoration(SpacesItemDecoration())
}

    override fun onProductLoadSuccess(productModelList: List<ProductModel>?) {
        val adapter = MyProductAdapter(this,productModelList!!)
        recycler_product.adapter = adapter

    }

    override fun onProductLoadFailed(message: String?) {
        val mainLayout: ConstraintLayout = findViewById(R.id.mainlayout)
        Snackbar.make(mainLayout,message!!,Snackbar.LENGTH_LONG).show()
    }

}

