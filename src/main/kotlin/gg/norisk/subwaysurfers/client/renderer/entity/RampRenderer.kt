package gg.norisk.subwaysurfers.client.renderer.entity

import gg.norisk.subwaysurfers.client.model.entity.RampModel
import gg.norisk.subwaysurfers.entity.RampEntity
import net.minecraft.client.render.entity.EntityRendererFactory
import software.bernie.geckolib.renderer.GeoEntityRenderer

class RampRenderer(context: EntityRendererFactory.Context) : GeoEntityRenderer<RampEntity>(context, RampModel()) {
    init {
        withScale(1.8f, 1.55f)
    }
}
