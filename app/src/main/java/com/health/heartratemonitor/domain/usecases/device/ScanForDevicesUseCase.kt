package com.health.heartratemonitor.domain.usecases.device
import com.health.heartratemonitor.domain.model.BleDevice
import com.health.heartratemonitor.domain.repository.BleRepository
import kotlinx.coroutines.flow.Flow

class ScanForDevicesUseCase(
    private val repository: BleRepository
) {
    operator fun invoke(): Flow<List<BleDevice>> {
        return repository.scanForDevices()
    }
}
