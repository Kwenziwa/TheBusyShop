package com.ikhokha.techcheck.model.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.ikhokha.techcheck.model.Product

@Database(entities = [Product::class],exportSchema = false,version = 7)
abstract class ProductDatabase: RoomDatabase() {

    abstract fun productDao(): ProductDAO

}