package turfbeat.com

import android.app.Application
import turfbeat.com.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class TurfBeatApp : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@TurfBeatApp)
            modules(appModule)
        }
    }
}
