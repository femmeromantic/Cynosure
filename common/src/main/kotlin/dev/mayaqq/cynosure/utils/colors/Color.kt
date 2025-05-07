package dev.mayaqq.cynosure.utils.colors

import com.mojang.datafixers.util.Either
import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import com.teamresourceful.bytecodecs.base.ByteCodec
import dev.mayaqq.cynosure.utils.atMost
import dev.mayaqq.cynosure.utils.codecs.Codecs
import dev.mayaqq.cynosure.utils.codecs.fieldOf
import dev.mayaqq.cynosure.utils.codecs.forGetter
import dev.mayaqq.cynosure.utils.foldToLeft
import dev.mayaqq.cynosure.utils.result.*
import dev.mayaqq.cynosure.utils.toCynosure
import kotlinx.serialization.Serializable

/**
 * Creates a [Color] from integer-based red, green, blue, and optional alpha components (0–255).
 *
 * @param red The red component.
 * @param green The green component.
 * @param blue The blue component.
 * @param alpha The alpha component, defaults to fully opaque.
 * @return A new [Color] instance.
 */
public fun Color(red: Int, green: Int, blue: Int, alpha: Int = 255): Color =
    Color((alpha shl 24) or (red shl 16) or (green shl 8) or blue)

/**
 * Creates a [Color] from float-based red, green, blue, and optional alpha components (0.0f–1.0f).
 *
 * @param red The red component.
 * @param green The green component.
 * @param blue The blue component.
 * @param alpha The alpha component, defaults to fully opaque.
 * @return A new [Color] instance.
 */
public fun Color(red: Float, green: Float, blue: Float, alpha: Float = 1.0f): Color =
    Color((red * 255).toInt(), (green * 255).toInt(), (blue * 255).toInt(), (alpha * 255).toInt())

/**
 * Creates a [Color] from an [UInt] using the given [ColorFormat].
 *
 * @param value The color value.
 * @param format The format to interpret the integer, defaults to [ColorFormat.ARGB].
 * @return A new [Color] instance.
 */
public fun Color(value: UInt, format: ColorFormat = ColorFormat.ARGB): Color = format.toColor(value.toInt())

/**
 * Creates a [Color] from an [Int] using the given [ColorFormat].
 *
 * @param value The color value.
 * @param format The format to interpret the integer as, defaults to [ColorFormat.ARGB].
 * @return A new [Color] instance.
 */
public fun Color(value: Int, format: ColorFormat = ColorFormat.ARGB): Color = format.toColor(value)

/**
 * Represents an immutable color value.
 *
 * @property value The internal integer representation of the color.
 */
