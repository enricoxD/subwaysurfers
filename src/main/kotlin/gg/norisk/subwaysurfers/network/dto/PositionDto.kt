package gg.norisk.subwaysurfers.network.dto

import kotlinx.serialization.Serializable
import net.minecraft.util.math.BlockPos

@Serializable
data class PositionDto(val x: Double, val y: Double, val z: Double, val yaw: Float, val pitch: Float) {
    fun toBlockPos() = BlockPos(x.toInt(), y.toInt(), z.toInt())
}
