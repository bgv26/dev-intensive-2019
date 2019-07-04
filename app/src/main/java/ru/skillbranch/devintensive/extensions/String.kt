package ru.skillbranch.devintensive.extensions

fun String.truncate(len: Int = 16): String {
    val result = this.trim()
    return when {
        result.length <= len -> result
        else -> "${result.take(len).trimEnd()}..."
    }
}

fun String.stripHtml(): String = this
        .replace("""<.*?>""".toRegex(), "") // Remove HTML tags
        .replace("""&(#\d+?|\w+?);""".toRegex(), "") // Remove HTML escape sequences
        .replace(""" +""".toRegex(), " ") // Remove extra spaces
