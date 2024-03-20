package gg.norisk.subwaysurfers.item

import gg.norisk.subwaysurfers.entity.DriveableEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.util.Hand
import net.minecraft.util.TypedActionResult
import net.minecraft.util.math.Box
import net.minecraft.world.World

class RemoteDetonatorItem(settings: Settings) : Item(settings) {
    override fun hasGlint(itemStack: ItemStack): Boolean {
        return true
    }

    override fun use(world: World, playerEntity: PlayerEntity, hand: Hand): TypedActionResult<ItemStack> {
        if (world.isClient) {
            val drivableEntities = world.getOtherEntities(
                playerEntity,
                Box.from(playerEntity.pos).expand(25.0)
            ) { it is DriveableEntity }.filterIsInstance<DriveableEntity>()

            for (drivableEntity in drivableEntities) {
                drivableEntity.isVisualTestDrive = !drivableEntity.isVisualTestDrive
            }
        }
        return super.use(world, playerEntity, hand)
    }
}
