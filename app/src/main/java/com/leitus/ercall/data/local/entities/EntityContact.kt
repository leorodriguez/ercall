package com.leitus.ercall.data.local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(primaryKeys = ["group_name", "name"], tableName = "contacts")
data class EntityContact(@ColumnInfo(name = "group_name") val groupName: String,
                         @ColumnInfo(name = "name") val name: String,
                         @ColumnInfo(name = "phone") val phone: String,
                         @ColumnInfo(name = "icon") val icon: String)

@Entity(tableName = "groups")
data class EntityContactGroup(@PrimaryKey @ColumnInfo(name = "group_name") val groupName: String)
