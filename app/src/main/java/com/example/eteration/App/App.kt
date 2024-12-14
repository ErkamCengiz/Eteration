package com.example.eteration.App

import android.app.Application
import android.content.Context
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class App: Application() {

    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    companion object {
        private var instance: App? = null

        fun getAppContext(): Context {
            return instance!!.applicationContext
        }
    }
}