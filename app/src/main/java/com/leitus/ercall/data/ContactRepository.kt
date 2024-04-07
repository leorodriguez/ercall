package com.leitus.ercall.data

import com.leitus.ercall.data.model.Contact
import com.leitus.ercall.data.model.ContactGroup
import kotlinx.coroutines.flow.Flow

interface ContactRepository {

    fun reload()
    fun loadGroupsAndContacts(): Flow<Map<ContactGroup, List<Contact>>>

}