package com.andreborud.thecoachtimer.ui.viewmodel

import android.content.Intent
import android.util.Log
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.andreborud.thecoachtimer.App
import com.andreborud.thecoachtimer.data.database.DatabaseHandler
import com.andreborud.thecoachtimer.data.model.Lap
import com.andreborud.thecoachtimer.data.model.Player
import com.andreborud.thecoachtimer.data.model.PlayerSession
import com.andreborud.thecoachtimer.data.service.ExportJobIntentService
import com.andreborud.thecoachtimer.data.service.SyncJobIntentService
import com.andreborud.thecoachtimer.util.Chronometer
import com.andreborud.thecoachtimer.util.Format
import com.github.mikephil.charting.data.Entry

class SessionViewModel: ViewModel(), LifecycleObserver, Chronometer.OnChronometerTickListener {

    // all the livedata which should be shown in the view
    val lapTimes: MutableLiveData<List<Lap>> = MutableLiveData()
    val time: MutableLiveData<String> = MutableLiveData()
    val averageTime: MutableLiveData<String> = MutableLiveData("-")
    val averageTimeLong: MutableLiveData<Long> = MutableLiveData()
    val averageSpeed: MutableLiveData<String> = MutableLiveData("-")
    val timeVariation: MutableLiveData<String> = MutableLiveData("-")
    val cadence: MutableLiveData<String> = MutableLiveData("-")
    val chartLapTimes: MutableLiveData<List<Entry>> = MutableLiveData()

    private var startTime: Long = 0
    private var stopTime: Long = 0
    private val lapTimesList: MutableList<Lap> = mutableListOf()
    private val chartLapTimesList: MutableList<Entry> = mutableListOf()
    private var chronometer: Chronometer = Chronometer(this)
    lateinit var player: Player
    private var lapDistance: Int = 25

    // set the plaer data for this session
    fun setup(player: Player, lapDistance: Int) {
        this.player = player
        this.lapDistance = lapDistance
    }

    // start the timer
    fun start () {
        startTime = chronometer.start()
    }

    // stop the timer
    fun stop () {
        stopTime = chronometer.stop()
    }

    // add new lap
    fun lap () {
        val lapTimeStamp = chronometer.lap()
        val lapTime = lapTimeStamp - if (lapTimesList.isEmpty()) {
            startTime
        } else {
            lapTimesList.last().timestamp
        }
        lapTimesList.add(
            Lap(
                lapTimesList.size + 1,
                lapTime,
                Format.time(lapTime),
                lapTimeStamp
            )
        )
        chartLapTimesList.add(Entry((lapTimesList.size).toFloat(), lapTime.toFloat()))
        chartLapTimes.value = chartLapTimesList
        calculations()
    }

    // Chronometer tick listener, updates the visible time in the view
    override fun onChronometerTick(time: Long) {
        this.time.value = Format.time(time)
    }

    /**
     * Calculate the values to be displayed along side the time and lap times
     */
    private fun calculations() {
        val timeArray = LongArray(lapTimesList.size)
        val speedArray = DoubleArray(lapTimesList.size)
        lapTimesList.forEachIndexed { index, lap ->
            timeArray[index] = lap.time
            speedArray[index] = lapDistance.toDouble() / (lap.time.toDouble() / 1000.0)
        }

        averageSpeed(speedArray)
        averageTime(timeArray)
        timeVariation(timeArray.min(), timeArray.max())
        cadence(timeArray)
        lapTimes.value = lapTimesList.reversed()
    }

    // get average speed, format and present
    private fun averageSpeed(speedArray: DoubleArray) {
        val avSpeed = speedArray.average()
        averageSpeed.value = "%.2f".format(avSpeed)
    }

    // get average time, format and present
    private fun averageTime(timeArray: LongArray) {
        val avTime = timeArray.average()
        averageTimeLong.value = avTime.toLong()
        averageTime.value = Format.timeAndShorten(avTime.toLong())
    }

    // get time variation, format and present
    private fun timeVariation(min: Long?, max: Long?) {
        if (min != null && max != null) {
            val variation = max - min
            timeVariation.value = Format.timeAndShorten(variation)
        }
    }

    // get cadence, format and present
    private fun cadence(timeArray: LongArray) {
        val cadence = 60000.0 / timeArray.average()
        this.cadence.value = "%.2f".format(cadence)
    }

    /**
     * store session to database, called when leaving the activity
     */
    fun saveSession() {
        if (lapTimesList.isEmpty()) return

        val speedArray = DoubleArray(lapTimesList.size)
        val timeArray = LongArray(lapTimesList.size)
        lapTimesList.forEachIndexed { index, lap ->
            speedArray[index] = lapDistance.toDouble() / (lap.time.toDouble() / 1000.0)
            timeArray[index] = lap.time
        }
        val peakSpeed = speedArray.max()!!
        val playerSession = PlayerSession(player.name.fullName, lapTimesList.size, peakSpeed, timeArray)

        val db = DatabaseHandler(App.context)
        val saved = db.saveSession(playerSession)

        Log.d("saved", saved.toString())
        syncSession(playerSession)
    }

    /**
     * send data to server with a background service
     */
    private fun syncSession(playerSession: PlayerSession) {
        val intent = Intent(App.context, ExportJobIntentService::class.java)
        intent.putExtra("playerSession", playerSession)
        SyncJobIntentService.enqueueWork(App.context, intent)
    }
}
