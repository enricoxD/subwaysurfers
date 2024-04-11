package gg.norisk.subwaysurfers.server.command

import com.mojang.brigadier.arguments.StringArgumentType
import gg.norisk.subwaysurfers.utils.ChatUtils.prefix
import gg.norisk.subwaysurfers.utils.CloudNetUtils
import gg.norisk.subwaysurfers.utils.hasPermission
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextColor
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.text.ClickEvent
import net.silkmc.silk.commands.PermissionLevel
import net.silkmc.silk.commands.command
import net.silkmc.silk.core.text.literalText
import java.io.File
import java.nio.file.Paths

object StructureCommand {
    //LOKL
    private const val PATH = "/home/mcuser/cloudnet/local/templates/subwaysurfers/default/config/subwaysurfers/nbt"

    //lol experimental for hglabor builders only :3 <3
    fun init() {
        command("structure") {
            requires { source -> source.playerOrThrow.hasPermission("hglabor.subwaysurfers.structures") }
            requiresPermissionLevel(PermissionLevel.OWNER)
            literal("upload") {
                argument<String>("templatename", StringArgumentType.greedyString()) { templateName ->
                    runs {
                        val localStructureFile = File("world/generated/minecraft/structures/${templateName()}.nbt")
                        val folder = Paths.get(PATH).toFile()

                        val railsFolder = File(folder, "rails")
                        val structureFile = File(railsFolder, localStructureFile.path.substringAfterLast("structures/"))
                        structureFile.parentFile.mkdirs()
                        structureFile.createNewFile()
                        structureFile.writeBytes(localStructureFile.readBytes())

                        this.source.sendMessage(literalText {
                            text(prefix)
                            text("Successfully uploaded ${templateName()}") {
                                color = 0x89ff21
                            }
                        })
                        CloudNetUtils.broadcastMessage(
                            Component.text("[SubwaySurfers] ").color(TextColor.color(0xf0a211))
                                .append(Component.text(source.name).color(NamedTextColor.RED))
                                .append(Component.text("created ")).color(NamedTextColor.GREEN)
                                .append(Component.text("structure ")).color(NamedTextColor.GRAY)
                                .append(Component.text(templateName())).color(NamedTextColor.GOLD)
                        )
                    }
                }
            }
            literal("delete") {
                argument<String>("templatename", StringArgumentType.greedyString()) { templateName ->
                    runs {
                        val localStructureFile = File("world/generated/minecraft/structures/${templateName()}.nbt")
                        val folder = Paths.get(PATH).toFile()

                        val railsFolder = File(folder, "rails")
                        val structureFile = File(railsFolder, localStructureFile.path.substringAfterLast("structures/"))
                        if (structureFile.exists()) {
                            structureFile.delete()
                        }

                        this.source.sendMessage(literalText {
                            text(prefix)
                            text("Successfully deleted ${templateName()}") {
                                color = 0xf01111
                            }
                        })
                        CloudNetUtils.broadcastMessage(
                            Component.text("[SubwaySurfers] ").color(TextColor.color(0xf0a211))
                                .append(Component.text(source.name).color(NamedTextColor.RED))
                                .append(Component.text("deleted ")).color(NamedTextColor.DARK_RED)
                                .append(Component.text("structure ")).color(NamedTextColor.GRAY)
                                .append(Component.text(templateName())).color(NamedTextColor.GOLD)
                        )
                    }
                }
            }
        }
    }

    fun handleStructureBlockSaveMessage(player: ServerPlayerEntity, templateName: String) {
        val name = templateName.split(":").last()
        if (player.isCreativeLevelTwoOp) {
            player.sendMessage(literalText {
                text(prefix)
                text("Found Structure $name")
                text(" ")
                text("[Upload]") {
                    color = 0x42b6f5
                    clickEvent = ClickEvent(ClickEvent.Action.RUN_COMMAND, "/structure upload $name")
                }
                text(" ")
                text("[Delete]") {
                    color = 0xf54242
                    clickEvent = ClickEvent(ClickEvent.Action.RUN_COMMAND, "/structure delete $name")
                }
            })
        }
    }
}
