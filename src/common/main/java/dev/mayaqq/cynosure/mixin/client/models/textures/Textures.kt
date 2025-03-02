package dev.mayaqq.cynosure.mixin.client.models.textures

import com.mojang.blaze3d.platform.NativeImage
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.runBlocking
import net.minecraft.client.renderer.texture.AbstractTexture
import net.minecraft.client.renderer.texture.Stitcher
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.packs.resources.ResourceManager


public fun loadTextures(map: Collection<ResourceLocation>): ResourceLocation = runBlocking {

    val results: MutableList<Deferred<NativeImage>> = mutableListOf()

    val deferred = repeat(map.size) {

    }



    ResourceLocation("")
}

public data class TextureData(val name: ResourceLocation, val width: Int, val height: Int) : Stitcher.Entry {
    override fun width(): Int {
        TODO("Not yet implemented")
    }

    override fun height(): Int {
        TODO("Not yet implemented")
    }

    override fun name(): ResourceLocation {
        TODO("Not yet implemented")
    }

}

public class StitchedTexture : AbstractTexture() {
    override fun load(p0: ResourceManager) {
        TODO("Not yet implemented")
    }

}