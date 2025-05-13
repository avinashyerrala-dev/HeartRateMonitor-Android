package com.health.heartratemonitor.data

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattDescriptor
import android.bluetooth.BluetoothGattService
import android.bluetooth.BluetoothProfile
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import androidx.annotation.RequiresPermission
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class BleManager(private val context: Context) {

    private var gatt: BluetoothGatt? = null

    companion object {
        val HEART_RATE_SERVICE_UUID = java.util.UUID.fromString("0000180D-0000-1000-8000-00805f9b34fb")
        val HEART_RATE_MEASUREMENT_CHAR_UUID = java.util.UUID.fromString("00002A37-0000-1000-8000-00805f9b34fb")
        val CLIENT_CHARACTERISTIC_CONFIG_UUID = java.util.UUID.fromString("00002902-0000-1000-8000-00805f9b34fb")
    }

    @SuppressLint("MissingPermission")
    fun connect(device: BluetoothDevice): Flow<Int> = callbackFlow {
        gatt = device.connectGatt(context, false, object : BluetoothGattCallback() {
            override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
                if (newState == BluetoothProfile.STATE_CONNECTED) {
                    gatt.discoverServices()
                } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                    close()
                }
            }

            override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
                val heartRateService = gatt.getService(HEART_RATE_SERVICE_UUID)
                val heartRateChar = heartRateService?.getCharacteristic(HEART_RATE_MEASUREMENT_CHAR_UUID)
                heartRateChar?.let { char ->
                    gatt.setCharacteristicNotification(char, true)
                    val descriptor = char.getDescriptor(CLIENT_CHARACTERISTIC_CONFIG_UUID)
                    descriptor.value = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
                    gatt.writeDescriptor(descriptor)
                }
            }

            override fun onCharacteristicChanged(gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic) {
                if (characteristic.uuid == HEART_RATE_MEASUREMENT_CHAR_UUID) {
                    val value = characteristic.value
                    if (value.isNotEmpty()) {
                        val flag = value[0].toInt()
                        val format = if (flag and 0x01 != 0) {
                            BluetoothGattCharacteristic.FORMAT_UINT16
                        } else {
                            BluetoothGattCharacteristic.FORMAT_UINT8
                        }
                        val heartRate = characteristic.getIntValue(format, 1)
                        trySend(heartRate)
                    }
                }
            }
        })

        awaitClose {
            gatt?.disconnect()
            gatt?.close()
        }
    }

    @SuppressLint("MissingPermission")
    fun disconnectDevice() {
        gatt?.disconnect()
        gatt?.close()
    }
}
