package com.ikhokha.techcheck.model.repository

import com.ikhokha.techcheck.model.Product

interface ProductRepository {

    fun addProduct(product: Product)

    fun updateProduct(product: Product)

    fun deleteProduct(product: Product)

    fun getProductById(productId: String): Product

    fun getAllProducts(): List<Product>

    fun nukeProducts()
}