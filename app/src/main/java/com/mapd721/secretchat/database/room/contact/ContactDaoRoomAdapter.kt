package com.mapd721.secretchat.database.room.contact

import com.mapd721.secretchat.data_model.contact.Contact
import com.mapd721.secretchat.data_model.contact.ContactDao

class ContactDaoRoomAdapter (
    private val dao: ContactDaoRoom
): ContactDao {

    override fun insert(contact: Contact) {
        dao.insert(
            ContactModel(
                contact
            )
        )
    }

    override fun getAll(): List<Contact> {
        return dao.getAll()
            .map {
                it.toContact()
            }
    }
}