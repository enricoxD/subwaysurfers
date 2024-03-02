package gg.norisk.subwaysurfers.network.c2s

import gg.norisk.subwaysurfers.SubwaySurfers.toId
import gg.norisk.subwaysurfers.common.serialization.UUIDSerializer
import kotlinx.serialization.Serializable
import net.silkmc.silk.network.packet.c2sPacket
import java.util.*

@Serializable
data class NbtEditDto(
    @Serializable(with = UUIDSerializer::class) val uuid: UUID,
    val moveSpeed: Float? = null,
    val shouldDrive: Boolean? = null,
    val variant: Int? = null
)

val nbtChangePacketC2S = c2sPacket<NbtEditDto>("nbt-change".toId())
