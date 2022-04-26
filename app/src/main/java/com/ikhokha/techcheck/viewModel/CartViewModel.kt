package com.ikhokha.techcheck.viewModel

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import com.ikhokha.techcheck.model.repository.ProductRepository

class CartViewModel @ViewModelInject constructor(
    private val dataRepository: ProductRepository
): ViewModel() {

    fun cartIsEmpty() = dataRepository.getAllProducts().none{ it.inBasket }

    fun getCartProducts() = dataRepository.getAllProducts().filter { it.inBasket }

}