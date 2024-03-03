package gg.norisk.subwaysurfers.client.model.entity

import gg.norisk.subwaysurfers.SubwaySurfers.toId
import gg.norisk.subwaysurfers.entity.CoinEntity
import gg.norisk.subwaysurfers.entity.HoverboardEntity
import net.minecraft.client.render.RenderLayer
import net.minecraft.util.Identifier
import software.bernie.geckolib.model.DefaultedEntityGeoModel

class HoverboardModel : DefaultedEntityGeoModel<HoverboardEntity>("hoverboard".toId()) {
    // We want this entity to have a translucent render
    override fun getRenderType(animatable: HoverboardEntity, texture: Identifier): RenderLayer {
        return RenderLayer.getEntityTranslucent(getTextureResource(animatable))
    }
}
