package gg.norisk.subwaysurfers.network.c2s

import gg.norisk.subwaysurfers.SubwaySurfers.toId
import gg.norisk.subwaysurfers.network.dto.BlockPosDto
import net.silkmc.silk.network.packet.c2sPacket

val horizontalCollisionPacketC2S = c2sPacket<BlockPosDto>("horizontal_collision".toId())