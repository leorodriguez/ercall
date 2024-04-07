package com.leitus.ercall.screens

import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.leitus.ercall.ERCallApp

object AppViewModelProvider {

    val Factory = viewModelFactory {
        initializer {
            val savedStateHandle = createSavedStateHandle()
            val repo = (this[APPLICATION_KEY] as ERCallApp).container.contactRepository
            ContactListViewModel(repo)
        }
    }

}