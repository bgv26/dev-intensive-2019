package ru.skillbranch.devintensive.utils

object Utils {
    fun parseFullName(fullName: String?): Pair<String?, String?> {
        val parts: List<String>? = fullName?.split(" ")
        var firstName = parts?.getOrNull(0)
        if (firstName == "") firstName = null
        var lastName = parts?.getOrNull(1)
        if (lastName == "") lastName = null
        return firstName to lastName
    }

    fun transliteration(payload: String, divider: String = " "): String {
        var outString = ""
        for (s in payload) {
            val isUpper = s.isUpperCase()
            val transLetter = when (s.toLowerCase()) {
                'а' -> "a"
                'б' -> "b"
                'в' -> "v"
                'г' -> "g"
                'д' -> "d"
                'е', 'ё', 'э' -> "e"
                'ж' -> "zh"
                'з' -> "z"
                'и', 'й', 'ы' -> "i"
                'к' -> "k"
                'л' -> "l"
                'м' -> "m"
                'н' -> "n"
                'о' -> "o"
                'п' -> "p"
                'р' -> "r"
                'с' -> "s"
                'т' -> "t"
                'у' -> "u"
                'ф' -> "f"
                'х' -> "h"
                'ц' -> "c"
                'ч' -> "ch"
                'ш', 'щ' -> "sh"
                'ъ', 'ь' -> ""
                'ю' -> "yu"
                'я' -> "ya"
                ' ' -> divider
                else -> "$s"
            }
            outString += if (isUpper) transLetter.capitalize() else transLetter
        }
        return outString
    }

    fun toInitials(firstName: String?, lastName: String?): String? {
        val initials = (firstName?.getOrNull(0)?.toUpperCase() ?: "").toString().trim() +
                (lastName?.getOrNull(0)?.toUpperCase() ?: "").toString().trim()

        return if (initials == "") null else initials
    }

}