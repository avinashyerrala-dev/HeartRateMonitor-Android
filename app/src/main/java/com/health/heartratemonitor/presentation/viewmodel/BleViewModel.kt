package com.health.heartratemonitor.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.health.heartratemonitor.domain.model.BleDevice
import com.health.heartratemonitor.domain.repository.BleRepository
import com.health.heartratemonitor.domain.usecases.device.ConnectToDeviceUseCase
import com.health.heartratemonitor.domain.usecases.device.ObserveHeartRateUseCase
import com.health.heartratemonitor.domain.usecases.device.ScanForDevicesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.cancellable
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.random.Random

@HiltViewModel
class BleViewModel @Inject constructor(
    private val scanForDevicesUseCase: ScanForDevicesUseCase,
    private val connectToDeviceUseCase: ConnectToDeviceUseCase,
    private val observeHeartRateUseCase: ObserveHeartRateUseCase,
    private val bleRepository: BleRepository
) : ViewModel() {

    private val _availableDevices = MutableStateFlow<List<BleDevice>>(emptyList())
    val availableDevices: StateFlow<List<BleDevice>> = _availableDevices

    private val _previouslyConnectedDevices = MutableStateFlow<List<BleDevice>>(emptyList())
    val previouslyConnectedDevices: StateFlow<List<BleDevice>> = _previouslyConnectedDevices

    private val _connectingAddress = MutableStateFlow<String?>(null)
    val connectingAddress: StateFlow<String?> = _connectingAddress

    private val _connectedAddress = MutableStateFlow<String?>(null)
    val connectedAddress: StateFlow<String?> = _connectedAddress

    private val _heartRate = MutableStateFlow<Int?>(null)
    val heartRate: StateFlow<Int?> = _heartRate

    private val _connectedDeviceName = MutableStateFlow("Not connected")
    val connectedDeviceName: StateFlow<String> = _connectedDeviceName

    private var hasTriedAutoConnect = false
    private var scanJob: Job? = null

    init {
        observePreviouslyConnectedDevices()
//        simulateHeartRateForTesting()
    }

    fun simulateHeartRateForTesting() {
        viewModelScope.launch {
            while (true) {
                val randomHeartRate = Random.nextInt(60, 190) // Simulate HR between 60 and 190 bpm
                _heartRate.value = randomHeartRate
                delay(1000L) // Update every 2 seconds
            }
        }
    }


    fun startScan() {
        if (scanJob?.isActive == true) return
        scanJob = viewModelScope.launch {
            scanForDevicesUseCase()
                .cancellable()
                .collectLatest { devices ->
                    _availableDevices.value = devices
                }
        }
    }


    fun stopScan() {
        scanJob?.cancel()
        scanJob = null
        _availableDevices.value = emptyList()
    }

    fun connectToDevice(address: String, name: String) {
        viewModelScope.launch {
            _connectingAddress.value = address
            connectToDeviceUseCase(address, name).collectLatest { device ->
                _connectedAddress.value = device.address
                _connectingAddress.value = null
                _connectedDeviceName.value = device.name.orEmpty()

                observeHeartRate(device.address)
            }
        }
    }

    private fun observeHeartRate(address: String) {
        viewModelScope.launch {
            observeHeartRateUseCase(address).collectLatest { rate ->
                _heartRate.value = rate
            }
        }
    }

    private fun observePreviouslyConnectedDevices() {
        viewModelScope.launch {
            bleRepository.observeConnectedDevices().collectLatest { devices ->
                _previouslyConnectedDevices.value = devices

                if (!hasTriedAutoConnect && devices.isNotEmpty()) {
                    autoConnectToPreviousDevice()
                }
            }
        }
    }

    private fun autoConnectToPreviousDevice() {
        if (hasTriedAutoConnect) return
        hasTriedAutoConnect = true

        val previousDevice = previouslyConnectedDevices.value.firstOrNull()
        if (previousDevice != null) {
            connectToDevice(previousDevice.address, previousDevice.name.orEmpty())
        }
    }
}
