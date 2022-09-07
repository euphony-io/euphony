package co.euphony.util

import android.os.Handler
import android.os.Looper
import android.util.Log
import java.util.*

class EuTimer(
    private val thread: Thread,
    private val timeOutListener: EuTimeOutListener?
) {

    companion object {
        private const val LOG = "EuTimer"
    }

    fun start(timeout: Long) {
        if (timeout <= 0) {
            Log.w(LOG, "timeout must be a number greater than 0")
            return
        }

        val timer = Timer()
        val timeOutTask = TimeOutTask(timer)
        timer.schedule(timeOutTask, timeout)
    }

    private inner class TimeOutTask(
        private val timer: Timer
    ) : TimerTask() {
        private val mainHandler: Handler = Handler(Looper.getMainLooper())

        override fun run() {
            if (thread.isAlive) {
                Log.e(LOG, "Your job(${Thread.currentThread().name}) is interrupted by timeout")
                thread.interrupt()
                timer.cancel()

                mainHandler.post {
                    timeOutListener?.onTimeOut()
                }
            }
        }
    }
}