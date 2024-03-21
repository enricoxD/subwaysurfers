package gg.norisk.subwaysurfers.client

import gg.norisk.subwaysurfers.client.lifecycle.ClientGamePreStartLifeCycle.isPreStarting
import gg.norisk.subwaysurfers.network.s2c.CameraSettings
import gg.norisk.subwaysurfers.network.s2c.cameraSettingsPacket
import gg.norisk.subwaysurfers.network.s2c.startStopPacketS2C
import gg.norisk.subwaysurfers.subwaysurfers.isSubwaySurfers
import gg.norisk.subwaysurfers.subwaysurfers.isSubwaySurfersOrSpectator
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.minecraft.client.MinecraftClient
import net.minecraft.client.network.ClientPlayerEntity
import net.minecraft.client.option.Perspective
import net.minecraft.util.math.Vec3d

//TODO 2 Neue Blöcke (Absperrband und So drunter Rutschen)

object ClientSettings : ClientTickEvents.EndTick {
    var isRunning: Boolean = false
    var cameraSettings = CameraSettings()
    var startPos: Vec3d? = null
    var ridingTicks = 0

    fun init() {
        cameraSettingsPacket.receiveOnClient { packet, context ->
            cameraSettings = packet
        }

        startStopPacketS2C.receiveOnClient { packet, context ->
            val player = context.client.player ?: return@receiveOnClient
            if (!packet.isEnabled) {
                MinecraftClient.getInstance().options.perspective = Perspective.FIRST_PERSON
            }
            isRunning = packet.isEnabled
        }
        ClientTickEvents.END_CLIENT_TICK.register(this)
    }

    fun isEnabled(): Boolean {
        return isRunning
    }

    fun disable() {
        isRunning = false
    }

    fun onToggle(player: ClientPlayerEntity) {
        if (player.isSubwaySurfers) {
            ridingTicks = 0
        }
    }


    fun useSubwayCamera(): Boolean {
        return isPreStarting() || isEnabled() || MinecraftClient.getInstance().player!!.isSubwaySurfersOrSpectator
    }

    override fun onEndTick(client: MinecraftClient) {
        if (client.player?.isSubwaySurfers == true) {
            ridingTicks++
        }
    }
}
