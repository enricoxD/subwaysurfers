package gg.norisk.subwaysurfers.client

import gg.norisk.subwaysurfers.SubwaySurfers.logger
import gg.norisk.subwaysurfers.client.lifecycle.ClientGamePreStartLifeCycle.isPreStarting
import gg.norisk.subwaysurfers.event.events.WorldEvents
import gg.norisk.subwaysurfers.network.c2s.trackListRequestPacketC2S
import gg.norisk.subwaysurfers.network.s2c.*
import gg.norisk.subwaysurfers.subwaysurfers.isSubwaySurfers
import gg.norisk.subwaysurfers.subwaysurfers.isSubwaySurfersOrSpectator
import gg.norisk.subwaysurfers.utils.ChatUtils.prefix
import gg.norisk.subwaysurfers.utils.HashUtils
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.minecraft.client.MinecraftClient
import net.minecraft.client.network.ClientPlayerEntity
import net.minecraft.client.option.Perspective
import net.minecraft.text.Text
import net.minecraft.util.math.Vec3d
import net.silkmc.silk.core.text.literalText
import java.io.File

object ClientSettings : ClientTickEvents.EndTick {
    var isRunning: Boolean = false
    var cameraSettings = CameraSettings()
    var startPos: Vec3d? = null
    var ridingTicks = 0
    val baseFolder = File("config", "subwaysurfers/maps").apply { mkdirs() }
    var isInitial = true

    //TODO maybe das downloaden reworken wenn es laggen sollte?
    fun init() {
        WorldEvents.clientJoinWorldEvent.listen { event ->
            disable()
        }

        cameraSettingsPacket.receiveOnClient { packet, context ->
            cameraSettings = packet
        }

        templatePacketS2C.receiveOnClient { packet, context ->
            downloadTrack(packet)
        }

        trackListPacketS2C.receiveOnClient { packet, context ->
            isInitial = baseFolder.listFiles()?.isEmpty() ?: true
            checkTrackList(packet)
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

    private fun checkTrackList(trackList: List<TrackInfo>) {
        val player = MinecraftClient.getInstance().player ?: return
        if (isInitial) {
            player.sendMessage(literalText {
                text(prefix)
                text(Text.translatable("download.initial"))
            })
        } else {
            player.sendMessage(literalText {
                text(prefix)
                text(Text.translatable("download.checking-for-updates"))
            })
        }
        val toDownload = mutableListOf<TrackInfo>()
        for (trackInfo in trackList) {
            val file = File(baseFolder, "${trackInfo.name}.nbt")
            val downloadFlag = !file.exists() || HashUtils.md5(file.readBytes()) != trackInfo.hash
            if (downloadFlag) {
                logger.info("Requesting download of $trackInfo")
                toDownload.add(trackInfo)
            }
        }
        if (toDownload.isNotEmpty()) {
            player.sendMessage(literalText {
                text(prefix)
                text(Text.translatable("download.found-updates", toDownload.size))
            })
            for (trackInfo in toDownload) {
                if (!isInitial) {
                    player.sendMessage(literalText {
                        text(" - ")
                        text(trackInfo.name)
                    })
                }
            }
            trackListRequestPacketC2S.send(toDownload)
        } else {
            player.sendMessage(literalText {
                text(prefix)
                text(Text.translatable("download.no-updates"))
            })
        }
    }

    private fun downloadTrack(templatePacket: TemplatePacket) {
        val player = MinecraftClient.getInstance().player ?: return
        runCatching {
            logger.info("Received ${templatePacket.path} ${templatePacket.bytes.size}")
            File(baseFolder, "${templatePacket.path}.nbt").writeBytes(templatePacket.bytes)
        }.onSuccess {
            if (!isInitial) {
                player.sendMessage(literalText {
                    text(prefix)
                    color = 0x89ff21
                    text(Text.translatable("download.successful", templatePacket.path))
                    text(" [${templatePacket.bytes.size}]") { }
                })
            }
        }.onFailure {
            player.sendMessage(literalText {
                text(prefix)
                color = 0xf01111
                text(Text.translatable("download.error", templatePacket.path))
                text(" [${templatePacket.bytes.size}]") { }
            })
            it.printStackTrace()
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
