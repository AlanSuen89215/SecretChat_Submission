package com.mapd721.secretchat.logic

import com.mapd721.secretchat.data_model.contact.Contact
import com.mapd721.secretchat.data_source.repository.ChatFactory
import java.security.PrivateKey

class MessageIOFactory(
    private val selfId: String,
    private val selfPrivateKey: PrivateKey,
    private val chatFactory: ChatFactory
) {
    fun getMessageSender(contact: Contact): MessageSender {
        return MessageSenderImp(
            selfId,
            contact.id,
            MessageCipherFactory.getCipherEncryptFromKey(contact.key),
            chatFactory.getChatFirestore(selfId, contact.id),
            chatFactory.getLocalChat(selfId, contact.id)
        )
    }

    fun getMessageReceiver(contact: Contact): MessageReceiver {
        return MessageReceiverImp(
            MessageCipherFactory.getCipherDecryptFromKey(selfPrivateKey),
            chatFactory.getChatFirestore(contact.id, selfId),
            chatFactory.getLocalChat(selfId, contact.id)
        )
    }
}