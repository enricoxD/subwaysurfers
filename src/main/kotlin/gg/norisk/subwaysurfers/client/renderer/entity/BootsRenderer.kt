package gg.norisk.subwaysurfers.client.renderer.entity

import gg.norisk.subwaysurfers.client.model.entity.BootsModel
import gg.norisk.subwaysurfers.entity.BootsEntity
import net.minecraft.client.render.entity.EntityRendererFactory
import software.bernie.geckolib.renderer.GeoEntityRenderer

class BootsRenderer(context: EntityRendererFactory.Context?) : GeoEntityRenderer<BootsEntity>(context, BootsModel())
