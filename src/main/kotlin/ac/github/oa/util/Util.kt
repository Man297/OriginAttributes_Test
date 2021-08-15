package ac.github.oa.util

import ac.github.oa.internal.core.script.hoop.MapScript
import ac.github.oa.internal.core.item.random.RandomPlant
import org.bukkit.entity.LivingEntity
import taboolib.platform.BukkitPlugin
import java.io.File

fun newfolder(root: File, name: String, files: List<String>): File {
    val file = File(root, name)
    if (!file.exists()) {
        file.mkdirs()
        files.forEach { saveDefaultFile(file, it) }
    }
    return file
}

fun File.listFile(suffix: String = ""): List<File> {
    val listOf = arrayListOf<File>()
    this.listFiles().forEach {
        if (it.isDirectory) {
            listOf.addAll(it.listFile(suffix))
        } else {
            if (suffix == "" || it.name.endsWith(".$suffix")) {
                listOf.add(it)
            }
        }
    }
    return listOf
}

fun saveDefaultFile(file: File, name: String) {
    BukkitPlugin.getInstance().saveResource("${file.name}/$name", false)
}

fun String.rebuild(): List<String> {
    if (this.contains("\n")) {
        return this.split("\n")
    }
    return listOf(this)
}

fun List<String>.rebuild(): List<String> {
    val listOf = arrayListOf<String>()
    this.forEach {
        listOf.addAll(it.rebuild())
    }
    return listOf
}

fun String.random(wrapper: MapScript.Wrapper, entity: LivingEntity?): String {
    return RandomPlant.eval(this, entity, wrapper)
}

fun MutableList<String>.random(wrapper: MapScript.Wrapper, entity: LivingEntity?): List<String> {
    return this.apply {
        forEachIndexed { index, s ->
            this[index] = s.random(wrapper, entity)
        }
    }
}
