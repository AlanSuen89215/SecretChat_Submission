package com.mapd721.secretchat.logic

import com.mapd721.secretchat.data_model.chat.Chat
import com.mapd721.secretchat.data_model.chat.Message
import com.mapd721.secretchat.encryption.MessageCipherEncrypt
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

class MessageSenderImp (
    private val senderId: String,
    private val receiverId: String,
    private val cipher: MessageCipherEncrypt,
    private var chat: Chat
): MessageSender {
    override fun send(text: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val message = Message()
            message.text = cipher.encrypt(text)
            message.senderId = senderId
            message.receiverId = receiverId
            message.sentDateTime = Date()
            chat.addMessage(message)
        }
    }
}