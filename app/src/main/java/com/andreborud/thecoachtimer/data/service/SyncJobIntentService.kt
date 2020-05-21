package com.andreborud.thecoachtimer.data.service

import android.content.Context
import android.content.Intent
import android.os.Handler
import android.widget.Toast
import androidx.core.app.JobIntentService
import com.andreborud.thecoachtimer.data.api.Api
import com.andreborud.thecoachtimer.data.model.PlayerSession
import com.google.gson.Gson
import org.json.JSONObject


class SyncJobIntentService : JobIntentService() {

    override fun onHandleWork(intent: Intent) {
        // Convert playerSession to a json string
        val playerSession: PlayerSession = intent.getParcelableExtra("playerSession")!!
        toast("Starting server sync")
        val json = Gson().toJson(playerSession)

        // Send json to server
        val response = Api().post("http://empatica-homework.free.beeceptor.com/trainings", json)
        val jsonObject = JSONObject(response)

        // Simple validation of the response
        if (jsonObject.optString("status") == "training-uploaded") {
            toast("Sync success")
        } else {
            toast("Sync failed")
        }
    }

    private val handler: Handler = Handler()

    // Helper for showing tests
    private fun toast(text: CharSequence?) {
        handler.post {
            Toast.makeText(this@SyncJobIntentService, text, Toast.LENGTH_SHORT).show()
        }
    }

    companion object {
        /**
         * Unique job ID for this service.
         */
        private const val JOB_ID = 1000

        /**
         * Convenience method for enqueuing work in to this service.
         */
        fun enqueueWork(context: Context, work: Intent) {
            enqueueWork(context,  SyncJobIntentService::class.java, JOB_ID, work)
        }
    }
}