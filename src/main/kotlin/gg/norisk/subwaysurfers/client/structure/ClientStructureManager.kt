package gg.norisk.subwaysurfers.client.structure

import com.google.common.cache.CacheBuilder
import gg.norisk.subwaysurfers.SubwaySurfers.logger
import gg.norisk.subwaysurfers.client.ClientSettings
import gg.norisk.subwaysurfers.common.structure.StructureManager
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.client.network.ClientPlayerEntity
import net.minecraft.nbt.NbtCompound
import net.minecraft.nbt.NbtIo
import net.minecraft.nbt.NbtSizeTracker
import net.minecraft.structure.StructureTemplate
import net.minecraft.util.math.BlockPos
import net.silkmc.silk.commands.clientCommand
import net.silkmc.silk.commands.player
import java.io.File
import kotlin.time.Duration.Companion.minutes
import kotlin.time.toJavaDuration

object ClientStructureManager : StructureManager {
    private val structureTemplates =
        CacheBuilder.newBuilder().expireAfterAccess(5.minutes.toJavaDuration()).build<String, StructureTemplate>()

    fun init() {
        if (FabricLoader.getInstance().isDevelopmentEnvironment) {
            clientCommand("clientstructure") {
                argument<String>("name") { templateName ->
                    runs {
                        placeStructure(this.source.player, this.source.player.blockPos, templateName())
                    }
                }
            }
        }
    }

    private fun placeStructure(player: ClientPlayerEntity, pos: BlockPos, name: String) {
        val template = readOrLoadTemplate(name) ?: return logger.error("Error placing structure $name at $pos")
        placeStructure(player, pos, template)
    }

    override fun readOrLoadTemplate(structureName: String?): StructureTemplate? {
        if (structureName == null) return null
        val template = structureTemplates.getIfPresent(structureName)

        if (template != null) {
            return template
        }

        var nbtCompound: NbtCompound? = null

        runCatching {
            File(ClientSettings.baseFolder, "$structureName.nbt").inputStream()
            //javaClass.getResourceAsStream("/structures/$name.nbt")!!
        }.onSuccess {
            nbtCompound = NbtIo.readCompressed(it, NbtSizeTracker.ofUnlimitedBytes())
        }.onFailure {
            logger.error("Error loading Template $structureName")
            it.printStackTrace()
        }

        return nbtCompound?.let { createTemplate(it) }
    }
}
