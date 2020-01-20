package com.awestruck.transformers

import android.app.Application
import com.awestruck.transformers.di.appComponent
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

/**
 * Created by Chris on 2018-09-29.
 */
class App : Application() {

    companion object {
        lateinit var application: Application
            private set
    }

    override fun onCreate() {
        super.onCreate()
        startKoin{
            androidLogger()
            androidContext(this@App)
            modules(appComponent)
        }
    }


    init {
        application = this
    }

}