package gg.norisk.subwaysurfers.server.mechanics

import gg.norisk.subwaysurfers.SubwaySurfers
import gg.norisk.subwaysurfers.SubwaySurfers.logger
import gg.norisk.subwaysurfers.network.c2s.trackListRequestPacketC2S
import gg.norisk.subwaysurfers.network.s2c.*
import gg.norisk.subwaysurfers.subwaysurfers.isSubwaySurfers
import gg.norisk.subwaysurfers.subwaysurfers.lastPatternUpdatePos
import gg.norisk.subwaysurfers.utils.HashUtils
import gg.norisk.subwaysurfers.worldgen.pattern.RailPattern
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents
import net.minecraft.entity.Entity
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.server.world.ServerWorld
import java.io.File
import java.util.*
import kotlin.math.absoluteValue

object PatternManager : ServerTickEvents.EndWorldTick, ServerEntityEvents.Load {
    val NEW_PATTERN_DISTANCE = 20
    val rails = mutableSetOf<RailPattern>()
    val sides = mutableSetOf<Pair<TrackInfo, File>>()
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
        ServerEntityEvents.ENTITY_LOAD.register(this)
        loadAllSides()
        loadAllTracks()
        trackListRequestPacketC2S.receiveOnServer { packet, context -> handleTrackListRequest(packet, context.player) }
    }

    private fun handleTrackListRequest(tracks: List<TrackInfo>, player: ServerPlayerEntity) {
        val railList =
            rails.filter { (it.railName in tracks.map { requestedTrack -> requestedTrack.name }) && (it.hash in tracks.map { requestedTrack -> requestedTrack.hash }) }
                .map { TrackHolder(it.file, it.railName, it.hash) }.toMutableList()
        val sideList =
            sides.filter { (it.first.name in tracks.map { requestedTrack -> requestedTrack.name }) && (it.first.hash in tracks.map { requestedTrack -> requestedTrack.hash }) }
                .map { TrackHolder(it.second, it.first.name, it.first.hash) }

        railList.addAll(sideList)
        sendTracksToPlayer(player, railList)
    }

    private fun sendTracksToPlayer(playerEntity: ServerPlayerEntity, tracks: List<TrackHolder>) {
        for (track in tracks) {
            templatePacketS2C.send(TemplatePacket(track.file.readBytes(), track.name), playerEntity)
        }
    }

    private fun loadAllSides() {
        val sidesFolder = File(javaClass.getResource("/structures/sides/").toURI())
        for (file in sidesFolder.walkTopDown()) {
            if (file.extension == "nbt") {
                val sideInfo = TrackInfo(file.nameWithoutExtension, HashUtils.md5(file.readBytes()))
                logger.info("Added $sideInfo")
                sides.add(Pair(sideInfo, file))
            }
        }
    }

    private fun loadAllTracks() {
        val railFolder = File(javaClass.getResource("/structures/rails/").toURI())
        for (file in railFolder.walkTopDown()) {
            if (file.extension == "nbt") {
                val railPattern = RailPattern(
                    file.path.substringAfterLast("structures").replace("\\", "/") //TODO das könnte breaken?
                        .substring(1).replace(".nbt", ""), file, HashUtils.md5(file.readBytes())
                )
                logger.info("RailPattern: $railPattern")
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
                        getEnvironmentPattern(), nextPattern.map { it.railName }, getEnvironmentPattern()
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
        firstTrack: RailPattern = rails.filter { it.startColors.size == 3 || it.startColors.contains("green") }
            .random(), length: Int = 10
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

    override fun onLoad(entity: Entity, world: ServerWorld) {
        val player = entity as? ServerPlayerEntity ?: return
        val railList = rails.map { TrackInfo(it.railName, it.hash) }.toMutableList()
        val sideList = sides.map { it.first }
        railList.addAll(sideList)
        trackListPacketS2C.send(railList, player)
    }
}
