package gg.norisk.subwaysurfers.worldgen.pattern

import java.io.File

data class RailPattern(val path: String, val file: File, val hash: String) {
    val startColors: List<String>
    val endColors: List<String>
    val railName: String

    init {
        val splittedName = path.split("/")
        val startColorsString = splittedName[1]
        val endColorsString = splittedName[2]

        railName = splittedName[3]
        startColors = startColorsString.split("_")
        endColors = endColorsString.split("_")
    }

    override fun toString(): String {
        return "RailPattern(path='$path', startColors=$startColors, endColors=$endColors, railName='$railName')"
    }
}
