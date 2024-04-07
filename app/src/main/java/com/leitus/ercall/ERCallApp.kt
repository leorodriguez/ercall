package com.leitus.ercall

import android.app.Application
import com.leitus.ercall.data.AppContainer
import com.leitus.ercall.data.AppDataContainer

class ERCallApp : Application() {
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = AppDataContainer(this)
    }
}