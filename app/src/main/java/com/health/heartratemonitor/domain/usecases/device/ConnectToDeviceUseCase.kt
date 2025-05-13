package com.health.heartratemonitor.domain.usecases.device

import com.health.heartratemonitor.domain.model.BleDevice
import com.health.heartratemonitor.domain.repository.BleRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ConnectToDeviceUseCase @Inject constructor(
    private val repository: BleRepository
) {
    operator fun invoke(address: String, name: String): Flow<BleDevice> {
        return repository.connectToDevice(address, name)
    }
}
