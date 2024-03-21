package gg.norisk.subwaysurfers.network.c2s

import gg.norisk.subwaysurfers.SubwaySurfers.toId
import gg.norisk.subwaysurfers.network.s2c.TrackInfo
import net.silkmc.silk.network.packet.c2sPacket

val trackListRequestPacketC2S = c2sPacket<List<TrackInfo>>("track-list-request-packet".toId())
