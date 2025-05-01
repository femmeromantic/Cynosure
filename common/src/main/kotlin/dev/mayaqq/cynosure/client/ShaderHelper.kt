package dev.mayaqq.cynosure.client

import dev.mayaqq.cynosure.utils.isModLoaded
import net.irisshaders.iris.api.v0.IrisApi
import java.util.function.BooleanSupplier

public val isShaderPackInUse: Boolean
    get() {
        if (shaderPackInUseSupplier == null) shaderPackInUseSupplier = init()
        return shaderPackInUseSupplier!!.asBoolean
    }

private var shaderPackInUseSupplier: BooleanSupplier? = null

private fun init(): BooleanSupplier = when {
    isModLoaded("iris") || isModLoaded("oculus") ->
        BooleanSupplier { IrisApi.getInstance().isShaderPackInUse }
    Package.getPackage("net.optifine") != null -> optifineShaderInUse()
    else -> BooleanSupplier { false }
}

private fun optifineShaderInUse(): BooleanSupplier {
    try {
        val ofShaders = Class.forName("net.optifine.shaders.Shaders")
        val field = ofShaders.getDeclaredField("shaderPackLoaded")
        field.isAccessible = true
        return BooleanSupplier {
            try {
                field.getBoolean(null)
            } catch (ignored: IllegalAccessException) {
                false
            }
        }
    } catch (ignored: Exception) {
        return BooleanSupplier { false }
    }
}