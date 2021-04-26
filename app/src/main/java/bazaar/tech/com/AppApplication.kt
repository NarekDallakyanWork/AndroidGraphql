package bazaar.tech.com

import android.app.Application

class AppApplication : Application() {

    companion object {
        lateinit var appContext: Application
    }

    override fun onCreate() {
        super.onCreate()
        appContext = this
    }
}