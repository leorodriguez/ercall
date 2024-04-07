package com.leitus.ercall.data.model

import com.leitus.ercall.data.local.entities.EntityContact
import com.leitus.ercall.data.local.entities.EntityContactGroup
import com.leitus.ercall.data.network.models.NetworkContact
import com.leitus.ercall.data.network.models.NetworkContactGroup

data class Contact(val groupName: String, val name: String, val phone: String, val icon: String)

data class ContactGroup(val groupName: String)

fun Contact.asEntity(): EntityContact {
    return EntityContact(
        groupName = this.groupName,
        name = this.name,
        phone = this.phone,
        icon = this.icon
    )
}

fun ContactGroup.asEntity(): EntityContactGroup {
    return EntityContactGroup(groupName = this.groupName)
}

fun NetworkContact.asExternalModel(): Contact {
    return Contact(
        groupName = this.groupName,
        name = this.name,
        phone = this.phone,
        icon = this.icon
    )
}

fun NetworkContactGroup.asExternalModel(): ContactGroup {
    return ContactGroup(groupName = this.groupName)
}

fun EntityContact.asExternalModel(): Contact {
    return Contact(
        groupName = this.groupName,
        name = this.name,
        phone = this.phone,
        icon = this.icon
    )
}

fun EntityContactGroup.asExternalModel(): ContactGroup {
    return ContactGroup(groupName = this.groupName)
}