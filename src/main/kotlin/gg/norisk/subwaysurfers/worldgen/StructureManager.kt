package gg.norisk.subwaysurfers.worldgen

import com.google.common.cache.CacheBuilder
import gg.norisk.subwaysurfers.SubwaySurfers.logger
import gg.norisk.subwaysurfers.client.ClientSettings
import gg.norisk.subwaysurfers.client.structure.ClientStructureTemplate
import gg.norisk.subwaysurfers.extensions.toStack
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents.END_WORLD_TICK
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents.EndWorldTick
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.block.BlockState
import net.minecraft.client.MinecraftClient
import net.minecraft.client.network.ClientPlayerEntity
import net.minecraft.client.world.ClientWorld
import net.minecraft.datafixer.DataFixTypes
import net.minecraft.datafixer.Schemas
import net.minecraft.entity.Entity
import net.minecraft.nbt.NbtCompound
import net.minecraft.nbt.NbtHelper
import net.minecraft.nbt.NbtIo
import net.minecraft.nbt.NbtSizeTracker
import net.minecraft.registry.Registries
import net.minecraft.structure.StructurePlacementData
import net.minecraft.structure.StructureTemplate
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3i
import net.silkmc.silk.commands.clientCommand
import net.silkmc.silk.commands.player
import java.io.File
import java.io.IOException
import java.util.*
import kotlin.time.Duration.Companion.minutes
import kotlin.time.toJavaDuration

object StructureManager : EndWorldTick {
    var patternGenerator: PatternGenerator? = null
    private val structureTemplates =
        CacheBuilder.newBuilder().expireAfterAccess(5.minutes.toJavaDuration()).build<String, StructureTemplate>()

    fun initClient() {
        if (FabricLoader.getInstance().isDevelopmentEnvironment) {
            clientCommand("clientstructure") {
                argument<String>("name") { templateName ->
                    runs {
                        placeStructure(this.source.player, this.source.player.blockPos, templateName())
                    }
                }
            }
            clientCommand("fakestructure") {
                runs {
                    val player = this.source.player
                    val startPos = player.blockPos
                    patternGenerator = PatternGenerator(
                        startPos = startPos!!.add(10, 0, 0),
                        patternStack = Stack<Stack<String>>().apply {
                            add(
                                listOf(
                                    "plains_small_house_1",
                                    "plains_small_house_2",
                                    "plains_small_house_3",
                                    "plains_small_house_4",
                                    "plains_small_house_5",
                                    "plains_small_house_6",
                                    "plains_small_house_7",
                                    "plains_small_house_8",
                                ).toStack()
                            )
                        }
                    )
                }
            }
            END_WORLD_TICK.register(this)
        }
    }

    fun placeStructure(
        player: ClientPlayerEntity,
        pos: BlockPos,
        template: StructureTemplate,
        placementData: StructurePlacementData = StructurePlacementData(),
        ignoreAir: Boolean = false,
        blocks: MutableMap<BlockPos, BlockState>? = null,
        entities: MutableSet<Entity>? = null,
    ) {
        val world = player.world as ClientWorld

        (template as ClientStructureTemplate).placeClient(
            world,
            pos,
            pos.add(Vec3i(0, 1, 0)),
            placementData,
            world.random,
            2,
            ignoreAir,
            blocks,
            entities
        )
    }

    fun placeStructure(player: ClientPlayerEntity, pos: BlockPos, name: String) {
        val template = readOrLoadTemplate(name) ?: return logger.error("Error placing structure $name at $pos")
        placeStructure(player, pos, template)
    }

    @Throws(IOException::class)
    fun readOrLoadTemplate(name: String?): StructureTemplate? {
        if (name == null) return null
        val template = structureTemplates.getIfPresent(name)

        if (template != null) {
            return template
        }

        var nbtCompound: NbtCompound? = null

        runCatching {
            File(ClientSettings.baseFolder,"$name.nbt").inputStream()
            //javaClass.getResourceAsStream("/structures/$name.nbt")!!
        }.onSuccess {
            nbtCompound = NbtIo.readCompressed(it, NbtSizeTracker.ofUnlimitedBytes())
        }.onFailure {
            logger.error("Error loading Template $name")
            it.printStackTrace()
        }

        return nbtCompound?.let { this.createTemplate(it) }
    }

    private fun createTemplate(nbtCompound: NbtCompound): StructureTemplate {
        val structureTemplate = StructureTemplate()
        val i = NbtHelper.getDataVersion(nbtCompound, 500)
        structureTemplate.readNbt(
            Registries.BLOCK.readOnlyWrapper,
            DataFixTypes.STRUCTURE.update(Schemas.getFixer(), nbtCompound, i)
        )
        return structureTemplate
    }

    override fun onEndTick(world: ClientWorld?) {
        patternGenerator?.tick(MinecraftClient.getInstance().player!!)
    }
}
