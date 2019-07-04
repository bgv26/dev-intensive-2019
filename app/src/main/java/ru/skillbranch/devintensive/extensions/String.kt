package ru.skillbranch.devintensive.extensions

fun String.truncate(len: Int = 16): String {
    val result = this.trim()
    return when {
        result.length <= len + 1 -> result
        else -> "${result.take(len + 1).trimEnd()}..."
    }
}

fun String.stripHtml(): String =
    """\s+""".toRegex().split("""<.*?>""".toRegex().replace(this, "")).joinToString(" ")
