package gg.norisk.subwaysurfers.network.s2c

import gg.norisk.subwaysurfers.SubwaySurfers.toId
import net.silkmc.silk.network.packet.s2cPacket

val policeTeleportPacketS2C = s2cPacket<Unit>("police-teleport-packet".toId())
