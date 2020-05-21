package com.andreborud.thecoachtimer.util

import android.os.Handler
import android.os.Message
import android.os.SystemClock
import java.lang.ref.WeakReference


class Chronometer(private val onChronometerTickListener: OnChronometerTickListener) {

    private var lastTick: Long = 0
    private var started = false
    private var running = false
    private var timeElapsed: Long = 0
    private val outerClass = WeakReference(this)
    private val handler =
        ChronometerHandler(
            outerClass
        )

    fun start(): Long {
        started = true
        updateRunning()
        return SystemClock.elapsedRealtime()
    }

    fun stop(): Long {
        started = false
        lastTick = 0L
        updateRunning()
        return SystemClock.elapsedRealtime()
    }

    fun lap(): Long {
        return SystemClock.elapsedRealtime()
    }

    // handle start stops of the handler
    private fun updateRunning() {
        val running = started
        if (running != this.running) {
            if (running) {
                dispatchChronometerTick()
                handler.sendMessageDelayed(
                    Message.obtain(
                        handler,
                        TICKER
                    ), 10
                )
            } else {
                handler.removeMessages(TICKER)
            }
            this.running = running
        }
    }

    private fun dispatchChronometerTick() {
        onChronometerTickListener.onChronometerTick(timeElapsed)
    }

    // The handler itself, handles the time.
    private class ChronometerHandler(private val outerClass: WeakReference<Chronometer>) : Handler() {

        override fun handleMessage(msg: Message?) {
            if (outerClass.get()!!.running) {
                if (outerClass.get()!!.lastTick != 0L) {
                    outerClass.get()!!.timeElapsed += SystemClock.elapsedRealtime() - outerClass.get()!!.lastTick
                }
                outerClass.get()!!.lastTick = SystemClock.elapsedRealtime()

                outerClass.get()?.dispatchChronometerTick()
                sendMessageDelayed(
                    Message.obtain(this,
                        TICKER
                    ),
                    10
                )
            }
        }
    }

    companion object {
        private const val TICKER = 2
    }

    interface OnChronometerTickListener {
        fun onChronometerTick(time: Long)
    }
}