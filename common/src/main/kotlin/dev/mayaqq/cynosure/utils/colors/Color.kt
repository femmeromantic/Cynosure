package dev.mayaqq.cynosure.utils.colors

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import com.teamresourceful.bytecodecs.base.ByteCodec
import dev.mayaqq.cynosure.utils.atMost
import dev.mayaqq.cynosure.utils.codecs.Codecs
import dev.mayaqq.cynosure.utils.codecs.fieldOf
import dev.mayaqq.cynosure.utils.codecs.forGetter
import dev.mayaqq.cynosure.utils.result.*
import kotlinx.serialization.Serializable

public fun Color(red: Int, green: Int, blue: Int, alpha: Int = 255): Color =
    Color((alpha shl 24) or (red shl 16) or (green shl 8) or blue)

public fun Color(red: Float, green: Float, blue: Float, alpha: Float = 1.0f): Color =
    Color((red * 255).toInt(), (green * 255).toInt(), (blue * 255).toInt(), (alpha * 255).toInt())

public fun Color(argb: UInt, format: ColorFormat = ColorFormat.ARGB): Color = format.toColor(argb.toInt())

public fun Color(value: Int, format: ColorFormat = ColorFormat.ARGB): Color = format.toColor(value)

@JvmInline
@Serializable
public value class Color(@PublishedApi internal val value: Int) {

    public companion object {

        @JvmField
        public val RGB_CODEC: Codec<Color> = RecordCodecBuilder.create { it.group(
            Codec.INT fieldOf Color::red,
            Codec.INT fieldOf Color::green,
            Codec.INT fieldOf Color::blue,
            Codec.INT.optionalFieldOf("alpha", 255) forGetter Color::alpha
        ).apply(it, ::Color) }

        @JvmField
        public val VALUE_CODEC: Codec<Color> = Codec.INT.xmap(::Color, Color::value)

        @JvmField
        public val NAMED_CODEC: Codec<Color> = Codec.STRING.flatXmap(
            { namedColors[it].failureIfNull().toDataResult() },
            { namedColors.inverse()[it].failureIfNull().toDataResult() }
        )

        @JvmField
        public val STRING_CODEC: Codec<Color> = Codec.STRING.comapFlatMap({ tryParse(it).toDataResult() }, Color::toHex)

        @JvmField
        public val CODEC: Codec<Color> = Codecs.alternatives(
            RGB_CODEC,
            VALUE_CODEC,
            NAMED_CODEC,
            STRING_CODEC
        )

        @JvmField
        public val NETWORK_CODEC: ByteCodec<Color> = ByteCodec.INT.map(::Color, Color::value)

        @OptIn(ExperimentalStdlibApi::class)
        public fun parse(data: String, defaultFormat: ColorFormat = ColorFormat.ARGB): Color = data.lowercase().let {
            when {
                namedColors.contains(it) -> namedColors[it]!!
                it.startsWith('#') -> Color(it.substring(1).hexToInt(), defaultFormat)
                it.startsWith("0x") -> Color(it.substring(2).hexToInt(), defaultFormat)
                else -> {
                    val format = ColorFormat.valueOf(data.substringBefore('(').uppercase())
                    format.parseString(data.substringAfter('(').substringBeforeLast(')'))
                }

            }
        }

        @OptIn(ExperimentalStdlibApi::class)
        public fun tryParse(from: String, defaultFormat: ColorFormat = ColorFormat.ARGB): Result<Color> = from.lowercase().let {
            when {
                namedColors.contains(it) -> namedColors[it].failureIfNull()
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

    public infix fun mix(other: Color): Color = this.mix(other, 0.5f)

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

    public operator fun component1(): Int = red

    public operator fun component2(): Int = green

    public operator fun component3(): Int = blue

    public operator fun component4(): Int = alpha

    public fun toInt(format: ColorFormat): Int = format.toInt(this)

    public fun toInt(): Int = value

    override fun toString(): String {
        return "Color[$red, $green, $blue, $alpha]"
    }
}

// Some extensions just to keep the class itself cleaner
public inline val Color.floatRed: Float get() = red / 255f

public inline val Color.floatGreen: Float get() = green / 255f

public inline val Color.floatBlue: Float get() = blue / 255f

public inline val Color.floatAlpha: Float get() = alpha / 255f

public fun Color.lighter(): Color = mix(White, 0.25f)

public fun Color.darker(): Color = mix(Black, 0.25f)

public fun Color.toUInt(): UInt = toInt().toUInt()

public fun Color.toUInt(format: ColorFormat): UInt = toInt(format).toUInt()

@OptIn(ExperimentalStdlibApi::class)
public fun Color.toHex(): String = toUInt().toHexString(HexFormat.UpperCase)