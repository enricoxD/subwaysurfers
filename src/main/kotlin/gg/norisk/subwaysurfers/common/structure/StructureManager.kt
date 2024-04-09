package gg.norisk.subwaysurfers.common.structure

import net.minecraft.block.BlockState
import net.minecraft.client.world.ClientWorld
import net.minecraft.datafixer.DataFixTypes
import net.minecraft.datafixer.Schemas
import net.minecraft.entity.Entity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.nbt.NbtCompound
import net.minecraft.nbt.NbtHelper
import net.minecraft.registry.Registries
import net.minecraft.structure.StructurePlacementData
import net.minecraft.structure.StructureTemplate
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3i

interface StructureManager {
    fun readOrLoadTemplate(structureName: String?): StructureTemplate?

    fun placeStructure(
        player: PlayerEntity,
        pos: BlockPos,
        template: StructureTemplate,
        placementData: StructurePlacementData = StructurePlacementData(),
        ignoreAir: Boolean = false,
        blocks: MutableMap<BlockPos, BlockState>? = null,
        entities: MutableSet<Entity>? = null,
    ) {
        (template as ModifiedStructureTemplate).modifiedPlace(
            player.world,
            pos,
            pos.add(Vec3i(0, 1, 0)),
            placementData,
            player.world.random,
            2,
            ignoreAir,
            blocks,
            entities
        )
    }

    fun createTemplate(nbtCompound: NbtCompound): StructureTemplate {
        val structureTemplate = StructureTemplate()
        val i = NbtHelper.getDataVersion(nbtCompound, 500)
        structureTemplate.readNbt(
            Registries.BLOCK.readOnlyWrapper,
            DataFixTypes.STRUCTURE.update(Schemas.getFixer(), nbtCompound, i)
        )
        return structureTemplate
    }
}
