package com.ikhokha.techcheck.utilits


import android.Manifest

object Constants {

    const val REQUEST_PERMISSION_CODE = 1001
    val REQUEST_PERMISSION = arrayOf(Manifest.permission.CAMERA, Manifest.permission.INTERNET)
    const val DATABASE_NAME = "product_database"
}