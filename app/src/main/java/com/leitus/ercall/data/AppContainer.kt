package com.leitus.ercall.data

import android.content.Context
import com.leitus.ercall.data.local.NetworkStatusDataSourceImpl
import com.leitus.ercall.data.network.ContactSheetDataSource

interface AppContainer {
    val contactRepository: ContactRepository
}

class AppDataContainer(private val context: Context) : AppContainer {
    private val networkContactDataSource = ContactSheetDataSource()
    private val networkStatusDataSource = NetworkStatusDataSourceImpl(context)
    override val contactRepository: ContactRepository
        get() = ContactRepositoryImpl(
            ContactDatabase.getDatabase(context).contactDao(),
            networkContactDataSource,
            networkStatusDataSource
        )
}
