package com.ikhokha.techcheck.viewModel

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ikhokha.techcheck.model.Product
import com.ikhokha.techcheck.model.repository.ProductRepository

class BarcodeViewModel @ViewModelInject constructor(
    private val databaseRepository: ProductRepository
):ViewModel() {

    private val itemCounter = MutableLiveData<Int>()
    private val unrecognizedItem = MutableLiveData<Boolean>()
    private var product: Product? = null

    fun initViewModel(){
        setItemCounter(
            databaseRepository.getAllProducts().filter { it.inBasket }.run {
                var counter = 0
                forEach {
                    counter += it.quantity!!
                }
                counter
            }
        )
    }

    fun getItemCounter(): LiveData<Int>{
        return itemCounter
    }

    private fun setItemCounter(currentItemCounter: Int){
        itemCounter.value = currentItemCounter
    }

    fun itemUnrecognised(): LiveData<Boolean>{
        return unrecognizedItem
    }

    private fun setItemUnrecognised(recognised: Boolean){
        unrecognizedItem.value = recognised
    }

    fun onItemScanned(productKey: String){
        try {
            product = databaseRepository.getProductById(productKey)
            updateOrAdd()
        }catch (ex: NullPointerException){
            setItemUnrecognised(true)
        }

    }

    private fun updateOrAdd(){
        try {
            if (productExist()) {
                incrementItemInCart()
            } else {
                addNewItemToCart()
            }

            incrementItemCounter()
            setItemUnrecognised(false)
        }catch (ex: NullPointerException){
            setItemUnrecognised(true)
        }
    }

    private fun productExist() = product!!.inBasket

    private fun incrementItemInCart(){
        product!!.quantity = product!!.quantity!!+1
        databaseRepository.updateProduct(product!!)
    }

    private fun addNewItemToCart(){
        product?.apply {
            inBasket = true
            quantity = 1
        }
        databaseRepository.updateProduct(product!!)
    }

    private fun incrementItemCounter(){
        itemCounter.value = itemCounter.value!!+1
    }


}