package io.github.afalabarce.mvvmkmmtemplate.data.datasources.core.db

import app.cash.sqldelight.db.SqlDriver

expect class DriverFactory {
    fun createDriver(): SqlDriver
}

object Database {
    val databaseName = "KmmDatabase"
}


fun createDatabase(driverFactory: DriverFactory): KmmDatabase {
    val driver = driverFactory.createDriver()
    val database = KmmDatabase(driver)

    // Do more work with the database (see below).
    return database
}

