package com.example.animefinder.utils

import com.example.animefinder.apollo.ApolloController
import kotlin.system.exitProcess

class CrashReporting: Thread.UncaughtExceptionHandler {
    override fun uncaughtException(p0: Thread, p1: Throwable) {
        ApolloController.chuckerCollector.onError(p1.message ?: "Crash Reported", p1)
        exitProcess(1)
    }
}