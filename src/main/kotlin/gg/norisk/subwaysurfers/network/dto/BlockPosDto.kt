package gg.norisk.subwaysurfers.network.dto

import kotlinx.serialization.Serializable
import net.minecraft.util.math.BlockPos

@Serializable
data class BlockPosDto(val x: Int, val y: Int, val z: Int)

fun BlockPosDto.toBlockPos() = BlockPos(x, y, z)
fun BlockPos.toDto() = BlockPosDto(x, y, z)
