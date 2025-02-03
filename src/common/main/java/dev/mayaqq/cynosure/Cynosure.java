package dev.mayaqq.cynosure;

import dev.mayaqq.cynosure.mixin.MinecraftServerMixin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Cynosure {

    public static final String MODID = "cynosure";
    public static final String NAME = "Cynosure";
    public static final Logger LOGGER = LoggerFactory.getLogger(NAME);

    public static void init() {
        LOGGER.info("Militech's answer to Arasaka's Soulkiller.");
    }
}
