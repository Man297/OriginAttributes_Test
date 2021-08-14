plugins {
    java
    id("io.izzel.taboolib") version "1.12"
    id("org.jetbrains.kotlin.jvm") version "1.5.10"
}

taboolib {
    install("common")
    install("platform-bukkit")
    install("module-nms")
    install("module-nms-util")
    install("module-lang")
    install("module-configuration")
    classifier = null
    version = "6.0.0-pre39"

    description {
        contributors {
            name("AmazingOcean")
            name("KunSs")
        }
        dependencies {
            name("PlaceholderAPI")
        }
    }

}

repositories {
    mavenCentral()
}

dependencies {
    implementation("ink.ptms.core:v11701:11701:mapped")
    implementation("ink.ptms.core:v11701:11701:universal")
    compileOnly(kotlin("stdlib"))
    compileOnly(fileTree("libs"))

}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

tasks.withType<Jar> {
    destinationDir = file("F:/Server/Spigot 1.12.2 - 副本/plugins")
}

configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}