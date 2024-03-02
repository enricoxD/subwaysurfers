package gg.norisk.subwaysurfers.client.hud

import gg.norisk.subwaysurfers.network.c2s.homePacketC2S
import gg.norisk.subwaysurfers.network.c2s.restartPacketC2S
import io.wispforest.owo.ui.base.BaseOwoScreen
import io.wispforest.owo.ui.component.ButtonComponent
import io.wispforest.owo.ui.component.Components
import io.wispforest.owo.ui.component.LabelComponent
import io.wispforest.owo.ui.container.Containers
import io.wispforest.owo.ui.container.FlowLayout
import io.wispforest.owo.ui.core.*
import io.wispforest.owo.ui.util.UISounds
import net.minecraft.client.MinecraftClient
import net.minecraft.text.Text
import net.silkmc.silk.core.text.literal
import net.silkmc.silk.core.text.literalText
import kotlin.random.Random

class GameOverScreen : BaseOwoScreen<FlowLayout>() {
    override fun createAdapter(): OwoUIAdapter<FlowLayout> {
        return OwoUIAdapter.create(this, Containers::verticalFlow)
    }

    override fun build(rootComponent: FlowLayout) {
        rootComponent.alignment(HorizontalAlignment.CENTER, VerticalAlignment.CENTER)
        rootComponent.gap(5)
        rootComponent.surface(Surface.blur(30f, 30f))
        rootComponent.child(SubwaySurferComponent())
    }

    private inner class SubwaySurferComponent(
        horizontalSizing: Sizing = Sizing.fill(40), verticalSizing: Sizing = Sizing.fill(90)
    ) : FlowLayout(
        horizontalSizing, verticalSizing, Algorithm.VERTICAL
    ) {
        val header = HeaderComponent(
            listOf(
                TabComponent("ui.gameover.stats", ::onTabClick),
                TabComponent("ui.gameover.friends", ::onTabClick),
                TabComponent("ui.gameover.global", ::onTabClick)
            )
        )
        val base = Containers.horizontalFlow(Sizing.fill(), Sizing.fill(80))
        val footer = FooterComponent(listOf(Components.button(Text.translatable("ui.gameover.home")) {
            homePacketC2S.send(Unit)
            this@GameOverScreen.close()
        }.apply {
            horizontalSizing(
                Sizing.fill(40)
            )
        }, Components.button(Text.translatable("ui.gameover.play")) {
            restartPacketC2S.send(Unit)
            this@GameOverScreen.close()
        }.apply {
            horizontalSizing(
                Sizing.fill(40)
            )
        }))

        init {
            surface(Surface.PANEL)

            padding(Insets.of(10))

            //header.surface(Surface.outline(Color.RED.argb()))
            //base.surface(Surface.outline(Color.RED.argb()))
            //footer.surface(Surface.outline(Color.RED.argb()))

            child(header)
            child(base)
            child(footer)

            onTabClick(header.tabs.first())
        }

        private fun onTabClick(tabComponent: TabComponent) {
            for (tab in header.tabs) {
                tab.isActive = tab == tabComponent
            }

            UISounds.playInteractionSound()

            val component = when (tabComponent.textKey) {
                "ui.gameover.stats" -> GameOverComponent()
                "ui.gameover.friends" -> Components.label("FRIENDS".literal)
                "ui.gameover.global" -> Components.label("GLOBAL".literal)
                else -> {
                    Components.label("ERROR".literal)
                }
            }

            base.clearChildren()
            base.child(component)
        }
    }

    private class HeaderComponent(
        val tabs: List<TabComponent>, horizontalSizing: Sizing = Sizing.fill(), verticalSizing: Sizing = Sizing.fill(10)
    ) : FlowLayout(
        horizontalSizing, verticalSizing, Algorithm.HORIZONTAL
    ) {
        init {
            alignment(HorizontalAlignment.CENTER, VerticalAlignment.CENTER)
            gap(10)
            for (tab in tabs) {
                child(tab)
            }
        }
    }

