package gg.norisk.subwaysurfers.client.renderer.entity

import gg.norisk.subwaysurfers.client.model.entity.TrafficLightModel
import gg.norisk.subwaysurfers.entity.TrafficLightEntity
import net.minecraft.client.render.entity.EntityRendererFactory
import software.bernie.geckolib.renderer.GeoEntityRenderer

class TrafficLightRenderer(context: EntityRendererFactory.Context?) :
    GeoEntityRenderer<TrafficLightEntity>(context, TrafficLightModel()) {
    init {
        withScale(2f, 2f)
    }
}


