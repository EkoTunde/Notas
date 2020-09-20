package com.ekosoftware.notas.app.di

import android.content.Context
import androidx.room.Room
import com.ekosoftware.notas.app.Constants.DATABASE_NAME
import com.ekosoftware.notas.data.local.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideRoomInstance(@ApplicationContext context: Context) =
        Room.databaseBuilder(context, AppDatabase::class.java, DATABASE_NAME).fallbackToDestructiveMigration().build()

    @Singleton
    @Provides
    fun provideNoteDao(db: AppDatabase) = db.noteDao()

    @Singleton
    @Provides
    fun provideLabelDao(db: AppDatabase) = db.labelDao()
}