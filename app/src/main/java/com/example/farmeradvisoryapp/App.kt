package com.example.farmeradvisoryapp

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

@HiltAndroidApp
class App : Application() {
    override fun onCreate() {
        super.onCreate()
        initializeLogging()
    }

    private fun initializeLogging() {
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
            Timber.d("Timber logging initialized - DEBUG mode")
        } else {
            Timber.plant(ReleaseTree())
        }
    }
}

private class ReleaseTree : Timber.Tree() {
    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        if (t != null) {
            // Crashlytics.logException(t)
        }
    }
}
