package dev.mayaqq.cynosure.utils.colors

internal val namedColors: MutableMap<String, Color> = mutableMapOf()

public object Colors {
    public val WHITE: Color = Color(0xffffffu) named "white"
    public val BLACK: Color = Color(0x000000u) named "black"
    public val BLUE: Color = Color(0x0000ffu) named "blue"
    public val GREEN: Color = Color(0x00ff00u) named "green"
    public val RED: Color = Color(0xff0000u) named "red"
    public val YELLOW: Color = Color(0xffff00u) named "yellow"

    private infix fun Color.named(name: String): Color = also { namedColors[name] = it }
}