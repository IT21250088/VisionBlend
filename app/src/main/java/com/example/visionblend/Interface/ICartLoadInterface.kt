package com.example.visionblend.Interface

import com.example.visionblend.Model.CartModel

interface ICartLoadInterface {

    fun onCartLoadSuccess(cartModelList: List<CartModel>?)
    fun onCartLoadFailed(message: String?)
}