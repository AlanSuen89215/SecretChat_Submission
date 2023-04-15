package com.mapd721.secretchat.ui.view_model

import android.content.BroadcastReceiver
import android.content.IntentFilter
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mapd721.secretchat.broadcast.MessageBroadcastReceiver
import com.mapd721.secretchat.data_model.chat.Message
import com.mapd721.secretchat.data_model.contact.Contact
import com.mapd721.secretchat.data_source.repository.ChatFactory
import com.mapd721.secretchat.encryption.EncryptionKeyPairManager
import com.mapd721.secretchat.logic.ContactManager
import com.mapd721.secretchat.logic.MessageBroadcast
import com.mapd721.secretchat.logic.MessageIOFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ChatListViewModel(
    val contactManager: ContactManager,
    val chatFactory: ChatFactory,
    val selfId: String,
    val selfKeyPairName: String,
    val cloudStorageRootFolderName: String,
    val doRegisterBroadcastReceiver: (BroadcastReceiver, IntentFilter) -> Unit
): ViewModel() {
    val contactListLiveData: MutableLiveData<MutableList<Contact>> = MutableLiveData()
    private var contactList = ArrayList<Contact>()
    val messageListeners: MutableMap<String, (Message) -> Unit> = HashMap()
    val messageIOFactory: MessageIOFactory
    val messageReceiver: MessageBroadcastReceiver

    init {
        messageIOFactory = MessageIOFactory(
            selfId,
            EncryptionKeyPairManager().getKey(selfKeyPairName)!!,
            chatFactory,
            MessageIOFactory.Mode.UI,
            cloudStorageRootFolderName
        )
        messageReceiver = MessageBroadcastReceiver()
        messageReceiver.setOnMessageListener(::dispatchMessage)
        doRegisterBroadcastReceiver(messageReceiver, IntentFilter(MessageBroadcast.INTENT_FILTER))
        loadContactList()
    }

    fun initContactItem(contact: Contact, onMessage: (Message) -> Unit) {
        getLatestMessage(contact) {
            if (it != null) {
                onMessage(it)
            }
            listenMessage(contact, onMessage)
        }
    }

    private fun getLatestMessage(contact: Contact, onMessage: (Message?) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            val chat = chatFactory.getLocalChat(selfId, contact.id)
            val message = chat.getLatestMessage()

            withContext(Dispatchers.Main) {
                onMessage(message)
            }
        }
    }

    private fun listenMessage(contact: Contact, onMessage: (Message) -> Unit) {
        messageListeners[contact.id] = onMessage
    }

    private fun dispatchMessage(message: Message) {
        val contactId = if (message.senderId == selfId) message.receiverId else message.senderId
        messageListeners[contactId]?.invoke(message)
    }

    private fun loadContactList() {
        CoroutineScope(Dispatchers.IO).launch {
            val list = contactManager.getAll()

            withContext(Dispatchers.Main) {
                contactList = ArrayList(list)
                contactListLiveData.value = contactList
            }
        }
    }

    fun onAddedContact() {
        removeAllMessageListeners()
        loadContactList()
    }

    private fun removeAllMessageListeners() {
        messageListeners.clear()
    }

    val contactSearchOnQueryTextListener = object: SearchView.OnQueryTextListener {
        override fun onQueryTextSubmit(query: String): Boolean {
            if (query != "") {
                searchContact(query)
            }
            else {
                resetContactList()
            }
            return true
        }

        override fun onQueryTextChange(newText: String): Boolean {
            if (newText != "") {
                searchContact(newText)
            }
            else {
                resetContactList()
            }
            return true
        }
    }

    private fun searchContact(queryText: String) {
        val result = contactList.filter {
            it.name.contains(queryText, true)
        }
        contactListLiveData.value = ArrayList(result)
    }

    private fun resetContactList() {
        contactListLiveData.value = ArrayList(contactList)
    }
}