package ru.skillbranch.devintensive.extensions

fun String.truncate(len: Int = 16): String {
    val result = this.trimEnd()
    return when {
        result.length < len -> result
        else -> "${result.take(len).trimEnd()}..."
    }
}

fun String.stripHtml(): String =
    """\s+""".toRegex().split("""<.*?>""".toRegex().replace(this, "")).joinToString(" ")
