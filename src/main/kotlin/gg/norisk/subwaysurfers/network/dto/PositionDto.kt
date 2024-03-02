package gg.norisk.subwaysurfers.network.dto

import kotlinx.serialization.Serializable

@Serializable
data class PositionDto(val x: Double, val y: Double, val z: Double, val yaw: Float, val pitch: Float)
