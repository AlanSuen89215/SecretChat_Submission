package com.mapd721.secretchat.logic

import com.mapd721.secretchat.data_model.chat.Message

interface MessageSender {
    fun send(text: String): Message
    fun sendImage(name: String, bytes: ByteArray): Message
}