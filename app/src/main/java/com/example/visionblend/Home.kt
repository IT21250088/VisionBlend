package com.example.visionblend


import android.content.Intent
import android.os.Bundle
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.visionblend.Adapter.MyProductAdapter
import com.example.visionblend.Events.UpdateCartEvent
import com.example.visionblend.Interface.ICartLoadInterface
import com.example.visionblend.Interface.ProductLoadInterface
import com.example.visionblend.Model.CartModel
import com.example.visionblend.Model.ProductModel
import com.example.visionblend.utils.SpacesItemDecoration
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode



class Home : AppCompatActivity(), ProductLoadInterface,ICartLoadInterface{

    private lateinit var productLoadInterface: ProductLoadInterface
    private lateinit var cartInterface: ICartLoadInterface
    private lateinit var recyclerProduct: RecyclerView



    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        super.onStop()
        if (EventBus.getDefault().hasSubscriberForEvent(UpdateCartEvent::class.java))
            EventBus.getDefault().removeStickyEvent(UpdateCartEvent::class.java)
        EventBus.getDefault().unregister(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        init()
        loadProductFromFirebase()
        countCartFromFirebase()


    }


    private fun countCartFromFirebase() {
        val cartModels: MutableList<CartModel> = ArrayList()
        FirebaseDatabase.getInstance().getReference("Cart")
            .child("UNIQUE_USER_ID")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        for (cartSnapshot in snapshot.children) {
                            val cartModel = cartSnapshot.getValue(CartModel::class.java)
                            cartModel?.let {
                                it.key = cartSnapshot.key
                                cartModels.add(it)
                            }
                        }
                        cartInterface.onCartLoadSuccess(cartModels)
                    } else {
                        cartInterface.onCartLoadFailed("Cart Empty")
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    cartInterface.onCartLoadFailed(error.message)
                }
            })
    }

    private fun loadProductFromFirebase() {
        val productModels: MutableList<ProductModel> = ArrayList()
        FirebaseDatabase.getInstance().getReference("product")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        for (productSnapshot in snapshot.children) {
                            val productModel = productSnapshot.getValue(ProductModel::class.java)
                            productModel?.let {
                                it.key = productSnapshot.key
                                productModels.add(it)
                            }
                        }
                        productLoadInterface.onProductLoadSuccess(productModels)
                    } else {
                        productLoadInterface.onProductLoadFailed("Product not found")
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    productLoadInterface.onProductLoadFailed(error.message)
                }
            })
    }

    private fun init() {
        productLoadInterface = this
        cartInterface = this
        recyclerProduct = findViewById(R.id.recyclerView)

        val gridLayoutManager = GridLayoutManager(this, 2)
        recyclerProduct.layoutManager = gridLayoutManager
        recyclerProduct.addItemDecoration(SpacesItemDecoration())

        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottom_navigation)
        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    val intent = Intent(this, Home::class.java)
                    startActivity(intent)
                    true
                }
                R.id.navigation_category -> {
                    val intent = Intent(this, Categories::class.java)
                    startActivity(intent)
                    true
                }
                R.id.navigation_cart -> {
                    val intent = Intent(this, Cart::class.java)
                    startActivity(intent)
                    true
                }

                R.id.navigation_profile -> {
                    val intent = Intent(this, viewprofile::class.java)
                    startActivity(intent)
                    true
                }
                else -> false
            }
        }
    }

    override fun onProductLoadSuccess(productModelList: List<ProductModel>?) {
        val adapter = MyProductAdapter(this, productModelList!!, cartInterface)
        recyclerProduct.adapter = adapter
    }

    override fun onProductLoadFailed(message: String?) {
        val mainLayout: ConstraintLayout = findViewById(R.id.activity_home)
        Snackbar.make(mainLayout, message!!, Snackbar.LENGTH_LONG).show()
    }

    override fun onCartLoadSuccess(cartModelList: List<CartModel>?) {
        var cartSum = 0.0
        for (cartModel in cartModelList!!)
            cartSum += cartModel.quantity.toDouble()
    }

    override fun onCartLoadFailed(message: String?) {
        val mainLayout: ConstraintLayout = findViewById(R.id.activity_home)
        Snackbar.make(mainLayout, message!!, Snackbar.LENGTH_LONG).show()
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public fun onUpdateCartEvent(event: UpdateCartEvent) {
        countCartFromFirebase()
    }






}

