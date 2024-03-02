package gg.norisk.subwaysurfers.network.s2c

import gg.norisk.subwaysurfers.SubwaySurfers.toId
import kotlinx.serialization.Serializable
import net.silkmc.silk.network.packet.s2cPacket

@Serializable
data class GameOverDto(val coins: Int, val ticks: Int)

val gameOverScreenS2C = s2cPacket<GameOverDto>("gameover".toId())