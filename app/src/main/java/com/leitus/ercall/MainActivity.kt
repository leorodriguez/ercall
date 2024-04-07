package com.leitus.ercall

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.leitus.ercall.data.model.Contact
import com.leitus.ercall.screens.AppViewModelProvider
import com.leitus.ercall.screens.ContactListViewModel
import com.leitus.ercall.screens.ContactsUIState
import com.leitus.ercall.ui.theme.ErcallTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ErcallTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                )
                {
                    val contactListViewModel: ContactListViewModel =
                        viewModel(factory = AppViewModelProvider.Factory)
                    ContactsMain(contactListViewModel)
                }
            }
        }
    }

}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun Contact(contact: Contact, modifier: Modifier = Modifier) {
    val ctx = LocalContext.current
    val callPhonePermissionState =
        rememberPermissionState(permission = android.Manifest.permission.CALL_PHONE)

    SideEffect {
        if (!callPhonePermissionState.status.isGranted) {
            callPhonePermissionState.launchPermissionRequest()
        }
    }

    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Column(
            modifier = Modifier.weight(0.5f),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val res = when (contact.icon) {
                "fire" -> R.drawable.firedep
                "police" -> R.drawable.policedep
                "hospital" -> R.drawable.hospital
                else -> R.drawable.person
            }
            Image(painter = painterResource(id = res), contentDescription = "icon")
        }
        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.Start
        ) {
            Text(modifier = modifier, fontSize = 15.sp, text = contact.name)
        }
        //Spacer(modifier = Modifier.size(10.dp))
        Column(
            modifier = Modifier.weight(1.5f),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                modifier = modifier, fontSize = 15.sp,
                text = contact.phone, fontStyle = FontStyle.Italic, fontWeight = FontWeight.Bold
            )
        }
        Column(
            modifier = Modifier.weight(0.5f),
            horizontalAlignment = Alignment.Start
        ) {
            IconButton(onClick = {
                val u = Uri.parse("tel:${contact.phone}")
                val i = Intent(Intent.ACTION_DIAL, u)
                try {
                    ctx.startActivity(i)
                } catch (s: SecurityException) {
                    Toast.makeText(ctx, "An error occurred", Toast.LENGTH_LONG)
                        .show()
                }
            }) {
                Icon(
                    painter = painterResource(id = R.drawable.dialpad),
                    contentDescription = "Dial"
                )
            }
        }
        Column(
            modifier = Modifier.weight(0.5f),
            horizontalAlignment = Alignment.Start
        ) {
            IconButton(onClick = {
                if (callPhonePermissionState.status.isGranted) {
                    val u = Uri.parse("tel:${contact.phone}")
                    val i = Intent(Intent.ACTION_CALL, u)
                    try {
                        ctx.startActivity(i)
                    } catch (s: SecurityException) {
                        Log.e("MainActivity", s.localizedMessage ?: "")
                        Toast.makeText(ctx, "An error occurred", Toast.LENGTH_LONG)
                            .show()
                    }
                } else {
                    callPhonePermissionState.launchPermissionRequest()
                }
            }) {
                Icon(
                    painter = painterResource(id = R.drawable.call),
                    contentDescription = "Call"
                )
            }
        }
    }
}

@Composable
fun ContactList(contacts: List<Contact>, modifier: Modifier = Modifier) {
    Column(verticalArrangement = Arrangement.Center) {
        contacts.forEach { contact ->
            Contact(contact = contact, modifier = modifier)
        }
    }
}


@Composable
fun ContactsMain(
    contactsViewModel: ContactListViewModel,
    modifier: Modifier = Modifier
) {
    var tabState by remember { mutableStateOf(0) }
    val state by contactsViewModel.contactsState.collectAsStateWithLifecycle()
    val firstTimeLoaded = remember { mutableStateOf(false) }
    val isLoading = remember { mutableStateOf(false) }

    val context = LocalContext.current

    fun reloadWithProgress() {
        if (!isLoading.value) {
            isLoading.value = true
            contactsViewModel.reload(onDataLoaded = {
                isLoading.value = false
            })
        }
    }

    SideEffect {
        if (!firstTimeLoaded.value) {
            reloadWithProgress()
            firstTimeLoaded.value = true
        }
    }

    when (val contactsState = state) {
        is ContactsUIState.Success -> {
            val contactMap = contactsState.groupsWithContacts
            val groups = contactMap.keys.toList()
            val groupSelection = buildMap {
                groups.forEachIndexed { index, group ->
                    put(index, group)
                }
            }
            if (contactMap.isNotEmpty()) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    if (isLoading.value) {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .size(47.dp)
                                .align(Alignment.End),
                            strokeWidth = 2.dp
                        )
                    } else {
                        IconButton(
                            onClick = {
                                contactsViewModel.reload(onDataLoaded = {
                                    reloadWithProgress()
                                })
                            },
                            modifier = Modifier.align(Alignment.End)
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.refresh),
                                contentDescription = "refresh"
                            )
                        }
                    }
                    ScrollableTabRow(selectedTabIndex = tabState) {
                        groups.toList().forEachIndexed { index, group ->
                            Tab(selected = tabState == index,
                                onClick = { tabState = index },
                                text = { Text(group.groupName) })
                        }
                    }
                    val selectedGroup = groupSelection[tabState]
                    val selectedContacts = contactMap
                        .getOrDefault(selectedGroup, listOf())
                    ContactList(contacts = selectedContacts)
                }
            } else {
                Text("No available content")
            }
        }

        is ContactsUIState.Error ->
            Text(text = "Error")

        is ContactsUIState.Loading ->
            Text(text = "Loading")
    }
}

@Preview("ContactListPreview")
@Composable
fun ContactListPreview() {
    TODO()
}




