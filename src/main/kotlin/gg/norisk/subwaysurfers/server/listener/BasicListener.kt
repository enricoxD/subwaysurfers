package gg.norisk.subwaysurfers.server.listener

import gg.norisk.subwaysurfers.server.ServerConfig
import gg.norisk.subwaysurfers.server.command.StartCommand
import gg.norisk.subwaysurfers.subwaysurfers.isSubwaySurfers
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents
import net.minecraft.entity.Entity
import net.minecraft.server.MinecraftServer
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.server.world.ServerWorld
import net.minecraft.world.Difficulty
import net.minecraft.world.GameRules

object BasicListener : ServerEntityEvents.Load, ServerWorldEvents.Load, ServerPlayerEvents.AfterRespawn {
    fun init() {
        ServerEntityEvents.ENTITY_LOAD.register(this)
        ServerWorldEvents.LOAD.register(this)
        ServerPlayerEvents.AFTER_RESPAWN.register(this)
    }

    override fun onLoad(entity: Entity, world: ServerWorld) {
        ServerConfig.config.spawn
        val player = entity as? ServerPlayerEntity ?: return
        player.isSubwaySurfers = false
        player.teleport(
            world.server.overworld, ServerConfig.config.spawn.x,
            ServerConfig.config.spawn.y,
            ServerConfig.config.spawn.z,
            ServerConfig.config.spawn.yaw,
            ServerConfig.config.spawn.pitch
        )
    }

    override fun onWorldLoad(server: MinecraftServer, world: ServerWorld) {
        server.setDifficulty(Difficulty.PEACEFUL, true)
        server.overworld.timeOfDay = 6000
        server.gameRules.get(GameRules.DO_DAYLIGHT_CYCLE).set(false, server)
        server.gameRules.get(GameRules.DO_WEATHER_CYCLE).set(false, server)
    }

    override fun afterRespawn(oldPlayer: ServerPlayerEntity, newPlayer: ServerPlayerEntity, alive: Boolean) {
        StartCommand.handleGameStop(newPlayer, false)
    }
}
