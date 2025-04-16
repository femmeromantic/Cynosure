plugins {
    idea
    java
    kotlin("jvm")
    alias(libs.plugins.vanillaGradle)
}

val modid: String by project
val mod_name: String by project

base {
    archivesName = "$modid-common"
}

minecraft {
    version(libs.versions.minecraft.get())
    if (file("src/main/resources/${modid}.accesswidener").exists())
        accessWideners(file("src/main/resources/${modid}.accesswidener"))

}

dependencies {
    compileOnly(libs.mixin)
    compileOnly(libs.mixinextras)
    compileOnly(libs.asm)
    compileOnly(libs.asm.analysis)
    compileOnly(libs.koffee)
    compileOnly(libs.kotlin.metadata)  {
        isTransitive = false
    }
    api(libs.javax.annotations)
    api(libs.bytecodecs)
    api(libs.kotlinx.serialization)
    api(libs.kotlinx.coroutines)
    implementation(kotlin("reflect"))
}

publishing.publications {
    create<MavenPublication>("maven") {
        artifactId = base.archivesName.get()
        from(components["java"])
    }
}