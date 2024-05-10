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
import com.example.visionblend.Events.UpdateCartEvent
import com.example.visionblend.Home
import com.example.visionblend.Interface.ICartLoadInterface
import com.example.visionblend.Interface.IrecycleClickInterface
import com.example.visionblend.Model.CartModel
import org.greenrobot.eventbus.EventBus
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MyProductAdapter(
    private val context: Home,
    private val list: List<ProductModel>,
    private val cartInterface : ICartLoadInterface
): RecyclerView.Adapter<MyProductAdapter.MyProductViewHolder>() {

        class MyProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
            View.OnClickListener {

            var imageView: ImageView? = null
            var textName: TextView? = null
            var textPrice: TextView? = null

            private var iRecycleClickInterface: IrecycleClickInterface? = null

            fun setClickInterface(iRecycleClickInterface: IrecycleClickInterface){
                this.iRecycleClickInterface = iRecycleClickInterface
            }

            init {
                imageView = itemView.findViewById<View>(com.example.visionblend.R.id.ivImage) as ImageView
                textName = itemView.findViewById<View>(com.example.visionblend.R.id.Name1) as TextView
                textPrice = itemView.findViewById<View>(com.example.visionblend.R.id.Price) as TextView

                itemView.setOnClickListener(this)
            }

            override fun onClick(v: View?) {
                iRecycleClickInterface!!.onItemClickListener(v,adapterPosition)
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

        holder.setClickInterface(object : IrecycleClickInterface {
            override fun onItemClickListener(view: View?, position: Int) {
                addToCart(list[position])
            }
        })

    }

    private fun addToCart(productModel: ProductModel) {
        val userCart = FirebaseDatabase.getInstance().getReference("Cart")
            .child("UNIQUE_USER_ID") // You can use FirebaseAuth to get current user ID

        userCart.child(productModel.key!!)
            .addListenerForSingleValueEvent(object :ValueEventListener{

                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) // If product already in cart, just update quantity
                    {
                        val cartModel = snapshot.getValue(CartModel::class.java)
                        val updateData: MutableMap<String, Any> = HashMap()
                        cartModel!!.quantity = cartModel.quantity + 1;
                        updateData["quantity"] = cartModel.quantity + 1
                        updateData["totalPrice"] = cartModel.price!!.toDouble() * (cartModel.quantity + 1)
                        userCart.child(productModel.key!!).updateChildren(updateData)
                            .addOnSuccessListener {
                                EventBus.getDefault().postSticky(UpdateCartEvent())
                                cartInterface.onCartLoadFailed("Cart Updated successfully")
                            }
                            .addOnFailureListener { e->
                                cartInterface.onCartLoadFailed(e.message)
                            }
                    }
                    else // If product not in cart, add new
                    {
                        val cartModel = CartModel()
                        cartModel.key = productModel.key
                        cartModel.name = productModel.name
                        cartModel.image = productModel.image
                        cartModel.price = productModel.price
                        cartModel.quantity = 1
                        cartModel.totalPrice = productModel.price!!.toDouble()
                        userCart.child(productModel.key!!).setValue(cartModel)
                            .addOnSuccessListener {
                                EventBus.getDefault().postSticky(UpdateCartEvent())
                                cartInterface.onCartLoadFailed("Cart Updated successfully")
                            }
                            .addOnFailureListener { e->
                                cartInterface.onCartLoadFailed(e.message)
                            }
                    }
                }

                override fun onCancelled(p0: DatabaseError) {
                    cartInterface.onCartLoadFailed(p0.message)
                }

            })
    }

}