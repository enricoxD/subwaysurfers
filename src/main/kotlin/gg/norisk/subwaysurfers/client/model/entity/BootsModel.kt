package gg.norisk.subwaysurfers.client.model.entity

import gg.norisk.subwaysurfers.SubwaySurfers.toId
import gg.norisk.subwaysurfers.entity.BootsEntity
import gg.norisk.subwaysurfers.entity.CoinEntity
import net.minecraft.client.render.RenderLayer
import net.minecraft.util.Identifier
import software.bernie.geckolib.model.DefaultedEntityGeoModel

class BootsModel : DefaultedEntityGeoModel<BootsEntity>("boots".toId()) {
    // We want this entity to have a translucent render
    override fun getRenderType(animatable: BootsEntity, texture: Identifier): RenderLayer {
        return RenderLayer.getEntityTranslucent(getTextureResource(animatable))
    }
}

