package y1row.android.app.accelerometersample

import android.app.Application
import android.content.Context
import timber.log.Timber

class AccelerometerApplication : Application() {

    companion object {
        private var _context: Context? = null

        val context: Context by lazy {
            if (_context == null) throw Exception("context disappeared.")
            _context!!
        }
    }

    override fun onCreate() {
        super.onCreate()
        _context = this

        Timber.plant(Timber.DebugTree())
    }

}
