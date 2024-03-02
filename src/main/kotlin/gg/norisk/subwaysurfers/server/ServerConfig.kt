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
    @Serializable
    data class Config(
        var spawn: PositionDto,
        var startPos: PositionDto
    )

    var configFile = File("config", "subwaysurfer-config.json")
    var config = Config(
        PositionDto(0.5, -60.0, 0.5, 0f, 0f),
        PositionDto(8.5, -60.0, 8.5, 0f, 0f)
    )

    fun init() {
        configCommands()
        if (!configFile.exists()) {
            logger.info("Creating new config file")
            configFile.writeText(Json.encodeToString(config))
        }
        config = Json.decodeFromString(configFile.readText())
        logger.info("Loaded Server Config")
    }

    private fun load() {
        config = Json.decodeFromString(configFile.readText())
        logger.info("Loaded Server Config")
    }

    private fun save() {
        configFile.writeText(Json.encodeToString(config))
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
