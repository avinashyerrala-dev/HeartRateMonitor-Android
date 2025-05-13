package com.health.heartratemonitor.data.repository

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import com.health.heartratemonitor.data.BleManager
import com.health.heartratemonitor.data.local.ble.BleDeviceDao
import com.health.heartratemonitor.data.local.ble.BleDeviceEntity
import com.health.heartratemonitor.domain.model.BleDevice
import com.health.heartratemonitor.domain.repository.BleRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import java.util.UUID
import javax.inject.Inject

class BleRepositoryImpl @Inject constructor(
    private val context: Context,
    private val bleDeviceDao: BleDeviceDao
) : BleRepository {

    private val bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()
    private val bleManager = BleManager(context)

    private val HEART_RATE_SERVICE_UUID = UUID.fromString("0000180D-0000-1000-8000-00805f9b34fb")

    override fun scanForDevices(): Flow<List<BleDevice>> = callbackFlow {
        val scanner = bluetoothAdapter?.bluetoothLeScanner
        if (scanner == null) {
            close()
            return@callbackFlow
        }

        val devices = mutableListOf<BleDevice>()

        val callback = object : ScanCallback() {
            override fun onScanResult(callbackType: Int, result: ScanResult) {
                val device = result.device
                val serviceUuids = result.scanRecord?.serviceUuids
                val hasHeartRateService = serviceUuids?.any { it.uuid == HEART_RATE_SERVICE_UUID } == true

                if (hasHeartRateService && devices.none { it.address == device.address }) {
                    devices.add(
                        BleDevice(
                            name = device.name ?: "Unknown",
                            address = device.address
                        )
                    )
                    trySend(devices.toList())
                }
            }
        }

        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.BLUETOOTH_SCAN
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            scanner.startScan(callback)
        }
        awaitClose {
            scanner.stopScan(callback)
        }
    }

    override fun connectToDevice(address: String, name: String): Flow<BleDevice> = flow {
        val device = BleDevice(name = name, address = address, isConnected = true)
        bleDeviceDao.insertDevice(
            BleDeviceEntity(
                address = device.address,
                name = device.name,
                isConnected = device.isConnected,
                lastConnectedAt = System.currentTimeMillis()
            )
        )
        emit(device)
    }

    override fun observeConnectedDevices(): Flow<List<BleDevice>> {
        return bleDeviceDao.getAllDevices().map { list ->
            list.map { entity ->
                BleDevice(
                    name = entity.name,
                    address = entity.address,
                    isConnected = entity.isConnected,
                    isPreviouslyConnected = true
                )
            }
        }
    }

    override fun observeHeartRate(address: String): Flow<Int> {
        val device = bluetoothAdapter?.getRemoteDevice(address)
        requireNotNull(device) { "Device not found with address: $address" }
        return bleManager.connect(device)
    }
    override fun disconnectDevice() {
        bleManager.disconnectDevice()
    }
}
