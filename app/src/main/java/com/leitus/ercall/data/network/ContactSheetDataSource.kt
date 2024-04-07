package com.leitus.ercall.data.network

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.http.HttpRequest
import com.google.api.client.http.HttpRequestInitializer
import com.google.api.client.http.HttpTransport
import com.google.api.client.json.JsonFactory
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.sheets.v4.Sheets
import com.google.api.services.sheets.v4.model.Sheet
import com.google.api.services.sheets.v4.model.SheetProperties
import com.leitus.ercall.data.network.models.NetworkContact
import com.leitus.ercall.data.network.models.NetworkContactGroup
import java.io.IOException
import java.security.GeneralSecurityException

class ContactSheetDataSource {
    private val spreadsheetId = "<your spreadsheet Id>"
    private var sheetService: Sheets? = null

    private fun create() {
        if (sheetService == null) {
            sheetService = createSheetsService()
        }
    }

    @Throws(IOException::class, GeneralSecurityException::class)
    private fun createSheetsService(): Sheets {
        val httpTransport: HttpTransport = GoogleNetHttpTransport.newTrustedTransport()
        val jsonFactory: JsonFactory = GsonFactory.getDefaultInstance()

        val httpRequestInitializer =
            HttpRequestInitializer { request: HttpRequest ->
                request.setInterceptor { intercepted: HttpRequest ->
                    intercepted.url["key"] = "<your key>"
                }
            }
        return Sheets.Builder(httpTransport, jsonFactory, httpRequestInitializer)
            .setApplicationName("Google-SheetsSample/0.1")
            .build()
    }


    @Throws(IOException::class, GeneralSecurityException::class)
    fun contacts(group: NetworkContactGroup): List<NetworkContact> {
        create()
        val range = "${group.groupName}!A2:C"
        val valueRenderOption = "UNFORMATTED_VALUE"
        val dateTimeRenderOption = "FORMATTED_STRING"
        val sheets = sheetService!!.spreadsheets()
        val request = sheets.values()[spreadsheetId, range]
        request.valueRenderOption = valueRenderOption
        request.dateTimeRenderOption = dateTimeRenderOption
        request.majorDimension = "ROWS"
        val response = request.execute()

        val values = response["values"] as List<*>
        val contacts = sequence {
            for (row in values) {
                if (row is List<*>) {
                    val name = row.getOrNull(0) as String? ?: "?"
                    val phone = row.getOrNull(1) as String? ?: "0"
                    val icon = row.getOrNull(2) as String? ?: "default"
                    yield(
                        NetworkContact(
                            name = name,
                            phone = phone,
                            groupName = group.groupName,
                            icon = icon
                        )
                    )
                }
            }
        }
        return contacts.toList()
    }

    fun contactGroups(): List<NetworkContactGroup> {
        create()
        val sheetsMetadata = sheetService!!.spreadsheets().get(spreadsheetId).execute()
        val sheets = sheetsMetadata["sheets"] as List<*>
        val groups = sequence {
            for (sheet in sheets) {
                if (sheet is Sheet) {
                    val properties = sheet["properties"] as SheetProperties
                    yield(properties["title"] as String)
                }
            }
        }
        return groups.toList().map { NetworkContactGroup(it) }
    }
}
