package com.example.animefinder.apollo

import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.android.support.BuildConfig
import com.chuckerteam.chucker.api.ChuckerCollector
import com.chuckerteam.chucker.api.ChuckerInterceptor
import com.chuckerteam.chucker.api.RetentionManager
import com.example.animefinder.AnimeFinderApplication
import com.example.animefinder.BuildConfig.BASE_URL
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit

object ApolloController {
    private fun createHttpClient(): OkHttpClient {
        val client = OkHttpClient.Builder()
        val timeout: Int = 5 * 60
        client.readTimeout(timeout.toLong(), TimeUnit.SECONDS)
        return client.addInterceptor {
            val original = it.request()
            val requestBuilder = original.newBuilder()
            requestBuilder.header("Content-Type", "application/json")
            val request = requestBuilder.method(original.method, original.body).build()
            return@addInterceptor it.proceed(request)
        }.addInterceptor(
            ChuckerInterceptor.Builder(AnimeFinderApplication.getAppContext())
                .collector(chuckerCollector)
                .maxContentLength(250000L)
                .redactHeaders(emptySet())
                .alwaysReadResponseBody(false)
                .build()
        ).build()
    }

    val chuckerCollector = ChuckerCollector(
        context = AnimeFinderApplication.getAppContext(),
        // Toggles visibility of the push notification
        showNotification = true,
        // Allows to customize the retention period of collected data
        retentionPeriod = RetentionManager.Period.ONE_HOUR,
    )

    val apolloClient: ApolloClient
        get() =
            ApolloClient.builder()
                .serverUrl(BASE_URL)
                .okHttpClient(createHttpClient())
                .build()

}