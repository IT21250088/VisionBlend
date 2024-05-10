package com.example.visionblend.Interface

import com.example.visionblend.Model.ProductModel

interface ProductLoadInterface {
    fun onProductLoadSuccess(productModelList: List<ProductModel>?)
    fun onProductLoadFailed(message: String?)
}