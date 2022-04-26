package com.ikhokha.techcheck.model

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

@Entity(tableName = "product")
data class Product(
    @PrimaryKey
    var id: String,
    var quantity: Int?=0,
    var inBasket: Boolean=false,
    val image: String?="",
    val price: Double?=0.0,
    val description: String?="",
    var imageUrl: String?=""
){
    @Ignore
    constructor():this("noId")
}