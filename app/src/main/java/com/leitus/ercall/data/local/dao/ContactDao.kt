package com.leitus.ercall.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.leitus.ercall.data.local.entities.EntityContact
import com.leitus.ercall.data.local.entities.EntityContactGroup
import kotlinx.coroutines.flow.Flow

@Dao
interface ContactDao {
    @Query("SELECT * FROM groups JOIN contacts WHERE contacts.group_name = groups.group_name")
    fun loadGroupsAndContacts(): Flow<Map<EntityContactGroup, List<EntityContact>>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllContact(vararg contact: EntityContact)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllGroup(vararg group: EntityContactGroup)
}