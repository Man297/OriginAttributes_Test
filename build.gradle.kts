plugins {
    java
    id("io.izzel.taboolib") version "1.26"
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
    classifier = null
    version = "6.0.2-11"

    description {
        contributors {
            name("AmazingOcean")
            name("KunSs")
        }
        dependencies {
            name("PlaceholderAPI").optional(true)
            name("MythicMobs").optional(true)
            name("Chemdah").optional(true)
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

//tasks.withType<Jar> {
////    destinationDir = file("F:/Server/spigot 1.17/plugins")
//    destinationDir = file("F:/Server/Spigot 1.12.2 - 测试/plugins")
//}

configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}
