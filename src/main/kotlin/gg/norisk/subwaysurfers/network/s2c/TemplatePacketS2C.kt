package gg.norisk.subwaysurfers.network.s2c

import gg.norisk.subwaysurfers.SubwaySurfers.toId
import kotlinx.serialization.Serializable
import net.silkmc.silk.network.packet.s2cPacket
import java.io.File

@Serializable
data class TemplatePacket(val bytes: ByteArray, val path: String)

@Serializable
data class TrackInfo(val name: String, val hash: String) {
    override fun toString(): String {
        return "TrackInfo(name='$name', hash='$hash')"
    }
}

data class TrackHolder(val file: File, val name: String, val hash: String)

val templatePacketS2C = s2cPacket<TemplatePacket>("template-packet".toId())

val trackListPacketS2C = s2cPacket<List<TrackInfo>>("track-list-packet".toId())

