package gg.norisk.subwaysurfers.network.c2s

import gg.norisk.subwaysurfers.SubwaySurfers.toId
import gg.norisk.subwaysurfers.network.dto.BlockPosDto
import net.silkmc.silk.network.packet.c2sPacket

val magnetCollisionPacketC2S = c2sPacket<BlockPosDto>("magnet".toId())
val jetpackCollisionPacketC2S = c2sPacket<BlockPosDto>("jetpack".toId())

//TODO hallo wer auch immer das liest,
//ja das müsste man auf dem server auch checken ob die coin es da wirklich gibt aber bis dahin...
val coinCollisionPacketC2S = c2sPacket<BlockPosDto>("coin".toId())