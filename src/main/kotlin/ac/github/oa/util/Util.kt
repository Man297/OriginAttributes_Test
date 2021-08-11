package ac.github.oa.util

import java.io.File

fun newfolder(root: File, name: String, onCreated: (File) -> Unit): File {
    val file = File(root, name)
    if (!file.exists()) {
        file.mkdirs()
        onCreated(file)
    }
    return file
}
