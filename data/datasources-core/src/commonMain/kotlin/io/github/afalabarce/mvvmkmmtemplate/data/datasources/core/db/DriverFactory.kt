package io.github.afalabarce.mvvmkmmtemplate.data.datasources.core.db

import app.cash.sqldelight.db.SqlDriver

expect class DriverFactory {
    fun createDriver(): SqlDriver
}

object Database {
    val databaseName = "KmmDatabase"
}

/*
fun createDatabase(driverFactory: DriverFactory): Database {
    val driver = driverFactory.createDriver()
    val database = Database(driver)

    // Do more work with the database (see below).
}

 */