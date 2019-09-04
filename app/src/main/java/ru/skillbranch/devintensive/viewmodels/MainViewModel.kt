package ru.skillbranch.devintensive.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import ru.skillbranch.devintensive.extensions.mutableLiveData
import ru.skillbranch.devintensive.models.data.ChatItem
import ru.skillbranch.devintensive.models.data.ChatType
import ru.skillbranch.devintensive.repositories.ChatRepository

class MainViewModel : ViewModel() {
    private var lastId: MutableList<String> = mutableListOf()
    private val query = mutableLiveData("")
    private val chatRepository = ChatRepository

    private val nonArchiveChats = Transformations.map(chatRepository.loadChats()) { chats ->
        return@map chats.filter { !it.isArchived }.map { it.toChatItem() }.sortedBy { it.id.toInt() }
    }

    private val archiveChats = Transformations.map(chatRepository.loadChats()) { chats ->
        return@map chats.filter { it.isArchived }.map { it.toChatItem() }
    }

    fun getChatData(): LiveData<List<ChatItem>> {
        val result = MediatorLiveData<List<ChatItem>>()

        val filterF = {
            val queryStr = query.value!!
            val chats: MutableList<ChatItem> = mutableListOf()
            if (!archiveChats.value.isNullOrEmpty()) {
                chats.add(
                    archiveChats.value!!.find { it.id == lastId[lastId.lastIndex] }!!.copy(
                        chatType = ChatType.ARCHIVE,
                        messageCount = archiveChats.value!!.sumBy { it.messageCount })
                )
            }
            chats.addAll(nonArchiveChats.value!!)

            result.value = if (queryStr.isEmpty()) chats
            else chats.filter { it.title.contains(queryStr, true) }
        }

        result.addSource(nonArchiveChats) { filterF.invoke() }
        result.addSource(query) { filterF.invoke() }

        return result
    }

    fun getArchiveData(): LiveData<List<ChatItem>> = archiveChats

    fun addToArchive(chatId: String) {
        val chat = chatRepository.find(chatId)
        chat ?: return
        lastId.add(chat.id)
        chatRepository.update(chat.copy(isArchived = true))
    }

    fun restoreFromArchive(chatId: String) {
        val chat = chatRepository.find(chatId)
        chat ?: return
        lastId.removeAt(lastId.lastIndex)
        chatRepository.update(chat.copy(isArchived = false))
    }

    fun handleSearchQuery(text: String) {
        query.value = text
    }
}