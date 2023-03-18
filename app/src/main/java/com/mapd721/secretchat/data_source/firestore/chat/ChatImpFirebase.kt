package com.mapd721.secretchat.data_source.firestore.chat

import com.mapd721.secretchat.data_model.chat.Chat
import com.mapd721.secretchat.data_model.chat.Message
import com.mapd721.secretchat.data_source.firestore.message.MessageDaoFirestore

class ChatImpFirebase: Chat {
    override var id: String
    override var messages: MutableList<Message>
    private val messageDao: MessageDaoFirestore

    constructor(id: String, messages: List<Message>, messageDao: MessageDaoFirestore) {
        this.id = id
        this.messages = ArrayList(messages)
        this.messageDao = messageDao
    }

    override fun addMessage(message: Message): String {
        val messageId = messageDao.insert(message)
        message.id = messageId
        messages.add(message)
        return message.id
    }
}