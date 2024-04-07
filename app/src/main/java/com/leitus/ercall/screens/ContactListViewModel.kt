package com.leitus.ercall.screens

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.leitus.ercall.data.ContactRepository
import com.leitus.ercall.data.model.Contact
import com.leitus.ercall.data.model.ContactGroup
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch


sealed interface ContactsUIState {
    data class Success(val groupsWithContacts: Map<ContactGroup, List<Contact>>) : ContactsUIState
    object Error : ContactsUIState
    object Loading : ContactsUIState
}

class ContactListViewModel(private val repository: ContactRepository) : ViewModel() {

    private val TAG: String = "ContactListViewModel"
    private val _contactsState = MutableStateFlow<ContactsUIState>(ContactsUIState.Loading)
    val contactsState: StateFlow<ContactsUIState> = _contactsState

    init {
        getContacts()
    }

    fun reload(onDataLoaded: () -> Unit) {
        viewModelScope.launch(Dispatchers.Default) {
            try {
                repository.reload()
            } catch (e: Exception) {
                Log.e(TAG, "Error reloading contacts ${e.localizedMessage}")
            }
        }.invokeOnCompletion {
            onDataLoaded()
        }
    }

    private fun getContacts() {
        viewModelScope.launch(Dispatchers.Default) {
            Log.v(TAG, "Getting contacts from repository")
            repository.loadGroupsAndContacts()
                .catch { e ->
                    Log.v(TAG, "Error loading groups ${e.localizedMessage}")
                    _contactsState.value = ContactsUIState.Error
                }
                .collect { groupsWithContacts ->
                    _contactsState.value = ContactsUIState.Success(groupsWithContacts)
                }
        }
    }

}