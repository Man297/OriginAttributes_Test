plugins {
    java
    id("io.izzel.taboolib") version "1.40"
    id("org.jetbrains.kotlin.jvm") version "1.5.10"
}

taboolib {
    install("common")
    install("common-5")
    install("platform-bukkit")
    install("module-nms")
    install("module-nms-util")
    install("module-ui")
    install("module-lang")
    install("module-configuration")
    install("module-metrics")
    install("module-kether")
    install("module-chat")
    install("expansion-javascript")
    install("expansion-command-helper")
    classifier = null
    version = "6.0.9-14"

    description {
        contributors {
            name("AmazingOcean")
            name("KunSs")
        }
        dependencies {
            name("PlaceholderAPI").optional(true)
            name("MythicMobs").optional(true)
            name("Chemdah").optional(true)
            name("ProtocolLib").optional(true)
        }
    }

}

repositories {
    mavenCentral()
}

dependencies {
    compileOnly("ink.ptms:nms-all:1.0.0")
    implementation("ink.ptms.core:v11701:11701:mapped")
    implementation("ink.ptms.core:v11701:11701:universal")
    compileOnly(kotlin("stdlib"))
    compileOnly(fileTree("libs"))

}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

tasks.withType<Jar> {
//    destinationDir = file("F:/Server/purpur 1.18.2/plugins")
//    destinationDir = file("F:/Server/Spigot 1.12.2 - 赏金测试/plugins")
//    destinationDir = file("F:/Server/spigot 1.17/plugins")
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
        jvmTarget = "1.8"
        freeCompilerArgs = listOf("-Xjvm-default=all")
    }
}

configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}
