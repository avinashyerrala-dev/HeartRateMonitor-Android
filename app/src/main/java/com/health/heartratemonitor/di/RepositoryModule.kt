package com.health.heartratemonitor.di

import android.content.Context
import com.health.heartratemonitor.data.local.ble.BleDeviceDao
import com.health.heartratemonitor.data.repository.ActivityRepositoryImpl
import com.health.heartratemonitor.data.repository.BleRepositoryImpl
import com.health.heartratemonitor.data.repository.UserProfileRepositoryImpl
import com.health.heartratemonitor.domain.repository.ActivityRepository
import com.health.heartratemonitor.domain.repository.BleRepository
import com.health.heartratemonitor.domain.repository.UserProfileRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindUserProfileRepository(
        impl: UserProfileRepositoryImpl
    ): UserProfileRepository

    @Binds
    @Singleton
    abstract fun bindActivityRepository(
        impl: ActivityRepositoryImpl
    ): ActivityRepository
}

@Module
@InstallIn(SingletonComponent::class)
object BleRepositoryProviderModule {

    @Provides
    @Singleton
    fun provideBleRepository(
        @ApplicationContext context: Context,
        bleDeviceDao: BleDeviceDao
    ): BleRepository {
        return BleRepositoryImpl(context, bleDeviceDao)
    }
}
