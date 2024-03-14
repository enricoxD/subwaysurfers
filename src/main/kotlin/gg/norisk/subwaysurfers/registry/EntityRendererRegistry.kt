package gg.norisk.subwaysurfers.registry

import gg.norisk.subwaysurfers.client.renderer.entity.*
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry

object EntityRendererRegistry {
    fun init() {
        EntityRendererRegistry.register(EntityRegistry.COIN, ::CoinRenderer)
        EntityRendererRegistry.register(EntityRegistry.TRAFFICLIGHT, ::TrafficLightRenderer)
        EntityRendererRegistry.register(EntityRegistry.HOVERBOARD, ::HoverboardRenderer)
        EntityRendererRegistry.register(EntityRegistry.JETPACK, ::JetpackRenderer)
        EntityRendererRegistry.register(EntityRegistry.BOOTS, ::BootsRenderer)
        EntityRendererRegistry.register(EntityRegistry.TRAIN, ::TrainRenderer)
        EntityRendererRegistry.register(EntityRegistry.MAGNET, ::MagnetRenderer)
    }
}
