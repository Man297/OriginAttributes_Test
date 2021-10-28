package ac.github.oa.util

import ac.github.oa.internal.attribute.AttributeAdapter
import ac.github.oa.internal.attribute.AttributeManager
import ac.github.oa.internal.core.item.random.RandomPlant
import ac.github.oa.internal.core.script.func.EmptyScript
import ac.github.oa.internal.core.script.hoop.MapScript
import org.bukkit.entity.LivingEntity
import taboolib.platform.BukkitPlugin
import java.io.File
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

fun newfolder(root: File, name: String, files: List<String>): File {
    val file = File(root, name)
    if (!file.exists()) {
        file.mkdirs()
        files.forEach { saveDefaultFile(file, it) }
    }
    return file
}

fun Class<out AttributeAdapter>.isEnabled(): Boolean {
    return AttributeManager.isEnabled(this)
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
    if (this.contains("/n")) {
        return this.split("/n")
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
    }.filter { it !== EmptyScript.NAMESPACE }
}


fun getInterfaceT(o: Any, index: Int): Class<*>? {
    val types = o.javaClass.genericInterfaces
    val parameterizedType = types[index] as ParameterizedType
    val type = parameterizedType.actualTypeArguments[index]
    return checkType(type, index)
}

private fun checkType(type: Type?, index: Int): Class<*>? {
    if (type is Class<*>) {
        return type
    } else if (type is ParameterizedType) {
        val t = type.actualTypeArguments[index]
        return checkType(t, index)
    } else {
        val className = if (type == null) "null" else type.javaClass.name
        throw IllegalArgumentException(
            "Expected a Class, ParameterizedType"
                    + ", but <" + type + "> is of type " + className
        )
    }
}
