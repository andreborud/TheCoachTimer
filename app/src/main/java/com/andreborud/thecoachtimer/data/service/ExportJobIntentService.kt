package com.andreborud.thecoachtimer.data.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import androidx.core.app.JobIntentService
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.andreborud.thecoachtimer.App
import com.andreborud.thecoachtimer.R
import com.andreborud.thecoachtimer.data.model.Leader
import com.andreborud.thecoachtimer.data.model.Player
import com.andreborud.thecoachtimer.ui.view.FilesActivity
import com.andreborud.thecoachtimer.util.FileHandler
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.json.JSONObject
import java.util.*

class ExportJobIntentService : JobIntentService() {

    override fun onHandleWork(intent: Intent) {
        showNotification()

        // Generate some content for the csv
        // In a real case scenario it should load the values from a DB reflecting what was visible in the leaderboard
        val json = App.context.resources.openRawResource(R.raw.randomuser).bufferedReader().use { it.readText() }
        val playerJsonArray: String? = JSONObject(json).optJSONArray("results")?.toString()
        val players = Gson().fromJson<List<Player>>(playerJsonArray!!, object : TypeToken<List<Player>>() {}.type)
        val leaderboardList = mutableListOf<Leader>()
        players.forEach {
            leaderboardList.add(Leader(it, randomSpeed(), randomLaps()))
        }

        val rows: MutableList<String> = mutableListOf()
        rows.add("")

        // Generate the CSV content
        val csv = StringBuilder()
        csv.appendln("Player,Explosiveness,Endurance")
        leaderboardList.forEach {
            csv.appendln("${it.player.name.fullName},${it.explosiveness},${it.endurance}")
        }

        // Write content to file
        FileHandler.writeFileToInternalStorage(csv.toString(), "Leaderboard - ${Date()}.csv")

        // Notify user the file is ready
        updateNotification()
    }

    /**
     * Show a notification
     */
    private fun showNotification() {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(ONGOING_NOTIFICATION_ID, getNotification("Generating CSV..."))
    }

    /**
     * Generates the notification to be shown
     */
    private fun getNotification(text: String): Notification {
        val pendingIntent = PendingIntent.getActivity(this, 0, Intent(this, FilesActivity::class.java), PendingIntent.FLAG_UPDATE_CURRENT)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel("csv-export-channel-1", "CSV Export Channel 1", NotificationManager.IMPORTANCE_LOW)
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(notificationChannel)
            notificationChannel.setSound(null, null)
        }

        return NotificationCompat.Builder(this, "csv-export-channel-1")
            .setSmallIcon(R.drawable.ic_file_download_black_24dp)
            .setLargeIcon(BitmapFactory.decodeResource(resources, R.drawable.ic_file_download_black_24dp))
            .setColor(ContextCompat.getColor(this, R.color.colorPrimaryDark))
            .setContentTitle("Exporting CSV")
            .setContentText(text)
            .setAutoCancel(false)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_MAX).build()
    }

    /**
     * This is the method that can be called to update the Notification
     */
    private fun updateNotification() {
        val notification = getNotification("CSV Ready")
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(ONGOING_NOTIFICATION_ID)
        notificationManager.notify(ONGOING_NOTIFICATION_ID, notification)
    }

    /**
     * Simple method to generate a random speed value for the purpose of testing this app
     */
    private fun randomSpeed(): Double {
        return (90..160).random().toDouble() / 10
    }

    /**
     * Simple method to generate a random laps for the purpose of testing this app
     */
    private fun randomLaps(): Int {
        return (7..21).random()
    }

    companion object {
        /**
         * Unique job ID for this service.
         * Unique notification ID for this service.
         */
        private const val JOB_ID = 1000
        const val ONGOING_NOTIFICATION_ID = 21000

        /**
         * Convenience method for enqueuing work in to this service.
         */
        fun enqueueWork(context: Context, work: Intent) {
            enqueueWork(context,  ExportJobIntentService::class.java, JOB_ID, work)
        }
    }
}