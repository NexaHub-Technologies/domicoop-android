package com.nexahub.domicoop

import android.app.Application
import com.nexahub.domicoop.data.repository.AppContainer

class DomiCoopApp : Application() {
    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
    }
}
