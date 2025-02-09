package gg.norisk.subwaysurfers.network.s2c

import gg.norisk.subwaysurfers.SubwaySurfers.toId
import kotlinx.serialization.Serializable
import net.silkmc.silk.network.packet.s2cPacket

@Serializable
data class CameraSettings(
    var desiredCameraDistance: Double = 5.0,
    var cameraOffsetY: Double = 1.5,
    var cameraOffsetZ: Double = 2.0,
    var cameraSpeedX: Double = 0.05,
    var cameraSpeedY: Double = 0.3,
    var fov: Double = 100.0,
    var yaw: Float = 0f,
    var pitch: Float = 20f,
) {
}

val cameraSettingsPacket = s2cPacket<CameraSettings>("camera-settings".toId())

