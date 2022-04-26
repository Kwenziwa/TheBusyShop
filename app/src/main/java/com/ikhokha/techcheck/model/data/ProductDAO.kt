package com.ikhokha.techcheck.model.data

import androidx.room.*
import com.ikhokha.techcheck.model.Product

@Dao
interface ProductDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addProduct(product: Product)

    @Query("Select * from product where id =:productId")
    fun getProductById(productId: String): Product

    @Query("Select * from product")
    fun getAllProducts(): List<Product>

    @Update
    fun updateProduct(product: Product)

    @Delete
    fun deleteProduct(product: Product)

    @Query("Delete from product")
    fun nukeProducts()
}