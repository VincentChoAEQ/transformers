package com.awestruck.transformers

import android.app.Application

/**
 * Created by Chris on 2018-09-29.
 */
class App : Application() {

    companion object {
        lateinit var application: Application
            private set
    }

    init {
        application = this
    }

}