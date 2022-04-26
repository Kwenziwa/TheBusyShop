package com.ikhokha.techcheck.model.repository

import com.ikhokha.techcheck.model.Product
import com.ikhokha.techcheck.model.data.ProductDAO
import javax.inject.Inject

class DefaultRepository @Inject constructor(private val productDao: ProductDAO): ProductRepository{

    override fun addProduct(product: Product) {
        productDao.addProduct(product)
    }

    override fun updateProduct(product: Product) {
        productDao.updateProduct(product)
    }

    override fun deleteProduct(product: Product) {
        productDao.deleteProduct(product)
    }

    override fun getProductById(productId: String): Product {
        return productDao.getProductById(productId)
    }

    override fun getAllProducts(): List<Product> {
        return productDao.getAllProducts()
    }

    override fun nukeProducts() {
        productDao.nukeProducts()
    }


}