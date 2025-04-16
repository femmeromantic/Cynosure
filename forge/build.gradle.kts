import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    idea
    alias(libs.plugins.forgeGradle)
    alias(libs.plugins.mixin)
    alias(libs.plugins.ksp)
}

val modid: String by project
val mod_name: String by project

base {
    archivesName = "${modid}-forge"
}

mixin {
    add(sourceSets.main.get(), "$modid.refmap.json")
    add(project(":common").sourceSets.main.get(), "$modid.refmap.json")
    config("$modid.mixins.json")
    config("$modid.forge.mixins.json")
}

minecraft {
    mappings(provider { "official" }, libs.versions.minecraft)
    copyIdeResources = true

    runs {
        create("client") {
            workingDirectory(project.file("run"))
            ideaModule("${rootProject.name}.${project.name}.main")
            taskName("Client")
            property("mixin.env.remapRefMap", "true")
            property("mixin.env.refMapRemappingFile", "${projectDir}/build/createSrgToMcp/output.srg")
            mods {
                create("modRun") {
                    source(sourceSets.main.get())
                    source(project(":common").sourceSets.main.get())
                }
            }
        }

        create("server") {
            workingDirectory(project.file("run"))
            ideaModule("${rootProject.name}.${project.name}.main")
            taskName("Server")
            property("mixin.env.remapRefMap", "true")
            property("mixin.env.refMapRemappingFile", "${projectDir}/build/createSrgToMcp/output.srg")
            mods {
                create("modServerRun") {
                    source(sourceSets.main.get())
                    source(project(":common").sourceSets.main.get())
                }
            }
        }
    }
}

repositories {
    maven(url = "https://thedarkcolour.github.io/KotlinForForge/") { name = "Kotlin for forge" }
}

jarJar {
    enable()
}

dependencies {
    minecraft(libs.forge)
    implementation(kotlin("reflect"))
    implementation(libs.forge.kotlin)
    implementation(libs.mixin)
    annotationProcessor("org.spongepowered:mixin:0.8.5-SNAPSHOT:processor")
    implementation(libs.mixinextras)
    annotationProcessor(libs.mixinextras)
    implementation(libs.forge.mixinextras)
    jarJar(libs.forge.mixinextras) {
        jarJar.ranged(this, "[0.4.1,)")
    }

    compileOnly(libs.asm)
    api(libs.javax.annotations)
    api(libs.bytecodecs)
    jarJar(libs.bytecodecs) {
        jarJar.ranged(this, "[1.0.2,)")
    }

    api(libs.koffee)
    jarJar(libs.koffee) {
        jarJar.ranged(this, "[8.0.4,)")
    }
    implementation(libs.kotlin.metadata) {
        isTransitive = false
    }
    jarJar(libs.kotlin.metadata) {
        isTransitive = false
        jarJar.ranged(this, "[2.1.20,)")
    }
    //compileOnly(libs.autoservice)
    //ksp(libs.autoservice.ksp)
    compileOnly(projects.common)
}

publishing.publications {
    create<MavenPublication>("maven") {
        artifactId = base.archivesName.get()
        artifact(tasks.jar)
        fg.component(this)
    }
}

sourceSets.main.get().resources.srcDir("src/generated/resources")

tasks {
    withType<JavaCompile> { source(project(":common").sourceSets.main.get().allSource) }

    withType<KotlinCompile> { source(project(":common").sourceSets.main.get().allSource) }

    named("sourcesJar", Jar::class) { from(project(":common").sourceSets.main.get().allSource) }

    processResources { from(project(":common").sourceSets.main.get().resources) }

    jar { finalizedBy("reobfJar") }
}

sourceSets.forEach {
    val dir = layout.buildDirectory.dir("sourceSets/${it.name}")
    it.output.setResourcesDir(dir)
    it.java.destinationDirectory.set(dir)
}