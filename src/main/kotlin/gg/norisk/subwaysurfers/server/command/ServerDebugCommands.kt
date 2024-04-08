package gg.norisk.subwaysurfers.server.command

import com.mojang.brigadier.arguments.IntegerArgumentType
import gg.norisk.subwaysurfers.network.s2c.policeTeleportPacketS2C
import gg.norisk.subwaysurfers.subwaysurfers.punishTicks
import net.fabricmc.loader.api.FabricLoader
import net.silkmc.silk.commands.command

object ServerDebugCommands {
    fun init() {
        if (FabricLoader.getInstance().isDevelopmentEnvironment) {
            command("police") {
                argument<Int>("ticks", IntegerArgumentType.integer(0)) { ticks ->
                    runs {
                        val player = this.source.playerOrThrow
                        //player.renderPolice = !player.renderPolice
                        player.punishTicks = ticks()
                        policeTeleportPacketS2C.send(Unit, player)
                    }
                }
            }
        }
    }
}
