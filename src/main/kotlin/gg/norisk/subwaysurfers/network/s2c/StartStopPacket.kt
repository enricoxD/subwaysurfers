package gg.norisk.subwaysurfers.network.s2c

import gg.norisk.subwaysurfers.SubwaySurfers.toId
import kotlinx.serialization.Serializable
import net.silkmc.silk.network.packet.s2cPacket

@Serializable
data class StartStopPacket(
    var isEnabled: Boolean = false,
)

val startStopPacketS2C = s2cPacket<StartStopPacket>("start-stop".toId())
