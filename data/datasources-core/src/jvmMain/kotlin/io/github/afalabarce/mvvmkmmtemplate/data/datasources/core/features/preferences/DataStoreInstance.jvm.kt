package io.github.afalabarce.mvvmkmmtemplate.data.datasources.core.features.preferences

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import java.io.File

fun dataStore(): DataStore<Preferences> = createDataStore(
    producePath = {
        val documentDirectory: String? = System.getProperty("user.dir")

        requireNotNull(documentDirectory) + "${File.separator}$dataStoreFileName"
    }
)