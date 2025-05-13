package com.health.heartratemonitor.data.local.ble

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ble_devices")
class BleDeviceEntity(
    @PrimaryKey val address: String,
    val name: String?,
    val isConnected: Boolean,
    val lastConnectedAt: Long = 0L
)
