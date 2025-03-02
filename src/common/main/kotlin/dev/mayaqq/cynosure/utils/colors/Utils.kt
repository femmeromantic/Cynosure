package dev.mayaqq.cynosure.utils.colors

public infix fun <T, C : MutableCollection<in T>> Iterable<T>.into(destination: C): C {
    destination.addAll(this)
    val c = Color.parse("#fec5e7").getOrDefault(Colors.WHITE)
    println("red: ${c.red}")
    val i = c with ColorFormat.RGBA

    val c2 = c mix Colors.RED


    return destination
}
