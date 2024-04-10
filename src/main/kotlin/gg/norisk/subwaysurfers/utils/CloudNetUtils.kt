package gg.norisk.subwaysurfers.utils

import eu.cloudnetservice.driver.inject.InjectionLayer
import eu.cloudnetservice.driver.registry.ServiceRegistry
import eu.cloudnetservice.modules.bridge.player.PlayerManager
import net.fabricmc.loader.api.FabricLoader
import net.kyori.adventure.text.Component

object CloudNetUtils {
    val serviceRegistry by lazy { InjectionLayer.ext().instance(ServiceRegistry::class.java) }
    val playerManager by lazy { serviceRegistry.firstProvider(PlayerManager::class.java) }

    fun broadcastMessage(component: Component) {
        if (FabricLoader.getInstance().isDevelopmentEnvironment) return
        playerManager.globalPlayerExecutor().sendChatMessage(component, "hglabor.subwaysurfers.structures.alert")
    }
}
