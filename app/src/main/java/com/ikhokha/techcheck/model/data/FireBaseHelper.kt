package com.ikhokha.techcheck.model.data

import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.ikhokha.techcheck.model.Product
import com.ikhokha.techcheck.model.repository.ProductRepository
import com.ikhokha.techcheck.utilits.EFirebaseLoadingStatus
import kotlinx.coroutines.runBlocking

class FireBaseHelper {

    private val firebaseDatabase = FirebaseDatabase.getInstance()
    private val firebaseStorage = FirebaseStorage.getInstance()

    fun getFirebaseProducts(localStorage: ProductRepository, dataLoadingStatus: MutableLiveData<EFirebaseLoadingStatus>){
        firebaseDatabase.getReference("/")
            .get()
            .addOnCompleteListener {
                if(it.isSuccessful){
                    val data = it.result.children
                    for(product in data){
                        product.getValue(Product::class.java)?.let { newProduct->
                            newProduct.id = product.key!!
                            generateImageUrl(localStorage,newProduct)
                        }
                    }
                    dataLoadingStatus.value = EFirebaseLoadingStatus.SUCCESS
                }else{
                    dataLoadingStatus.value = EFirebaseLoadingStatus.FAILURE
                }
            }
    }


    private fun generateImageUrl(localStorage: ProductRepository, product: Product){
        firebaseStorage.getReference("/")
            .child(product.image!!)
            .downloadUrl
            .addOnCompleteListener {
                if(it.isSuccessful){
                    product.imageUrl = it.result.toString()
                    runBlocking {
                        localStorage.addProduct(product)
                    }
                }else{
                    runBlocking {
                        localStorage.addProduct(product)
                    }

                }
            }
    }
}