package gg.norisk.subwaysurfers.server.listener

import gg.norisk.subwaysurfers.server.ServerConfig
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents
import net.minecraft.entity.Entity
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.server.world.ServerWorld

object BasicListener : ServerEntityEvents.Load {
    fun init() {
        ServerEntityEvents.ENTITY_LOAD.register(this)
    }

    override fun onLoad(entity: Entity, world: ServerWorld) {
        ServerConfig.config.spawn
        val player = entity as? ServerPlayerEntity ?: return
        player.teleport(
            world.server.overworld, ServerConfig.config.spawn.x,
            ServerConfig.config.spawn.y,
            ServerConfig.config.spawn.z,
            ServerConfig.config.spawn.yaw,
            ServerConfig.config.spawn.pitch
        )
    }
}
