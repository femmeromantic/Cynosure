package dev.mayaqq.cynosure.utils.bytecodecs

import com.teamresourceful.bytecodecs.base.ByteCodec
import com.teamresourceful.bytecodecs.base.`object`.ObjectByteCodec
import com.teamresourceful.bytecodecs.utils.ByteBufUtils
import dev.mayaqq.cynosure.utils.codecs.fieldOf
import io.netty.buffer.ByteBuf
import io.netty.buffer.ByteBufInputStream
import io.netty.buffer.ByteBufOutputStream
import net.minecraft.core.*
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.core.registries.Registries
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.NbtAccounter
import net.minecraft.nbt.NbtIo
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.Item
import net.minecraft.world.level.ChunkPos
import net.minecraft.world.level.Level
import net.minecraft.world.phys.Vec3
import org.joml.Vector3f
import java.io.IOException
import java.util.*

/**
 * Code modified from [Resourcefullib](https://github.com/Team-Resourceful/Resourcefullib) by Team Resourceful.
 * Licensed under MIT
 */
public object ExtraByteCodecs {

    @JvmField
    public val RESOURCE_LOCATION: ByteCodec<ResourceLocation> =
        ByteCodec.STRING.map(::ResourceLocation, ResourceLocation::toString)

    @JvmField
    public val DIMENSION: ByteCodec<ResourceKey<Level>> = resourceKey(Registries.DIMENSION)

    @JvmField
    public val BLOCK_POS: ByteCodec<BlockPos> = ByteCodec.LONG.map(BlockPos::of, BlockPos::asLong)

    @JvmField
    public val CHUNK_POS: ByteCodec<ChunkPos> = ByteCodec.LONG.map(::ChunkPos, ChunkPos::toLong)

    @JvmField
    public val SECTION_POS: ByteCodec<SectionPos> = ByteCodec.LONG.map(SectionPos::of, SectionPos::asLong)

    @JvmField
    public val GLOBAL_POS: ByteCodec<GlobalPos> = ObjectByteCodec.create(
        DIMENSION fieldOf GlobalPos::dimension,
        BLOCK_POS fieldOf GlobalPos::pos,
        GlobalPos::of
    )

    @JvmField
    public val VECTOR_3F: ByteCodec<Vector3f> = ObjectByteCodec.create(
        ByteCodec.FLOAT fieldOf { it.x },
        ByteCodec.FLOAT fieldOf { it.y },
        ByteCodec.FLOAT fieldOf { it.z },
        ::Vector3f
    )

    @JvmField
    public val VEC3: ByteCodec<Vec3> = ObjectByteCodec.create(
        ByteCodec.DOUBLE fieldOf { it.x },
        ByteCodec.DOUBLE fieldOf { it.y },
        ByteCodec.DOUBLE fieldOf { it.z },
        ::Vec3
    )

    @JvmField
    public val NULLABLE_COMPOUND_TAG: ByteCodec<CompoundTag?> = CompoundTagByteCodec
        .map(fun(value) = value.orElse(null), fun(tag) = Optional.ofNullable(tag))

    @JvmField
    public val NONNULL_COMPOUND_TAG: ByteCodec<CompoundTag> = CompoundTagByteCodec
        .map(Optional<CompoundTag>::orElseThrow, fun(tag) = Optional.of(tag))

    @JvmField
    public val COMPOUND_TAG: ByteCodec<Optional<CompoundTag>> = CompoundTagByteCodec

    @JvmField
    public val COMPONENT: ByteCodec<Component> = ByteCodec.STRING_COMPONENT
        .map(Component.Serializer::fromJson, Component.Serializer::toJson)

    @JvmField
    public val ITEM: ByteCodec<Item> = registry(BuiltInRegistries.ITEM)

    @JvmStatic
    public fun <T, R : Registry<T>> resourceKey(registry: ResourceKey<R>): ByteCodec<ResourceKey<T>> {
        return RESOURCE_LOCATION.map(fun(id) = ResourceKey.create(registry, id), ResourceKey<T>::location)
    }

    @JvmStatic
    public fun <T> registry(map: IdMap<T>): ByteCodec<T> {
        return IdMapByteCodec(map)
    }

    @JvmStatic
    public fun <T, R : Registry<T>> registry(key: ResourceKey<R>): ByteCodec<T> {
        return LazyByteCodec { registry((BuiltInRegistries.REGISTRY as Registry<R>).get(key)!!) }
    }

}

public object CompoundTagByteCodec : ByteCodec<Optional<CompoundTag>> {
    override fun encode(value: Optional<CompoundTag>, buffer: ByteBuf) {
        if (value.isEmpty) {
            buffer.writeByte(0)
        } else {
            try {
                NbtIo.write(value.get(), ByteBufOutputStream(buffer))
            } catch (exception: IOException) {
                throw RuntimeException(exception)
            }
        }
    }

    override fun decode(buffer: ByteBuf): Optional<CompoundTag> {
        val i = buffer.readerIndex()
        val b = buffer.readByte()
        return if (b.toInt() == 0) {
            Optional.empty()
        } else {
            buffer.readerIndex(i)

            try {
                Optional.of(NbtIo.read(ByteBufInputStream(buffer), NbtAccounter(2097152L)))
            } catch (exception: IOException) {
                throw RuntimeException(exception)
            }
        }
    }
}

public class IdMapByteCodec<T>(public val map: IdMap<T>) : ByteCodec<T> {
    override fun encode(value: T, buffer: ByteBuf) {
        val id = value?.let(map::getId) ?: -1
        require(id != -1) { "Can't find id for '$value' in map $map" }
        ByteBufUtils.writeVarInt(buffer, id)
    }

    override fun decode(buffer: ByteBuf): T {
        val id = ByteBufUtils.readVarInt(buffer)
        return requireNotNull(map.byId(id)) { "Cant find value for id '$id' in map $map" }
    }
}