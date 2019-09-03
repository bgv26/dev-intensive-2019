package ru.skillbranch.devintensive.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import ru.skillbranch.devintensive.extensions.mutableLiveData
import ru.skillbranch.devintensive.models.data.ChatItem
import ru.skillbranch.devintensive.models.data.ChatType
import ru.skillbranch.devintensive.repositories.ChatRepository

class MainViewModel : ViewModel() {
    private val query = mutableLiveData("")
    private val chatRepository = ChatRepository
    private val nonArchiveChats = Transformations.map(chatRepository.loadChats()) { chats ->
        return@map chats.filter { !it.isArchived }
            .map { it.toChatItem() }
            .sortedBy { it.id.toInt() }
    }
    private val allChats = Transformations.map(chatRepository.loadChats()) { chats ->
        return@map chats.groupBy { it.isArchived }
            .flatMap { (_, v) -> v.map {it.toChatItem() }}
            .sortedBy { it.id.toInt() }
    }

    private fun getArchiveChatItem(): ChatItem? {
        val archiveChatList = allChats.value?.filter { it.chatType == ChatType.ARCHIVE }
        val archiveChat = archiveChatList?.last()
        return archiveChat?.copy(messageCount = archiveChatList.sumBy { it.messageCount })
    }

    fun getChatData(): LiveData<List<ChatItem>> {
        val result = MediatorLiveData<List<ChatItem>>()

        val filterF = {
            val queryStr = query.value!!
            val chats: MutableList<ChatItem> = mutableListOf()
            val archiveChat = getArchiveChatItem()
            if (archiveChat != null) chats.add(archiveChat)
            chats.addAll(allChats.value!!.filter { it.chatType != ChatType.ARCHIVE })

            result.value = if (queryStr.isEmpty()) chats
            else chats.filter { it.title.contains(queryStr, true) }
        }

        result.addSource(allChats) { filterF.invoke() }
        result.addSource(query) { filterF.invoke() }

        return result
    }

    fun addToArchive(chatId: String) {
        val chat = chatRepository.find(chatId)
        Log.d("M_MainViewModel", "addToArchive $chat")
        chat ?: return
        chatRepository.update(chat.copy(isArchived = true))
    }

    fun restoreFromArchive(chatId: String) {
        val chat = chatRepository.find(chatId)
        chat ?: return
        chatRepository.update(chat.copy(isArchived = false))
    }

    fun handleSearchQuery(text: String) {
        query.value = text
    }
}