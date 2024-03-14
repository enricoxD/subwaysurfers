package gg.norisk.subwaysurfers.client.renderer.entity

import gg.norisk.subwaysurfers.client.model.entity.JetpackModel
import gg.norisk.subwaysurfers.entity.JetpackEntity
import net.minecraft.client.render.entity.EntityRendererFactory
import software.bernie.geckolib.renderer.GeoEntityRenderer

class JetpackRenderer(context: EntityRendererFactory.Context?) : GeoEntityRenderer<JetpackEntity>(context, JetpackModel())
