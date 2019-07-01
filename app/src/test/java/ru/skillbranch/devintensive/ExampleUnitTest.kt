package ru.skillbranch.devintensive

import org.junit.Assert.assertEquals
import org.junit.Test
import ru.skillbranch.devintensive.extensions.*
import ru.skillbranch.devintensive.models.BaseMessage
import ru.skillbranch.devintensive.models.Chat
import ru.skillbranch.devintensive.models.User
import ru.skillbranch.devintensive.utils.Utils
import java.util.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun test_instance() {
        val user2 = User("2", "John", "Cene")
        print("$user2")
    }

    @Test
    fun test_factory() {
        val user = User.makeUser("John Wick")
        val user2 = user.copy(id = "2", lastName = "Cene", lastVisit = Date())
        print("$user\n$user2")
    }

    @Test
    fun test_builder() {
        val user = User.Builder().id("0").firstName("John").lastName("Wick").build()

        print(user)
    }

    @Test
    fun test_decomposition() {
        val user = User.makeUser("John Wick")

        fun getUserInfo() = user

        val (id, firstName, lastName) = getUserInfo()

        println("$id, $firstName, $lastName")
        println("${user.component1()}, ${user.component2()}, ${user.component3()}")
    }

    @Test
    fun test_copy() {
        val user = User.makeUser("John Wick")
        val user2 = user.copy(lastVisit = Date())
        val user3 = user.copy(lastVisit = Date().add(-2, TimeUnits.SECOND))
        val user4 = user.copy(lastName = "Cene", lastVisit = Date().add(2, TimeUnits.HOUR))

        println(
            """
            ${user.lastVisit?.format()} 
            ${user2.lastVisit?.format()} 
            ${user3.lastVisit?.format()}    
            ${user4.lastVisit?.format()}    
        """.trimIndent()
        )
    }

    @Test
    fun test_data_mapping() {
        val user = User.makeUser("Герман Брижак")
        println(user)

        val userView = user.toUserView()

        userView.printMe()
    }

    @Test
    fun test_abstract_factory() {
        val user = User.makeUser("Василий")
        val chat = Chat("0")
        var date = Date()
        assertEquals(
            "id:0 Василий отправил текст \"any text message\" только что",
            BaseMessage.makeMessage(user, chat, date, "text", "any text message").formatMessage()
        )
        date = Date().add(-2, TimeUnits.HOUR)
        assertEquals(
            "id:1 Василий получил изображение \"https://anyurl.com\" 2 часа назад",
            BaseMessage.makeMessage(user, chat, date, "image", "https://anyurl.com", true).formatMessage()
        )
    }

    @Test
    fun test_initials() {
        assertEquals("ГБ", Utils.toInitials("Герман", "Брижак"))
        assertEquals("ГБ", Utils.toInitials("герман", "брижак"))
        assertEquals("Б", Utils.toInitials(null, "Брижак"))
        assertEquals("Б", Utils.toInitials("", "Брижак"))
        assertEquals("Б", Utils.toInitials(null, "брижак"))
        assertEquals("Б", Utils.toInitials("", "брижак"))
        assertEquals("Г", Utils.toInitials("Герман", null))
        assertEquals("Г", Utils.toInitials("Герман", ""))
        assertEquals("Г", Utils.toInitials("герман", null))
        assertEquals("Г", Utils.toInitials("герман", ""))
        assertEquals(null, Utils.toInitials(null, null))
        assertEquals(null, Utils.toInitials("", null))
        assertEquals(null, Utils.toInitials(null, ""))
        assertEquals(null, Utils.toInitials("", ""))
        assertEquals(null, Utils.toInitials(" ", ""))
        assertEquals(null, Utils.toInitials("", " "))
        assertEquals(null, Utils.toInitials(" ", " "))
    }

    @Test
    fun test_parseFullName() {
        assertEquals(Pair(null, null), Utils.parseFullName(null))
        assertEquals(Pair(null, null), Utils.parseFullName(""))
        assertEquals(Pair(null, null), Utils.parseFullName(" "))
        assertEquals(Pair("John", null), Utils.parseFullName("John"))
        assertEquals(Pair("John", "Wick"), Utils.parseFullName("John Wick"))
        assertEquals(Pair(null, "Wick"), Utils.parseFullName(" Wick"))
    }

    @Test
    fun test_transliteration() {
        assertEquals("Ivan Stereotipov", Utils.transliteration("Иван Стереотипов"))
        assertEquals("Amazing_Petr", Utils.transliteration("Amazing Петр", "_"))
        assertEquals("ZhZh", Utils.transliteration("ЖЖ"))
        assertEquals("puShok", Utils.transliteration("пуШок"))
    }

    @Test
    fun test_humanizeDiff() {
        assertEquals("несколько секунд назад", Date().humanizeDiff(Date().add(-2, TimeUnits.SECOND)))
        assertEquals("2 часа назад", Date().add(-2, TimeUnits.HOUR).humanizeDiff())
        assertEquals("5 дней назад", Date().add(-5, TimeUnits.DAY).humanizeDiff())
        assertEquals("через 12 минут", Date().add(12, TimeUnits.MINUTE).humanizeDiff())
        assertEquals("через 21 минуту", Date().add(21, TimeUnits.MINUTE).humanizeDiff())
        assertEquals("через 26 минут", Date().add(26, TimeUnits.MINUTE).humanizeDiff())
        assertEquals("через 7 дней", Date().add(7, TimeUnits.DAY).humanizeDiff())
        assertEquals("более года назад", Date().add(-400, TimeUnits.DAY).humanizeDiff())
        assertEquals("более чем через год", Date().add(400, TimeUnits.DAY).humanizeDiff())
    }

    @Test
    fun test_of_humanizeDiff_2() {
        // ----- Past -----
        assertEquals("только что", Date().add(-1, TimeUnits.SECOND).humanizeDiff())
        assertEquals("несколько секунд назад", Date().add(-45, TimeUnits.SECOND).humanizeDiff())
        assertEquals("минуту назад", Date().add(-46, TimeUnits.SECOND).humanizeDiff())
        assertEquals("1 минуту назад", Date().add(-76, TimeUnits.SECOND).humanizeDiff())
        assertEquals("минуту назад", Date().add(-1, TimeUnits.MINUTE).humanizeDiff())
        assertEquals("2 минуты назад", Date().add(-2, TimeUnits.MINUTE).humanizeDiff())
        assertEquals("3 минуты назад", Date().add(-3, TimeUnits.MINUTE).humanizeDiff())
        assertEquals("45 минут назад", Date().add(-45, TimeUnits.MINUTE).humanizeDiff())
        assertEquals("час назад", Date().add(-1, TimeUnits.HOUR).humanizeDiff())
        assertEquals("1 час назад", Date().add(-76, TimeUnits.MINUTE).humanizeDiff())
        assertEquals("2 часа назад", Date().add(-120, TimeUnits.MINUTE).humanizeDiff())
        assertEquals("3 часа назад", Date().add(-3, TimeUnits.HOUR).humanizeDiff())
        assertEquals("4 часа назад", Date().add(-4, TimeUnits.HOUR).humanizeDiff())
        assertEquals("5 часов назад", Date().add(-5, TimeUnits.HOUR).humanizeDiff())
        assertEquals("день назад", Date().add(-26, TimeUnits.HOUR).humanizeDiff())
        assertEquals("1 день назад", Date().add(-27, TimeUnits.HOUR).humanizeDiff())
        assertEquals("4 дня назад", Date().add(-4, TimeUnits.DAY).humanizeDiff())
        assertEquals("5 дней назад", Date().add(-5, TimeUnits.DAY).humanizeDiff())
        assertEquals("360 дней назад", Date().add(-360, TimeUnits.DAY).humanizeDiff())
        assertEquals("более года назад", Date().add(-361, TimeUnits.DAY).humanizeDiff())

        // ----- Future ------
        assertEquals("через несколько секунд", Date().add(2, TimeUnits.SECOND).humanizeDiff())
        assertEquals("через минуту", Date().add(1, TimeUnits.MINUTE).humanizeDiff())
        assertEquals("через 2 минуты", Date().add(2, TimeUnits.MINUTE).humanizeDiff())
        assertEquals("через 3 минуты", Date().add(3, TimeUnits.MINUTE).humanizeDiff())
        assertEquals("через 5 минут", Date().add(5, TimeUnits.MINUTE).humanizeDiff())
        assertEquals("через час", Date().add(1, TimeUnits.HOUR).humanizeDiff())
        assertEquals("через 2 часа", Date().add(2, TimeUnits.HOUR).humanizeDiff())
        assertEquals("через 3 часа", Date().add(3, TimeUnits.HOUR).humanizeDiff())
        assertEquals("через 4 часа", Date().add(4, TimeUnits.HOUR).humanizeDiff())
        assertEquals("через 5 часов", Date().add(5, TimeUnits.HOUR).humanizeDiff())
        assertEquals("через день", Date().add(1, TimeUnits.DAY).humanizeDiff())
        assertEquals("через 4 дня", Date().add(4, TimeUnits.DAY).humanizeDiff())
        assertEquals("через 5 дней", Date().add(5, TimeUnits.DAY).humanizeDiff())
        assertEquals("через 148 дней", Date().add(148, TimeUnits.DAY).humanizeDiff())
        assertEquals("более чем через год", Date().add(400, TimeUnits.DAY).humanizeDiff())
    }
}