package gg.norisk.subwaysurfers.client.commands

import net.fabricmc.loader.api.FabricLoader

object ClientCommands {
    fun init() {
        if (FabricLoader.getInstance().isDevelopmentEnvironment) {
            devCommands()
        }
    }

    private fun devCommands() {

    }
}
