package com.ikhokha.techcheck.utilits

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat


open class CheckPermissions(private val context: Context) {


    fun hasPermission() = Constants.REQUEST_PERMISSION.all { currentPermission->
        ContextCompat.checkSelfPermission(context,currentPermission) == PackageManager.PERMISSION_GRANTED
    }

    fun requestPermissions(){
        ActivityCompat.requestPermissions(
            context as Activity,
            Constants.REQUEST_PERMISSION,
            Constants.REQUEST_PERMISSION_CODE)
    }


}