package com.health.heartratemonitor.di

import android.content.Context
import androidx.room.Room
import com.health.heartratemonitor.data.local.BleDatabase
import com.health.heartratemonitor.data.local.activity.ActivityDao
import com.health.heartratemonitor.data.local.ble.BleDeviceDao
import com.health.heartratemonitor.data.local.user.UserProfileDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideBleDatabase(
        @ApplicationContext context: Context
    ): BleDatabase {
        return Room.databaseBuilder(
            context,
            BleDatabase::class.java,
            "ble_database"
        ).build()
    }

    @Provides
    @Singleton
    fun provideBleDeviceDao(
        bleDatabase: BleDatabase
    ): BleDeviceDao {
        return bleDatabase.bleDeviceDao()
    }

    @Provides
    @Singleton
    fun provideUserProfileDao(
        bleDatabase: BleDatabase
    ): UserProfileDao {
        return bleDatabase.userProfileDao()
    }

    @Provides
    @Singleton
    fun provideActivityDao(
        bleDatabase: BleDatabase
    ): ActivityDao {
        return bleDatabase.activityDao()
    }
}
