package gg.norisk.subwaysurfers.client.hud

import gg.norisk.subwaysurfers.client.ClientSettings
import gg.norisk.subwaysurfers.common.collectible.*
import gg.norisk.subwaysurfers.subwaysurfers.coins
import gg.norisk.subwaysurfers.subwaysurfers.isSubwaySurfers
import gg.norisk.subwaysurfers.subwaysurfers.multiplier
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback
import net.minecraft.client.MinecraftClient
import net.minecraft.client.font.TextRenderer
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.screen.ingame.InventoryScreen
import net.minecraft.item.Items

object InGameHud : HudRenderCallback {
    fun init() {
        HudRenderCallback.EVENT.register(this)
    }

    override fun onHudRender(drawContext: DrawContext, tickDelta: Float) {
        val textRenderer = MinecraftClient.getInstance().textRenderer
        if (MinecraftClient.getInstance().player?.isSubwaySurfers == true && MinecraftClient.getInstance().currentScreen == null) {
            val tickX = renderTicks(drawContext, textRenderer)
            renderCollectibles(drawContext, textRenderer)
            renderCoins(drawContext, textRenderer)
            renderMultiplier(drawContext, textRenderer, tickX)
        }
    }

    private fun renderMultiplier(
        drawContext: DrawContext, textRenderer: TextRenderer, startX: Int
    ) {
        val multiplier = MinecraftClient.getInstance().player?.multiplier ?: 1
        val multiplierText = "x$multiplier"
        val xOffset = 2
        val x = startX - 2 - textRenderer.getWidth(multiplierText) - xOffset
        val scale = 2f
        drawContext.matrices.push()
        drawContext.matrices.scale(scale, scale, scale)
        drawContext.drawTextWithShadow(textRenderer, multiplierText, x, 2, 16564739)
        drawContext.fill(
            x - xOffset,
            1,
            x + 1 + (textRenderer.getWidth(multiplierText)).toInt(),
            textRenderer.fontHeight + xOffset,
            -1072689136
        )
        drawContext.matrices.pop()
    }

    private fun renderCollectibles(drawContext: DrawContext, textRenderer: TextRenderer) {
        val player = MinecraftClient.getInstance().player ?: return
        val scale = 1.5f

        for ((index, powerup) in collectibles.filterIsInstance<Powerup>().filter { player.hasPowerUp(it) }
            .withIndex()) {
            drawContext.matrices.push()
            drawContext.matrices.scale(scale, scale, scale)
            val y = (index * textRenderer.fontHeight + 5) + 6 * index

            val timeLeft = powerup.getTimeLeft(player)
            val text = if (timeLeft < 0) {
                " ${powerup.id.replaceFirstChar { it.uppercaseChar() }} "
            } else {
                " \uD83D\uDD51 ${powerup.id.replaceFirstChar { it.uppercaseChar() }} ${powerup.getTimeLeft(player)} "
            }

            drawContext.drawTextWithShadow(textRenderer, text, 0, y , -1)
            drawContext.fill(
                0,
                y - 3,
                textRenderer.getWidth(text),
                textRenderer.fontHeight + y + 3,
                -1072689136
            )
            drawContext.matrices.pop()
        }
    }

    private fun renderCoins(drawContext: DrawContext, textRenderer: TextRenderer): Int {
        val coins = (MinecraftClient.getInstance().player?.coins ?: 0).toString()
        val scale = 2f
        val y = 15
        val coinOffset = 16
        val xOffset = -20
        val x =
            (MinecraftClient.getInstance().window.scaledWidth / scale - xOffset - textRenderer.getWidth(coins)).toInt()
        drawContext.matrices.push()
        drawContext.matrices.scale(scale, scale, scale)
        drawContext.drawTextWithShadow(textRenderer, coins, x - coinOffset * 2, y, -1)
        drawContext.fill(
            (x - coinOffset * 2) - 2,
            y - 3,
            x - xOffset - xOffset + (textRenderer.getWidth(coins) * scale).toInt(),
            y + 2 + textRenderer.fontHeight,
            -1072689136
        )
        drawContext.drawItem(
            Items.EMERALD.defaultStack,
            x - coinOffset * 2 + textRenderer.getWidth(coins) - 2,
            +textRenderer.fontHeight + textRenderer.fontHeight / 4
        )
        drawContext.matrices.pop()
        return x
    }

    private fun renderTicks(drawContext: DrawContext, textRenderer: TextRenderer): Int {
        val ticksSinceStart = String.format("%06d", ClientSettings.ridingTicks)
        val scale = 2f
        val xOffset = 2
        val x =
            (MinecraftClient.getInstance().window.scaledWidth / scale - xOffset - textRenderer.getWidth(ticksSinceStart)).toInt()
        drawContext.matrices.push()
        drawContext.matrices.scale(scale, scale, scale)
        drawContext.drawTextWithShadow(textRenderer, ticksSinceStart, x, 2, -1)
        drawContext.fill(
            x - xOffset,
            1,
            x - xOffset - xOffset + (textRenderer.getWidth(ticksSinceStart) * scale).toInt(),
            textRenderer.fontHeight + xOffset,
            -1072689136
        )
        drawContext.matrices.pop()
        return x
    }
}
