package com.ikhokha.techcheck.viewModel

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import com.ikhokha.techcheck.model.repository.ProductRepository
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class ReceiptViewModel @ViewModelInject constructor(
    private val dataRepository: ProductRepository
): ViewModel() {

    fun getProducts() = dataRepository.getAllProducts().filter { it.inBasket }


    @RequiresApi(Build.VERSION_CODES.O)
    fun getDateAndTime(): String{
        return DateTimeFormatter.ofPattern("yyyy-MM-dd 'at' HH:mm:ss")
            .format(LocalDateTime.now())
            .toString()
    }

    fun calculateTotal(): Double{
        return dataRepository.getAllProducts()
            .filter { it.inBasket }
            .sumOf { (it.price!! * it.quantity!!) }

    }

    fun cleanUpData(){
        dataRepository.getAllProducts().filter { it.inBasket }.forEach { product->
            product.quantity = 0
            product.inBasket = false
            dataRepository.updateProduct(product)
        }
    }
}