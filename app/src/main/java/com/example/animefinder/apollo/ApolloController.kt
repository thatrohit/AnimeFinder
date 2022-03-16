package com.example.animefinder.apollo

import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.android.support.BuildConfig
import com.example.animefinder.AnimeFinderApplication
import com.example.animefinder.BuildConfig.BASE_URL
import com.readystatesoftware.chuck.ChuckInterceptor
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
        }.addInterceptor(ChuckInterceptor(AnimeFinderApplication.getAppContext())).build()
    }

    val apolloClient: ApolloClient
        get() =
            ApolloClient.builder()
                .serverUrl(BASE_URL)
                .okHttpClient(createHttpClient())
                .build()

}