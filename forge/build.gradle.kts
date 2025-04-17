import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    idea
    alias(libs.plugins.archLoom)
    alias(libs.plugins.ksp)
}

val modid: String by project
val mod_name: String by project

base {
    archivesName = "$modid-forge"
}

loom {
    val aw = project(":common").file("src/main/resources/$modid.accesswidener")
    if (aw.exists()) accessWidenerPath.set(aw)

    @Suppress("UnstableApiUsage")
    mixin { defaultRefmapName.set("${modid}.refmap.json") }

    mods.create(modid) {
        sourceSet(project.sourceSets.main.get())
        sourceSet(project(":common").sourceSets.main.get())
    }

    forge {
        convertAccessWideners = true
        extraAccessWideners.add(loom.accessWidenerPath.get().asFile.name)

        mixinConfig("cynosure.forge.mixins.json")
        mixinConfig("cynosure.mixins.json")
    }
}

repositories {
    maven(url = "https://thedarkcolour.github.io/KotlinForForge/") { name = "Kotlin for forge" }
}

dependencies {
    // Minecraft
    minecraft(libs.minecraft)
    // Mappings
    mappings(loom.officialMojangMappings())
    // Forge
    forge(libs.forge)
    // Kotlin
    implementation(kotlin("reflect"))
    implementation(libs.forge.kotlin)
    implementation(libs.mixin)
    annotationProcessor("org.spongepowered:mixin:0.8.5-SNAPSHOT:processor")
    // Mixin Extras
    implementation(libs.mixinextras)
    annotationProcessor(libs.mixinextras)
    implementation(libs.forge.mixinextras)
    include(libs.forge.mixinextras)

    // ASM
    compileOnly(libs.asm)
    // Javax Annotations
    api(libs.javax.annotations)
    // Bytecodecs
    api(libs.bytecodecs)
    include(libs.bytecodecs)

    // Kotlin
    implementation(libs.kotlin.metadata) { isTransitive = false }
    include(libs.kotlin.metadata) { isTransitive = false }
    //compileOnly(libs.autoservice)
    //ksp(libs.autoservice.ksp)
    compileOnly(projects.common)
}

tasks {
    withType<JavaCompile> { source(project(":common").sourceSets.main.get().allSource) }

    withType<KotlinCompile> { source(project(":common").sourceSets.main.get().allSource) }

    named("sourcesJar", Jar::class) { from(project(":common").sourceSets.main.get().allSource) }

    processResources { from(project(":common").sourceSets.main.get().resources) }
}

publishing.publications {
    create<MavenPublication>("maven") {
        artifactId = base.archivesName.get()
        from(components["java"])
    }
}