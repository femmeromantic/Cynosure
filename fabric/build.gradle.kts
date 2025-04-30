import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    alias(libs.plugins.fabricLoom)
    alias(libs.plugins.ksp)
    alias(libs.plugins.blossom)
}

val modid: String by project
val mod_name: String by project

base {
    archivesName = "$modid-fabric"
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
}

dependencies {
    minecraft(libs.minecraft)
    mappings(loom.officialMojangMappings())
    modImplementation(libs.fabric)
    modImplementation(libs.fabric.api)
    modImplementation(libs.fabric.kotlin)
    modImplementation(libs.fabric.kritter)
    include(libs.fabric.kritter)
    api(libs.javax.annotations)
    api(libs.bytecodecs)
    include(libs.bytecodecs)
    implementation(libs.kotlin.metadata) {
        isTransitive = false
    }
    include(libs.kotlin.metadata)
//    compileOnly(libs.autoservice)
//    ksp(libs.autoservice.ksp)
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