package com.example.animefinder

import android.app.Application
import android.content.Context

class AnimeFinderApplication: Application() {
    companion object {
        private lateinit var appContext: Context
        fun getAppContext(): Context {
            return appContext
        }
    }

    override fun onCreate() {
        super.onCreate()
        appContext = applicationContext
    }


}