@JvmInline
@Serializable
public value class Color(@PublishedApi internal val value: Int) {

    public companion object {

        private val INT_OR_NFLOAT: Codec<Int> = Codec.either(Codec.INT, Codec.FLOAT)
            .xmap(
                { it.toCynosure().foldToLeft { ((it atMost 1.0f) * 255).toInt() } },
                { Either.left(it) }
            )

        @JvmField
        public val RGB_CODEC: Codec<Color> = RecordCodecBuilder.create { it.group(
            INT_OR_NFLOAT fieldOf Color::red,
            INT_OR_NFLOAT fieldOf Color::green,
            INT_OR_NFLOAT fieldOf Color::blue,
            INT_OR_NFLOAT.optionalFieldOf("alpha", 255) forGetter Color::alpha
        ).apply(it, ::Color) }

        public val RGB_LIST_CODEC: Codec<Color> = INT_OR_NFLOAT.listOf().xmap(
            { list -> Color(list[0], list[1], list[2], list[3]) },
            { color -> listOf(color.red, color.green, color.blue, color.alpha) }
        )

        @JvmField
        public val VALUE_CODEC: Codec<Color> = Codec.INT.xmap(::Color, Color::value)

        @JvmField
        public val NAMED_CODEC: Codec<Color> = Codec.STRING.flatXmap(
            { colorByName[it].failureIfNull().toDataResult() },
            { nameByColor[it].failureIfNull().toDataResult() }
        )

        @JvmField
        public val STRING_CODEC: Codec<Color> = Codec.STRING.comapFlatMap({ tryParse(it).toDataResult() }, Color::toHex)

        @JvmField
        public val CODEC: Codec<Color> = Codecs.alternatives(
            RGB_LIST_CODEC,
            RGB_CODEC,
            VALUE_CODEC,
            NAMED_CODEC,
            STRING_CODEC
        )

        @JvmField
        public val NETWORK_CODEC: ByteCodec<Color> = ByteCodec.INT.map(::Color, Color::value)

        /**
         * Parses a color from a given string representation.
         *
         * @param data The color string to parse.
         * @param defaultFormat The default color format if not specified explicitly.
         * @return Parsed `Color` object.
         */
        @OptIn(ExperimentalStdlibApi::class)
        public fun parse(data: String, defaultFormat: ColorFormat = ColorFormat.ARGB): Color = data.lowercase().let {
            when {
                colorByName.contains(it) -> colorByName[it]!!
                it.startsWith('#') -> Color(it.substring(1).hexToInt(), defaultFormat)
                it.startsWith("0x") -> Color(it.substring(2).hexToInt(), defaultFormat)
                else -> {
                    val format = ColorFormat.valueOf(data.substringBefore('(').uppercase())
                    format.parseString(data.substringAfter('(').substringBeforeLast(')'))
                }

            }
        }


        /**
         * Attempts to parse a color from a given string representation.
         *
         * @param from The color string to parse.
         * @param defaultFormat The default color format if parsing fails.
         * @return A [Result] containing either a successful parsed [Color] or a [Throwable].
         */
        @OptIn(ExperimentalStdlibApi::class)
        public fun tryParse(from: String, defaultFormat: ColorFormat = ColorFormat.ARGB): Result<Color> = from.lowercase().let {
            when {
                colorByName.contains(it) -> colorByName[it].failureIfNull()
                it.startsWith('#') -> it.substring(1)
                    .runCatchingSpecific<_, _, NumberFormatException>(String::hexToInt)
                    .map { Color(it, defaultFormat) }
                it.startsWith("0x") -> it.substring(2)
                    .runCatchingSpecific<_, _, NumberFormatException>(String::hexToInt)
                    .map { Color(it, defaultFormat) }
                else -> {
                    val format = runCatchingSpecific<_, IllegalArgumentException> { ColorFormat.valueOf(it.substringBefore('(')) }
                    format.mapCatching { it.parseString(from.substringAfter('(').substringBeforeLast(')')) }
                        .recoverCatching { _ -> defaultFormat.parseString(it) }
                }
            }
        }
    }

    public inline val red: Int get() = (value shr 16) and 0xFF

    public inline val green: Int get() = (value shr 8) and 0xFF

    public inline val blue: Int get() = value and 0xFF

    public inline val alpha: Int get() = value ushr 24

    /**
     * Mixes this color with another color using a default weight of 0.5.
     *
     * @param other The other color to mix with.
     * @return A new [Color] representing the mixed color.
     */
    public infix fun mix(other: Color): Color = this.mix(other, 0.5f)

    /**
     * Mixes this color with another color using a specified weight.
     *
     * @param other The other color to mix with.
     * @param weight The blending weight (0.0-1.0), where 0 is fully this color and 1 is fully the other.
     * @return A new [Color] representing the mixed color.
     */
    public fun mix(other: Color, weight: Float): Color = Color(
        (this.red + (other.red - this.red) * weight).toInt(),
        (this.green + (other.green - this.green) * weight).toInt(),
        (this.blue + (other.blue - this.blue) * weight).toInt(),
        (this.alpha + (other.alpha - this.alpha) * weight).toInt()
    )

    public operator fun times(other: Color): Color = Color(
        this.red * other.red / 255,
        this.green * other.green / 255,
        this.blue * other.blue / 255,
        this.alpha * other.alpha / 255
    )

    public operator fun times(other: UInt): Color = this * Color(other)

    public operator fun plus(other: Color): Color = Color(
        (this.red + other.red) atMost 255,
        (this.green + other.green) atMost 255,
        (this.blue + other.blue) atMost 255,
        (this.alpha + other.alpha) atMost 255
    )

    public inline operator fun component1(): Int = red

    public inline operator fun component2(): Int = green

    public inline operator fun component3(): Int = blue

    public inline operator fun component4(): Int = alpha

    public fun toInt(format: ColorFormat): Int = format.toInt(this)

    public fun toInt(): Int = value

    override fun toString(): String {
        return "Color[$red, $green, $blue, $alpha]"
    }
}