package com.health.heartratemonitor.domain.repository

import com.health.heartratemonitor.domain.model.BleDevice
import kotlinx.coroutines.flow.Flow

interface BleRepository {
    fun scanForDevices(): Flow<List<BleDevice>>
    fun connectToDevice(address: String, name: String): Flow<BleDevice>
    fun observeConnectedDevices(): Flow<List<BleDevice>>
    fun observeHeartRate(address: String): Flow<Int>
    fun disconnectDevice()
}