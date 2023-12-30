package io.github.afalabarce.mvvmkmmtemplate.data.datasources.core.remote

import de.jensklingenberg.ktorfit.http.GET

interface ApiService {
    @GET("/api/getAllValues")
    suspend fun getAllItems(): List<String>

    companion object {
        const val API_URL = "https://your.own.api"
    }
}