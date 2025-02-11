package dev.mayaqq.cynosure.utils.colors

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import dev.mayaqq.cynosure.utils.codecs.alternatives
import dev.mayaqq.cynosure.utils.codecs.toDataResult
import org.checkerframework.checker.guieffect.qual.UI


@JvmInline
public value class Color(private val value: Int) {

    public companion object {
        public val CODEC: Codec<Color> = alternatives(
            Codec.INT.xmap(::Color, Color::value),
            Codec.STRING.comapFlatMap({ parse(it).toDataResult() }, Color::toHex),
            RecordCodecBuilder.create { it.group(
                Codec.INT.fieldOf("red").forGetter(Color::red),
                Codec.INT.fieldOf("green").forGetter(Color::green),
                Codec.INT.fieldOf("blue").forGetter(Color::blue),
                Codec.INT.optionalFieldOf("alpha", 255).forGetter(Color::alpha)
            ).apply(it, ::Color) }
        )

        @OptIn(ExperimentalStdlibApi::class)
        public fun parse(from: String, format: ColorFormat = ColorFormat.ARGB): Result<Color> = when {
            namedColors.contains(from) -> Result.success(namedColors[from]!!)
            from.startsWith('#') -> from.substring(1).runCatching(String::hexToInt).map { Color(it, format) }
            from.startsWith("0x") -> from.substring(2).runCatching(String::hexToInt).map { Color(it, format) }
//            from.startsWith('[') && from.endsWith(']') -> from.substring(1..from.length - 2).split(',')
//                .runCatching { Color(this[0].toInt(), this[1].toInt(), this[2].toInt(), if(this.size >= 4) this[3].toInt() else 255) }
            else -> Result.failure(IllegalArgumentException("Invalid color format"))
        }
    }

    public constructor(value: UInt) : this(value.toInt())

    public constructor(red: Int, green: Int, blue: Int, alpha: Int = 255)
            : this((alpha shl 24) or (red shl 16) or (green shl 8) or blue)

    public constructor(red: Float, green: Float, blue: Float, alpha: Float = 1.0f)
            : this((red * 255).toInt(), (green * 255).toInt(), (blue * 255).toInt(), (alpha * 255).toInt())

    public val red: Int get() = (value shr 16) and 0xFF

    public val green: Int get() = (value shr 8) and 0xFF

    public val blue: Int get() = value and 0xFF

    public val alpha: Int get() = value ushr 24

    public val argb: UInt get() = value.toUInt()

    public fun hue(): Float {
        val min = red.coerceAtMost(green).coerceAtMost(blue) / 255f
        val max = red.coerceAtLeast(green).coerceAtLeast(blue) / 255f

        val r = floatRed(); val g = floatGreen(); val b = floatBlue()
        return (when (max) {
            min -> 0f
            r -> (g - b) / (max - min)
            g -> 2f + (b - r) / (max - min)
            b -> 4f + (r - g) / (max - min)
            else -> 0f
        } * 60f).let { if(it < 0) it + 360f else it }
    }

    public fun saturation(): Float = TODO()

    public fun brightness(): Float = TODO()

    public fun floatRed(): Float = red / 255f

    public fun floatGreen(): Float = green / 255f

    public fun floatBlue(): Float = blue / 255f

    public fun floatAlpha(): Float = alpha / 255f

    public infix fun mix(other: Color): Color = this.mix(other, 0.5f)

    public fun mix(other: Color, weight: Float): Color = Color(
        (this.red + (other.red - this.red) * weight).toInt(),
        (this.green + (other.green - this.green) * weight).toInt(),
        (this.blue + (other.blue - this.blue) * weight).toInt(),
        (this.alpha + (other.alpha - this.alpha) * weight).toInt()
    )

    public fun lighter(): Color = mix(Colors.WHITE, 0.25f)

    public fun darker(): Color = mix(Colors.BLACK, 0.25f)

    public operator fun component0(): Int = red

    public operator fun component1(): Int = green

    public operator fun component2(): Int = blue

    public operator fun component3(): Int = alpha

    @OptIn(ExperimentalStdlibApi::class)
    public fun toHex(): String = value.toHexString(HexFormat.UpperCase)

    override fun toString(): String {
        return "Color[$red, $green, $blue, $alpha]"
    }
}

private val namedColors: MutableMap<String, Color> = mutableMapOf()

public object Colors {
    public val WHITE: Color = Color(0xffffff) named "white"
    public val BLACK: Color = Color(0x000000) named "black"
    public val BLUE: Color = Color(0x0000ff) named "blue"
    public val GREEN: Color = Color(0x00ff00) named "green"
    public val RED: Color = Color(0xff0000) named "red"
    public val YELLOW: Color = Color(0xffff00) named "yellow"

    private infix fun Color.named(name: String): Color = also { namedColors[name] = it }
}