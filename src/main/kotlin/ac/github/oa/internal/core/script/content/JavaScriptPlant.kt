package ac.github.oa.internal.core.script.content

import ac.github.oa.internal.core.item.ItemPlant
import ac.github.oa.util.listFile
import ac.github.oa.util.newfolder
import taboolib.common.LifeCycle
import taboolib.common.platform.Awake
import taboolib.common5.compileJS
import taboolib.module.configuration.Config
import taboolib.platform.BukkitPlugin
import java.io.File
import javax.script.CompiledScript

object JavaScriptPlant {

    val folder = newfolder(BukkitPlugin.getInstance().dataFolder, "script", listOf("format.js"))
    val keyword = "OriginAttribute${File.separatorChar}script"

    val map = mutableMapOf<String, CompiledScript>()

    @Awake(LifeCycle.ENABLE)
    fun init() {
        map.clear()
        folder.listFile("js").forEach {
            val absolutePath = it.absolutePath
            val indexOf = absolutePath.indexOf(keyword)
            val substring = absolutePath.substring(indexOf + keyword.length + 1)
                .replace(File.separatorChar, '/')
                .replace(".js", "")
            val readText = it.readText()
            map[substring] = (readText.compileJS() ?: return@forEach)
        }
    }


}