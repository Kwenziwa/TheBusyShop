package com.ikhokha.techcheck.model.di

import android.content.Context
import androidx.room.Room
import com.ikhokha.techcheck.model.data.ProductDatabase
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import com.ikhokha.techcheck.utilits.Constants.DATABASE_NAME
import com.ikhokha.techcheck.model.data.ProductDAO
import com.ikhokha.techcheck.model.repository.DefaultRepository
import com.ikhokha.techcheck.model.repository.ProductRepository
import dagger.Provides
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun providesProductDatabase(
        @ApplicationContext context: Context
    ) = Room.databaseBuilder(context,ProductDatabase::class.java,DATABASE_NAME)
        .fallbackToDestructiveMigration()
        .allowMainThreadQueries()
        .build()


    @Singleton
    @Provides
    fun providesDefaultRepository(
        dao: ProductDAO
    )= DefaultRepository(dao) as ProductRepository

    @Singleton
    @Provides
    fun providesProductDao(
        database: ProductDatabase
    ) = database.productDao()
}