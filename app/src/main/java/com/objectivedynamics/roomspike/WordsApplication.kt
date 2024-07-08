package com.objectivedynamics.roomspike

import android.app.Application
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class WordsApplication : Application() {
    // No need to cancel this scope as it'll be torn down with the process
    val applicationScope = CoroutineScope(SupervisorJob())

    // Using by lazy so the database and the repository are only created when they're needed
    // rather than when the application starts
    val database by lazy {
        val database1 = WordRoomDatabase.getDatabase(this, applicationScope)
        database1
    }
    val repository by lazy {
        WordRepository(database.wordDao())
    }
}

