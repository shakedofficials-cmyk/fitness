package com.recompos.app

import android.app.Application
import com.recompos.app.data.local.RecompDatabase
import com.recompos.app.data.repository.RecompRepository
import com.recompos.app.data.repository.SettingsStore
import com.recompos.app.notifications.ReminderScheduler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class RecompOsApplication : Application() {
    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
        CoroutineScope(SupervisorJob() + Dispatchers.IO).launch {
            container.repository.seedIfNeeded()
        }
    }
}

class AppContainer(application: Application) {
    private val db = RecompDatabase.get(application)
    val repository = RecompRepository(db.dao())
    val settingsStore = SettingsStore(application)
    val reminderScheduler = ReminderScheduler(application)
}
