package com.health.heartratemonitor.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.health.heartratemonitor.data.local.activity.ActivityDao
import com.health.heartratemonitor.data.local.activity.ActivityGraphEntity
import com.health.heartratemonitor.data.local.activity.ActivitySummaryEntity
import com.health.heartratemonitor.data.local.ble.BleDeviceDao
import com.health.heartratemonitor.data.local.ble.BleDeviceEntity
import com.health.heartratemonitor.data.local.user.UserProfileDao
import com.health.heartratemonitor.data.local.user.UserProfileEntity

@Database(
    entities = [BleDeviceEntity::class, UserProfileEntity::class,
        ActivitySummaryEntity::class, ActivityGraphEntity::class],
    version = 2,
    exportSchema = false
)
abstract class BleDatabase : RoomDatabase() {
    abstract fun bleDeviceDao(): BleDeviceDao
    abstract fun userProfileDao(): UserProfileDao
    abstract fun activityDao(): ActivityDao
}