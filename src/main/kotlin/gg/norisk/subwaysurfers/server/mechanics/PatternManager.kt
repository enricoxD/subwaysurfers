package gg.norisk.subwaysurfers.server.mechanics

import gg.norisk.subwaysurfers.SubwaySurfers.logger
import gg.norisk.subwaysurfers.network.s2c.PatternPacket
import gg.norisk.subwaysurfers.network.s2c.patternPacketS2C
import gg.norisk.subwaysurfers.subwaysurfers.isSubwaySurfers
import gg.norisk.subwaysurfers.subwaysurfers.lastPatternUpdatePos
import gg.norisk.subwaysurfers.worldgen.pattern.RailPattern
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents
import net.minecraft.server.world.ServerWorld
import java.io.File
import java.util.*
import kotlin.math.absoluteValue

object PatternManager : ServerTickEvents.EndWorldTick {
    val NEW_PATTERN_DISTANCE = 20
    val rails = mutableSetOf<RailPattern>()
    val houses = listOf(
        "subway_house_1",
        "subway_house_2",
        "subway_house_3",
        "subway_house_4",
        "subway_house_5",
        "subway_house_6",
        "subway_house_7",
        "subway_house_8",
        "subway_house_9",
        "subway_house_10",
        "subway_house_11",
    )

    //TODO das clearen wenn spieler leaved etc vergess ich eh lol
    val playerPatterns = mutableMapOf<UUID, List<RailPattern>>()

    fun init() {
        ServerTickEvents.END_WORLD_TICK.register(this)

        val railFolder = File(javaClass.getResource("/structures/rails/").toURI())
        for (file in railFolder.walkTopDown()) {
            if (file.extension == "nbt") {
                val railPattern = RailPattern(
                    file.path
                        .substringAfterLast("structures")
                        .replace("\\", "/") //TODO das könnte breaken?
                        .substring(1)
                        .replace(".nbt", "")
                )
                rails.add(railPattern)
            }
        }
    }

    override fun onEndTick(world: ServerWorld) {
        for (playerEntity in world.players.filter { it.isSubwaySurfers }) {
            val pos = playerEntity.z.absoluteValue.toInt()
            if (pos.mod(NEW_PATTERN_DISTANCE) == 0 && pos != playerEntity.lastPatternUpdatePos) {
                playerEntity.lastPatternUpdatePos = pos
                logger.info("Sending new Pattern to ${playerEntity.name}")

                val lastPatterns = playerPatterns[playerEntity.uuid] ?: getRailPattern()
                val nextPattern = getRailPattern(firstTrack = lastPatterns.last())
                playerPatterns[playerEntity.uuid] = nextPattern

                patternPacketS2C.send(
                    PatternPacket(
                        getEnvironmentPattern(),
                        nextPattern.map { it.path },
                        getEnvironmentPattern()
                    ), playerEntity
                )
            }
        }
    }

    fun getEnvironmentPattern(length: Int = 20): List<String> {
        return buildList {
            repeat(length) {
                add(houses.random())
            }
        }
    }

    fun getRailPattern(
        firstTrack: RailPattern = rails.filter { it.startColors.size == 3 || it.startColors.contains("green") }.random(),
        length: Int = 10
    ): List<RailPattern> {
        return buildList {
            var lastTrack = firstTrack
            repeat(length) {
                val nextRailPattern = getFittingRailPattern(lastTrack)
                add(nextRailPattern)
                lastTrack = nextRailPattern
            }
        }
    }

    fun getRailsWithAllColors(): List<RailPattern> {
        return rails.filter { it.startColors.size == 3 }
    }

    fun getFittingRailPattern(railPattern: RailPattern): RailPattern {
        val fittingRails = rails.filter { rail -> rail.startColors.any { it in railPattern.endColors } }
        return fittingRails.random()
    }
}
