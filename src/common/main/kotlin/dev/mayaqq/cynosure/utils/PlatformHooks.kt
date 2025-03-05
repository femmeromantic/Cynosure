package dev.mayaqq.cynosure.utils

import dev.mayaqq.cynosure.utils.mod.Mod

public expect fun isModLoaded(modid: String): Boolean

public expect fun getMod(modid: String): Mod?

public expect fun currentLoader(): Loader

public enum class Loader {
    FABRIC,
    FORGE,
    UNKNOWN
}