package gg.norisk.subwaysurfers.network.s2c

import gg.norisk.subwaysurfers.SubwaySurfers.toId
import gg.norisk.subwaysurfers.network.dto.PositionDto
import kotlinx.serialization.Serializable
import net.silkmc.silk.network.packet.s2cPacket

@Serializable
data class PreStartS2C(
    var startPos: PositionDto = PositionDto(8.5, -60.0, 8.5, 0f, 0f),
    val initialPattern: PatternPacket,
    val howLong: Long,
    val cameraSettings: CameraSettings
)

val preStartS2C = s2cPacket<PreStartS2C>("pre-start".toId())
