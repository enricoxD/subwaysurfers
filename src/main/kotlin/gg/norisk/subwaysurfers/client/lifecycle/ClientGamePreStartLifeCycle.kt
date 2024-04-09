package gg.norisk.subwaysurfers.client.lifecycle

import gg.norisk.subwaysurfers.client.ClientSettings
import gg.norisk.subwaysurfers.client.structure.ClientStructureManager
import gg.norisk.subwaysurfers.client.world.ClientPatternGenerator
import gg.norisk.subwaysurfers.client.world.ClientRailPatternGenerator
import gg.norisk.subwaysurfers.common.world.AbstractPatternGenerator
import gg.norisk.subwaysurfers.extensions.toBlockPos
import gg.norisk.subwaysurfers.extensions.toStack
import gg.norisk.subwaysurfers.network.s2c.preStartS2C
import gg.norisk.subwaysurfers.subwaysurfers.SubwaySurfer
import gg.norisk.subwaysurfers.subwaysurfers.isPreStarting
import net.minecraft.client.MinecraftClient
import net.minecraft.client.option.Perspective
import net.minecraft.sound.SoundEvents
import net.minecraft.util.BlockMirror
import net.minecraft.util.math.Vec3d
import net.silkmc.silk.core.kotlin.ticks
import net.silkmc.silk.core.task.mcCoroutineTask
import net.silkmc.silk.core.text.literalText
import java.util.*

object ClientGamePreStartLifeCycle {

    fun init() {
        preStartS2C.receiveOnClient { packet, context ->
            val player = context.client.player ?: return@receiveOnClient
            val subwaySurfer = player as? SubwaySurfer? ?: return@receiveOnClient
            val pattern = packet.initialPattern
            val startPos = Vec3d(packet.startPos.x, packet.startPos.y, packet.startPos.z)

            player.leftWallPatternGenerator = ClientPatternGenerator(
                startPos = startPos.add(
                    AbstractPatternGenerator.leftOffset + AbstractPatternGenerator.offset,
                    -1.0,
                    0.0
                ).toBlockPos(),
                structureManager = ClientStructureManager,
                patternStack = Stack<Stack<String>>().apply { add(pattern.left.toStack()) }
            )
            player.railPatternGenerator = ClientRailPatternGenerator(
                startPos = startPos.add(0.0, -1.0, 0.0).toBlockPos(),
                structureManager = ClientStructureManager,
                patternStack = Stack<Stack<String>>().apply { add(pattern.middle.toStack()) }
            )
            player.rightWallPatternGenerator = ClientPatternGenerator(
                startPos = startPos.add(
                    AbstractPatternGenerator.rightOffset - AbstractPatternGenerator.offset,
                    -1.0,
                    0.0
                ).toBlockPos(),
                structureManager = ClientStructureManager,
                patternStack = Stack<Stack<String>>().apply { add(pattern.right.toStack()) },
                mirror = BlockMirror.FRONT_BACK
            )

            ClientSettings.cameraSettings = packet.cameraSettings
            ClientSettings.startPos = startPos
            player.yaw = 0f
            player.pitch = 0f
            MinecraftClient.getInstance().options.perspective = Perspective.THIRD_PERSON_BACK

            createStartTimer(packet.howLong)
        }
    }

    fun createStartTimer(howLong: Long) {
        mcCoroutineTask(client = true, sync = true, howOften = howLong, period = 20.ticks) { task ->
            val player = MinecraftClient.getInstance().player ?: return@mcCoroutineTask
            val timeLeft = task.counterDownToOne
            val titleColor = when {
                timeLeft > howLong * 0.80 -> 0xff0303 // Grün für die ersten 66%
                timeLeft > howLong * 0.50 -> 0xffee03 // Gelb für die nächsten 33%
                else -> 0x1aff00 // Rot für die letzten 33%
            }
            val text = literalText(task.counterDownToOne.toString()) {
                bold = true
                color = titleColor
            }
            MinecraftClient.getInstance().inGameHud.setTitle(text)
            MinecraftClient.getInstance().inGameHud.setTitleTicks(5, 20, 5)

            val pitch = when {
                timeLeft > howLong * 0.80 -> 0.5f // Tieferer Ton zu Beginn
                timeLeft > howLong * 0.50 -> 1.0f // Mittlerer Ton in der Mitte
                else -> 1.5f // Höherer Ton gegen Ende
            }

            player.playSound(
                SoundEvents.BLOCK_NOTE_BLOCK_BELL.comp_349(),
                1.0f,
                pitch
            )
        }
    }

    fun isPreStarting(): Boolean {
        return MinecraftClient.getInstance().player?.isPreStarting == true
    }
}
