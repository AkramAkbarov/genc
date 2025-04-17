package com.akbaria.genc.data.di


import com.akbaria.genc.data.repository.ArtRepository
import com.google.firebase.firestore.firestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideArtRepository(): ArtRepository {
        return ArtRepository()
    }
}