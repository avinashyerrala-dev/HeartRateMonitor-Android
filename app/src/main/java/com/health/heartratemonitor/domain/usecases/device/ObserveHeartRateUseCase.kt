package com.health.heartratemonitor.domain.usecases.device

import com.health.heartratemonitor.domain.repository.BleRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveHeartRateUseCase @Inject constructor(
    private val repository: BleRepository
) {
    operator fun invoke(address: String): Flow<Int> {
        return repository.observeHeartRate(address)
    }
}
