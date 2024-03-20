package gg.norisk.subwaysurfers.client.hud

import gg.norisk.subwaysurfers.entity.DriveableEntity
import gg.norisk.subwaysurfers.entity.TrainEntity
import gg.norisk.subwaysurfers.network.c2s.NbtEditDto
import gg.norisk.subwaysurfers.network.c2s.nbtChangePacketC2S
import io.wispforest.owo.ui.base.BaseOwoScreen
import io.wispforest.owo.ui.component.Components
import io.wispforest.owo.ui.component.SlimSliderComponent
import io.wispforest.owo.ui.component.SmallCheckboxComponent
import io.wispforest.owo.ui.container.Containers
import io.wispforest.owo.ui.container.FlowLayout
import io.wispforest.owo.ui.core.*
import net.minecraft.client.MinecraftClient
import net.minecraft.entity.Entity
import net.silkmc.silk.core.text.literal
import net.silkmc.silk.core.text.literalText

class NbtEditorScreen(val entity: Entity) : BaseOwoScreen<FlowLayout>() {
    override fun createAdapter(): OwoUIAdapter<FlowLayout> {
        return OwoUIAdapter.create(this, Containers::verticalFlow)
    }

    override fun build(rootComponent: FlowLayout) {
        rootComponent.alignment(HorizontalAlignment.CENTER, VerticalAlignment.CENTER)
        rootComponent.gap(5)
        rootComponent.child(NbtEditorComponent())
    }

    private inner class NbtEditorComponent(
        horizontalSizing: Sizing = Sizing.fill(40),
        verticalSizing: Sizing = Sizing.fill(90)
    ) : FlowLayout(
        horizontalSizing, verticalSizing, Algorithm.VERTICAL
    ) {
        var variantSlider: SlimSliderComponent? = null
        var moveSpeedSlider: SlimSliderComponent? = null
        var shouldDriveCheckbox: SmallCheckboxComponent? = null

        init {
            surface(Surface.PANEL)
            gap(5)
            padding(Insets.of(10))

            if (entity is TrainEntity) {
                val variantLabel = Components.label("Variant ${entity.variation}".literal).shadow(true)
                variantSlider = Components.slimSlider(SlimSliderComponent.Axis.HORIZONTAL)

                variantSlider!!.horizontalSizing(Sizing.fill(50))
                variantSlider!!.stepSize(1.0)
                variantSlider!!.value(entity.variation.toDouble())
                variantSlider!!.max(TrainEntity.TRAIN_TYPES.toDouble() - 1)
                variantSlider!!.onChanged().subscribe {
                    variantLabel.text("Variant ${variantSlider!!.value().toInt()}".literal)
                }

                child(variantLabel)
                child(variantSlider)
            }

            if (entity is DriveableEntity) {
                shouldDriveCheckbox = Components.smallCheckbox("Should Drive".literal)
                shouldDriveCheckbox!!.checked(entity.shouldDrive)

                val moveSpeedLabel = Components.label("Move Speed ${entity.moveSpeed}".literal).shadow(true)
                moveSpeedSlider = Components.slimSlider(SlimSliderComponent.Axis.HORIZONTAL)

                moveSpeedSlider!!.horizontalSizing(Sizing.fill(50))
                moveSpeedSlider!!.stepSize(0.05)
                moveSpeedSlider!!.value(entity.moveSpeed.toDouble())
                moveSpeedSlider!!.max(1.0)
                moveSpeedSlider!!.onChanged().subscribe {
                    moveSpeedLabel.text("Move Speed ${String.format("%.2f", it)}".literal)
                }

                child(shouldDriveCheckbox)
                child(moveSpeedLabel)
                child(moveSpeedSlider)
            }

            if (entity is DriveableEntity) {
                val copyButton = Components.button("Copy".literal) {
                    shouldDrive = shouldDriveCheckbox?.checked() ?: false
                    moveSpeed = moveSpeedSlider?.value() ?: 0.3
                    variant = variantSlider?.value()?.toInt() ?: 1
                    MinecraftClient.getInstance().player?.sendMessage(literalText {
                        text("Copied.")
                        text(" shouldDrive: $shouldDrive")
                        text(" moveSpeed: $moveSpeed")
                        text(" variant: $variant")
                    })
                }
                child(copyButton)
            }


            val saveButton = Components.button("Save".literal) {
                nbtChangePacketC2S.send(
                    NbtEditDto(
                        entity.uuid,
                        moveSpeedSlider?.value()?.toFloat(),
                        shouldDriveCheckbox?.checked(),
                        variantSlider?.value()?.toInt()
                    )
                )
                this@NbtEditorScreen.close()
            }

            val pasteButton = Components.button("Paste".literal) {
                moveSpeedSlider?.value(moveSpeed)
                variantSlider?.value(variant.toDouble())
                shouldDriveCheckbox?.checked(shouldDrive)

                nbtChangePacketC2S.send(
                    NbtEditDto(
                        entity.uuid,
                        moveSpeedSlider?.value()?.toFloat(),
                        shouldDriveCheckbox?.checked(),
                        variantSlider?.value()?.toInt()
                    )
                )
                this@NbtEditorScreen.close()
            }

            child(saveButton)
            child(pasteButton)
        }
    }

    companion object {
        var shouldDrive: Boolean = false
        var moveSpeed: Double = 0.3
        var variant: Int = 1
    }
}
