package gg.norisk.subwaysurfers.server

import gg.norisk.subwaysurfers.SubwaySurfers.logger
import gg.norisk.subwaysurfers.network.dto.PositionDto
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import net.silkmc.silk.commands.PermissionLevel
import net.silkmc.silk.commands.command
import java.io.File

object ServerConfig {
    private val json = Json {
        encodeDefaults = true
        ignoreUnknownKeys = true
        prettyPrint = true
    }

    @Serializable
    data class Config(
        var spawn: PositionDto = PositionDto(8.5, -60.0, 8.5, 0f, 0f),
        var startPos: PositionDto = PositionDto(8.5, -60.0, 34.5, 0f, 0f),
        var surferBaseSpeed: Double = 0.15,
        var surferMaxSpeed: Double = 0.4,
        var surferAcceleration: Double = 0.005,
    )

    var configFolder = File("config", "subwaysurfers").apply { mkdirs() }
    var configFile = File(configFolder, "config.json")
    var config = Config()

    fun init() {
        configCommands()
        if (!configFile.exists()) {
            logger.info("Creating new config file")
            configFile.writeText(json.encodeToString(config))
        }
        config = json.decodeFromString(configFile.readText())
        logger.info("Loaded Server Config")
    }

    private fun load() {
        config = json.decodeFromString(configFile.readText())
        logger.info("Loaded Server Config")
    }

    private fun save() {
        configFile.writeText(json.encodeToString(config))
        logger.info("Saved config file")
    }

    private fun configCommands() {
        command("subwaysurferconfig") {
            requiresPermissionLevel(PermissionLevel.COMMAND_RIGHTS)
            literal("spawn") {
                runs {
                    val player = this.source.playerOrThrow
                    config.spawn = PositionDto(
                        player.x, player.y, player.z, player.yaw, player.pitch
                    )
                    save()
                }
            }
            literal("reload") {
                runs {
                    load()
                }
            }
        }
    }
}
