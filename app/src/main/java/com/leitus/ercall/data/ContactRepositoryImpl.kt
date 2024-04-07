package com.leitus.ercall.data

import android.util.Log
import com.leitus.ercall.data.local.NetworkStatusDataSource
import com.leitus.ercall.data.local.dao.ContactDao
import com.leitus.ercall.data.local.entities.EntityContact
import com.leitus.ercall.data.model.ConnectionState
import com.leitus.ercall.data.model.Contact
import com.leitus.ercall.data.model.ContactGroup
import com.leitus.ercall.data.model.asEntity
import com.leitus.ercall.data.model.asExternalModel
import com.leitus.ercall.data.network.ContactSheetDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEmpty

class ContactRepositoryImpl(
    private val contactDao: ContactDao,
    private val networkContactDataSource: ContactSheetDataSource,
    private val networkStatusDataSource: NetworkStatusDataSource,
) : ContactRepository {

    private val TAG: String = "ContactRepositoryImpl"

    override fun reload() {
        when (networkStatusDataSource.getCurrentConnectivityState()) {
            is ConnectionState.Available -> {
                Log.i(TAG, "Reloading contacts")
                val groups = try {
                    networkContactDataSource.contactGroups()
                } catch (e: Exception) {
                    Log.e(TAG, e.localizedMessage ?: "")
                    emptyList()
                }
                contactDao
                    .insertAllGroup(
                        *(groups.map { g -> g.asExternalModel().asEntity() }.toTypedArray())
                    )
                groups.forEach { g ->
                    val contacts = try {
                        networkContactDataSource.contacts(g)
                    } catch (e: Exception) {
                        Log.e(TAG, e.localizedMessage ?: "")
                        emptyList()
                    }
                    contactDao.insertAllContact(
                        *(contacts.map { c -> c.asExternalModel().asEntity() }.toTypedArray())
                    )
                }
            }

            is ConnectionState.Unavailable -> {
                Log.w(TAG, "Network not available")
            }

        }
    }

    override fun loadGroupsAndContacts(): Flow<Map<ContactGroup, List<Contact>>> {
        return contactDao.loadGroupsAndContacts()
            .onEmpty { }
            .map {
                it.map { (entityGroup, entityContacts) ->
                    entityGroup.asExternalModel() to entityContacts.map(EntityContact::asExternalModel)
                }.toMap()
            }
    }

}
