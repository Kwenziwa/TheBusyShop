package com.ikhokha.techcheck.viewModel

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ikhokha.techcheck.model.data.FireBaseHelper
import com.ikhokha.techcheck.model.repository.ProductRepository
import com.ikhokha.techcheck.utilits.EFirebaseLoadingStatus

class SplashActivityViewModel @ViewModelInject constructor(
    private val dataRepository: ProductRepository
): ViewModel() {
    private val finishLoadingData = MutableLiveData<EFirebaseLoadingStatus>()

    fun getLoadingStatus():LiveData<EFirebaseLoadingStatus>{
        return finishLoadingData
    }

    fun setProducts(){
        finishLoadingData.value = EFirebaseLoadingStatus.LOADING
        dataRepository.nukeProducts()
        FireBaseHelper().getFirebaseProducts(dataRepository, finishLoadingData)
    }

}