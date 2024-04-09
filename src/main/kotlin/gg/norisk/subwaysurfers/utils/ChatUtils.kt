package gg.norisk.subwaysurfers.utils

import net.silkmc.silk.core.text.literalText

object ChatUtils {
    val prefix = literalText {
        text("[") { color = 0xFFFFFF }
        text("SubwaySurfers") { color = 0xf0a211 }
        text("] ") { color = 0xFFFFFF }
    }
}
