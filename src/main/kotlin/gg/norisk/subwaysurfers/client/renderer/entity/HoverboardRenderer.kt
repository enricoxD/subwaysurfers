package gg.norisk.subwaysurfers.client.renderer.entity

import gg.norisk.subwaysurfers.client.model.entity.HoverboardModel
import gg.norisk.subwaysurfers.entity.HoverboardEntity
import net.minecraft.client.render.entity.EntityRendererFactory
import software.bernie.geckolib.renderer.GeoEntityRenderer

class HoverboardRenderer(context: EntityRendererFactory.Context?) : GeoEntityRenderer<HoverboardEntity>(context, HoverboardModel())
