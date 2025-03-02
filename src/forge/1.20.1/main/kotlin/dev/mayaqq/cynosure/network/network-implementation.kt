@file:Suppress("ACTUAL_WITHOUT_EXPECT")
package dev.mayaqq.cynosure.network

import dev.mayaqq.cynosure.network.base.Network
import net.minecraft.resources.ResourceLocation
import java.util.function.BooleanSupplier

public actual fun getNetwork(channel: ResourceLocation, protocolVersion: Int, optional: BooleanSupplier): Network = ForgeNetwork(channel, protocolVersion, optional)