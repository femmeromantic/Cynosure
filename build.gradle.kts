@file:Suppress("PropertyName")

import org.jetbrains.kotlin.gradle.dsl.KotlinVersion
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    alias(libs.plugins.cloche)
    kotlin("jvm") version libs.versions.kotlin
    kotlin("plugin.serialization") version libs.versions.kotlin
    `maven-publish`
}

repositories {
    maven(url = "https://maven.parchmentmc.org") { name = "Parchment" }
    maven(url = "https://maven.fabricmc.net") { name = "FabricMC" }
    maven(url = "https://maven.msrandom.net/repository/root") { name = "Ashley"}
    maven(url = "https://maven.terraformersmc.com/releases/") { name = "TerraformersMC" }
    maven(url = "https://thedarkcolour.github.io/KotlinForForge/") { name = "KotlinForForge" }
    maven(url = "https://maven.minecraftforge.net/") { name = "Forge" }
    maven(url = "https://maven.resourcefulbees.com/repository/maven-public/") { name = "ResourcefulBees" }
    mavenLocal()
    mavenCentral()
}

cloche {
    metadata {
        modId = "cynosure"
        name = "Cynosure"
        description = "Militech's answer to Arasaka's Soulkiller."
        license = "LGPL-3.0"
        icon = "assets/cynosure/icon.png"
        url = "https://github.com/MayaqqDev/Cynosure"
        sources = "https://github.com/MayaqqDev/Cynosure"
    }

    mappings {
        official()
        parchment(libs.versions.parchment.get())
    }

    common {
        mixins.from(file("src/common/main/cynosure.mixins.json"))

        dependencies {
            compileOnly(libs.mixin)
            compileOnly(libs.mixinextras)
            implementation(libs.kotlinx.serialization)
            implementation(libs.kotlinx.coroutines)
            implementation(libs.bytecodecs)
        }
    }

    fabric("fabric:${libs.versions.minecraft.get()}") {
        loaderVersion = libs.versions.fabric.get()
        minecraftVersion = libs.versions.minecraft.get()

        runs {
            includedClient()
            client()
            server()
        }

        dependencies {

            fabricApi("${libs.versions.fapi.get()}+${libs.versions.minecraft.get()}")
            modApi(libs.fabric.kotlin)
        }

        metadata {
            entrypoint("main") {
                adapter.set("kotlin")
                value.set("dev.mayaqq.cynosure.CynosureFabric::init")
            }
            entrypoint("client") {
                adapter.set("kotlin")
                value.set("dev.mayaqq.cynosure.client.CynosureClientFabric::init")
            }
            entrypoint("server") {
                adapter.set("kotlin")
                value.set("dev.mayaqq.cynosure.CynosureFabric::lateinit")
            }
            entrypoint("preLaunch") {
                adapter = "kotlin"
                value = "dev.mayaqq.cynosure.CynosureFabricPreLaunchKt::onPreLaunch"
            }
        }

        mixins.from(file("src/fabric/1.20.1/main/cynosure.fabric.mixins.json"))
    }

    forge("forge:${libs.versions.minecraft.get()}") {
        loaderVersion = libs.versions.forge.get()
        minecraftVersion = libs.versions.minecraft.get()

        runs {
            client()
            server()
        }

        dependencies {
            api(libs.forge.kotlin)
            annotationProcessor(libs.mixinextras)
            implementation(libs.forge.mixinextras)
        }

        include(libs.forge.mixinextras)
    }
}


java {
    withSourcesJar()
}

tasks.withType<KotlinCompile> {
    explicitApiMode = org.jetbrains.kotlin.gradle.dsl.ExplicitApiMode.Warning
    compilerOptions {
        languageVersion = KotlinVersion.KOTLIN_2_0
        freeCompilerArgs = listOf("-Xmulti-platform", "-Xno-check-actual", "-Xexpect-actual-classes")
    }
}

publishing {
    publications {
        create<MavenPublication>("mod") {
            from(components["java"])
        }
    }

    repositories {
        val username = "sapphoCompanyUsername".let { System.getenv(it) ?: findProperty(it) }?.toString()
        val password = "sapphoCompanyPassword".let { System.getenv(it) ?: findProperty(it) }?.toString()
        if (username != null && password != null) {
            maven("https://maven.is-immensely.gay/${properties["maven_category"]}") {
                name = "sapphoCompany"
                credentials {
                    this.username = username
                    this.password = password
                }
            }
        } else {
            println("Sappho Company credentials not present.")
        }
    }
}