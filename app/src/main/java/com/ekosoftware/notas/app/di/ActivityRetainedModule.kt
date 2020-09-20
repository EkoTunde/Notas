package com.ekosoftware.notas.app.di

import com.ekosoftware.notas.domain.LabelRepository
import com.ekosoftware.notas.domain.LabelRepositoryImpl
import com.ekosoftware.notas.domain.NoteRepository
import com.ekosoftware.notas.domain.NoteRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent

@Module
@InstallIn(ActivityRetainedComponent::class)
abstract class ActivityRetainedModule {
    @Binds
    abstract fun dataSource(impl: NoteRepositoryImpl): NoteRepository

    @Binds
    abstract fun dataSource2(impl: LabelRepositoryImpl) : LabelRepository
}