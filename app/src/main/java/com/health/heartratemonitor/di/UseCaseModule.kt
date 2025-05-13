package com.health.heartratemonitor.di

import com.health.heartratemonitor.domain.repository.BleRepository
import com.health.heartratemonitor.domain.usecases.device.ConnectToDeviceUseCase
import com.health.heartratemonitor.domain.usecases.device.ObserveHeartRateUseCase
import com.health.heartratemonitor.domain.usecases.device.ScanForDevicesUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {

    @Provides
    @Singleton
    fun provideScanForDevicesUseCase(
        repository: BleRepository
    ): ScanForDevicesUseCase {
        return ScanForDevicesUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideConnectToDeviceUseCase(
        repository: BleRepository
    ): ConnectToDeviceUseCase {
        return ConnectToDeviceUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideObserveHeartRateUseCase(
        repository: BleRepository
    ): ObserveHeartRateUseCase {
        return ObserveHeartRateUseCase(repository)
    }
}
