package com.leitus.ercall.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.leitus.ercall.data.local.dao.ContactDao
import com.leitus.ercall.data.local.entities.EntityContact
import com.leitus.ercall.data.local.entities.EntityContactGroup


@Database(
    entities = [EntityContact::class, EntityContactGroup::class],
    version = 3,
    exportSchema = false
)
abstract class ContactDatabase : RoomDatabase() {
    abstract fun contactDao(): ContactDao

    companion object {
        @Volatile
        private var Instance: ContactDatabase? = null
        
        fun getDatabase(context: Context): ContactDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(context, ContactDatabase::class.java, "contacts_database")
                    // Setting this option in your app's database builder means that Room
                    // permanently deletes all data from the tables in your database when it
                    // attempts to perform a migration with no defined migration path.
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { Instance = it }
            }
        }
    }
}