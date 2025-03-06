package dev.mayaqq.cynosure.utils.colors

public fun Color.Companion.fromHSB(hue: Float, saturation: Float, brightness: Float): Color = TODO()

public fun Color.hue(): Float {
    val min = red.coerceAtMost(green).coerceAtMost(blue) / 255f
    val max = red.coerceAtLeast(green).coerceAtLeast(blue) / 255f

    val r = floatRed; val g = floatGreen; val b = floatBlue
    return (when (max) {
        min -> 0f
        r -> (g - b) / (max - min)
        g -> 2f + (b - r) / (max - min)
        b -> 4f + (r - g) / (max - min)
        else -> 0f
    } * 60f).let { if(it < 0) it + 360f else it }
}

public fun Color.saturation(): Float {
    val min = red.coerceAtMost(green).coerceAtMost(blue) / 255f
    val max = red.coerceAtLeast(green).coerceAtLeast(blue) / 255f

    TODO()
}

public fun Color.brightness(): Float = TODO()