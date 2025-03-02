package dev.mayaqq.cynosure

import org.slf4j.Logger
import org.slf4j.LoggerFactory

public const val MODID: String = "cynosure"
public const val NAME: String = "Cynosure"

public object Cynosure : Logger by LoggerFactory.getLogger(NAME) {
    public fun init() {
        info("Initializing $NAME")
    }
}