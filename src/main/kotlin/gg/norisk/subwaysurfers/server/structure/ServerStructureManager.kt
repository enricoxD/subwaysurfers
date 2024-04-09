package gg.norisk.subwaysurfers.server.structure

import gg.norisk.subwaysurfers.common.structure.StructureManager
import gg.norisk.subwaysurfers.worldgen.pattern.RailPattern
import net.minecraft.datafixer.DataFixTypes
import net.minecraft.datafixer.Schemas
import net.minecraft.nbt.NbtHelper
import net.minecraft.nbt.NbtIo
import net.minecraft.nbt.NbtSizeTracker
import net.minecraft.registry.Registries
import net.minecraft.structure.StructureTemplate
import java.io.File

object ServerStructureManager : StructureManager {
    val structures = mutableMapOf<String, StructureTemplate>()

    override fun readOrLoadTemplate(structureName: String?): StructureTemplate? {
        //TODO houses are null atm didnt want to fix
        return structures[structureName]
    }

    fun createStructure(railPattern: RailPattern, file: File) {
        val nbtCompound = run { NbtIo.readCompressed(file.inputStream(), NbtSizeTracker.ofUnlimitedBytes()) }
        val structureTemplate = StructureTemplate()
        val i = NbtHelper.getDataVersion(nbtCompound, 500)
        structureTemplate.readNbt(
            Registries.BLOCK.readOnlyWrapper,
            DataFixTypes.STRUCTURE.update(Schemas.getFixer(), nbtCompound, i)
        )
        structures[railPattern.railName] = structureTemplate
    }
}
