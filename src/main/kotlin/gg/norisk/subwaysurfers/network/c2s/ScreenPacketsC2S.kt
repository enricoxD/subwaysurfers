package gg.norisk.subwaysurfers.network.c2s

import gg.norisk.subwaysurfers.SubwaySurfers.toId
import net.silkmc.silk.network.packet.c2sPacket

val restartPacketC2S = c2sPacket<Unit>("restart".toId())

val homePacketC2S = c2sPacket<Unit>("home".toId())