    private class FooterComponent(
        val tabs: List<ButtonComponent>,
        horizontalSizing: Sizing = Sizing.fill(),
        verticalSizing: Sizing = Sizing.fill(10)
    ) : FlowLayout(
        horizontalSizing, verticalSizing, Algorithm.HORIZONTAL
    ) {
        init {
            alignment(HorizontalAlignment.CENTER, VerticalAlignment.CENTER)
            gap(10)
            for (tab in tabs) {
                child(tab)
            }
        }
    }


    private class TabComponent(
        val textKey: String,
        val onHeaderClick: (TabComponent) -> Unit,
        horizontalSizing: Sizing = Sizing.content(),
        verticalSizing: Sizing = Sizing.content()
    ) : FlowLayout(
        horizontalSizing, verticalSizing, Algorithm.HORIZONTAL
    ) {
        var isActive: Boolean = false
        val label: LabelComponent

        init {
            label = Components.label(literalText {
                text(Text.translatable(textKey)) { }
                bold = true
            })
            label.shadow(true)
            child(label)
        }

        override fun onMouseDown(mouseX: Double, mouseY: Double, button: Int): Boolean {
            val superResult = super.onMouseDown(mouseX, mouseY, button)
            if (!superResult) {
                onHeaderClick.invoke(this)
                return true
            } else {
                return superResult
            }
        }

        override fun draw(
            context: OwoUIDrawContext?, mouseX: Int, mouseY: Int, partialTicks: Float, delta: Float
        ) {
            super.draw(context, mouseX, mouseY, partialTicks, delta)
            if (isActive) {
                label.color(Color.ofArgb(0xe5e3df))
            } else {
                label.color(Color.ofArgb(0x8d8a85))
            }
        }
    }

    private class GameOverComponent(
        horizontalSizing: Sizing = Sizing.fill(),
        verticalSizing: Sizing = Sizing.fill()
    ) : FlowLayout(
        horizontalSizing, verticalSizing, Algorithm.VERTICAL
    ) {
        init {
            val playerDisplay = Components.entity(Sizing.fixed(40), MinecraftClient.getInstance().player)
            playerDisplay.lookAtCursor(true)
            playerDisplay.allowMouseRotation(true)
            playerDisplay.showNametag(true)


            val horizontalWrapper = Containers.horizontalFlow(Sizing.fill(), Sizing.fill())
            horizontalWrapper.child(playerDisplay)
            horizontalWrapper.gap(25)

            val verticalWrapper = Containers.verticalFlow(Sizing.content(), Sizing.content())
            val score = PrettyLabelComponent("ui.gameover.score", Random.nextInt(1, 100).toString())
            val coins = PrettyLabelComponent("ui.gameover.coins", Random.nextInt(1, 100).toString())
            verticalWrapper.child(score)
            verticalWrapper.child(coins)
            verticalWrapper.gap(5)

            horizontalWrapper.child(verticalWrapper)
            horizontalWrapper.alignment(HorizontalAlignment.CENTER, VerticalAlignment.CENTER)

            child(horizontalWrapper)
        }
    }

    private class PrettyLabelComponent(
        titleKey: String,
        baseText: String,
        horizontalSizing: Sizing = Sizing.fill(30),
        verticalSizing: Sizing = Sizing.content()
    ) : FlowLayout(
        horizontalSizing, verticalSizing, Algorithm.VERTICAL
    ) {
        private val title: LabelComponent
        private val base: LabelComponent

        init {
            title = Components.label(Text.translatable(titleKey)).shadow(true)
            base = Components.label(baseText.literal).shadow(true)

            base.text(literalText(baseText) {
                bold = true
                color = 0xffbf00
            })

            alignment(HorizontalAlignment.CENTER, VerticalAlignment.CENTER)

            padding(Insets.of(5))

            child(title)
            child(base)

            surface(Surface.PANEL)
        }
    }

    override fun shouldPause(): Boolean = false
    override fun shouldCloseOnEsc(): Boolean = false
}
