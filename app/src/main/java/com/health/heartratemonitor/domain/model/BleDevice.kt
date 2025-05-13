package com.health.heartratemonitor.domain.model

data class BleDevice(
    val name: String?,
    val address: String,
    val isConnected: Boolean = false,
    val isPreviouslyConnected: Boolean = false
)

