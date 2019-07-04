package ru.skillbranch.devintensive.extensions

fun String.truncate(len: Int = 16): String {
    val result = this.trim()
    return when {
        result.length <= len + 1 -> result
        else -> "${result.take(len + 1).trimEnd()}..."
    }
}

fun String.stripHtml(): String =
    """ +""".toRegex().split(
        """&(#\d+?|\w+?);""".toRegex().replace(
            """<.*?>""".toRegex().replace(
                this,
                ""
            ),
            ""
        )
    ).joinToString(" ")
