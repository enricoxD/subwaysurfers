package gg.norisk.subwaysurfers.registry

import gg.norisk.subwaysurfers.SubwaySurfers.toId
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.sound.SoundEvent

object SoundRegistry {
    var WHOOSH = Registry.register(Registries.SOUND_EVENT, "whoosh".toId(), SoundEvent.of("whoosh".toId()))
    var POLICE = Registry.register(Registries.SOUND_EVENT, "police".toId(), SoundEvent.of("police".toId()))

    fun init() {
    }
}
