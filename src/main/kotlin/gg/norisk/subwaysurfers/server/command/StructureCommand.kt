package gg.norisk.subwaysurfers.server.command

import com.mojang.brigadier.arguments.StringArgumentType
import gg.norisk.subwaysurfers.utils.ChatUtils.prefix
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.text.ClickEvent
import net.silkmc.silk.commands.PermissionLevel
import net.silkmc.silk.commands.command
import net.silkmc.silk.core.text.literal
import net.silkmc.silk.core.text.literalText
import java.io.File
import java.nio.file.Paths

object StructureCommand {
    //TODO permission check
    //LOKL
    private const val PATH = "/home/mcuser/cloudnet/local/templates/subwaysurfers/default/config/subwaysurfers/nbt"

    //lol experimental for hglabor builders only :3 <3
    fun init() {
        command("structure") {
            requiresPermissionLevel(PermissionLevel.OWNER)
            literal("upload") {
                argument<String>("templatename", StringArgumentType.greedyString()) { templateName ->
                    runs {
                        val localStructureFile = getStructureFile(templateName())
                        val folder = Paths.get(PATH).toFile()

                        val railsFolder = File(folder, "rails")
                        val structureFile = File(railsFolder, localStructureFile.path)
                        structureFile.parentFile.mkdirs()
                        structureFile.createNewFile()

                        this.source.sendMessage(literalText {
                            text(prefix)
                            text("Successfully uploaded ${templateName()}") {
                                color = 0x89ff21
                            }
                        })
                    }
                }
            }
            literal("delete") {
                argument<String>("templatename", StringArgumentType.greedyString()) { templateName ->
                    runs {
                        val localStructureFile = getStructureFile(templateName())
                        val folder = Paths.get(PATH).toFile()

                        val railsFolder = File(folder, "rails")
                        val structureFile = File(railsFolder, localStructureFile.path)
                        if (structureFile.exists()) {
                            structureFile.delete()
                        }

                        this.source.sendMessage(literalText {
                            text(prefix)
                            text("Successfully deleted ${templateName()}") {
                                color = 0xf01111
                            }
                        })
                    }
                }
            }
        }
    }

    private fun getStructureFile(templateName: String): File {
        val localStructureFile = File("world/generated/minecraft/structures/${templateName}.nbt")
        return Paths.get(localStructureFile.absolutePath.substringAfterLast("structures/")).toFile()
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
