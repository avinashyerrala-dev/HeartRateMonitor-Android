package com.health.heartratemonitor.domain.usecases.device

import com.health.heartratemonitor.domain.repository.BleRepository
import javax.inject.Inject

class DisconnectDeviceUseCase  @Inject constructor(
    private val repository: BleRepository
) {
    operator fun invoke() {
        return repository.disconnectDevice()
    }
}
