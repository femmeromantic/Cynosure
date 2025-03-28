package dev.mayaqq.cynosure.utils

import dev.mayaqq.cynosure.utils.mod.Mod
import net.msrandom.stub.Stub

@Stub
public expect fun isModLoaded(modid: String): Boolean

@Stub
public expect fun getMod(modid: String): Mod?

@Stub
public expect fun currentLoader(): Loader

public enum class Loader {
    FABRIC,
    FORGE,
    UNKNOWN
}