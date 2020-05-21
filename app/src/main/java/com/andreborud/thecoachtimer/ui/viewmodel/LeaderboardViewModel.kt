package com.andreborud.thecoachtimer.ui.viewmodel

import android.content.Intent
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.andreborud.thecoachtimer.App
import com.andreborud.thecoachtimer.R
import com.andreborud.thecoachtimer.data.model.Leader
import com.andreborud.thecoachtimer.data.model.Player
import com.andreborud.thecoachtimer.data.service.ExportJobIntentService
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject


class LeaderboardViewModel : ViewModel(), LifecycleObserver {

    val leaderboardItems: MutableLiveData<List<Leader>> = MutableLiveData()

    private var leaderboardList: MutableList<Leader> = mutableListOf()

    /**
     * start background thread to load leaderboard content
     */
    fun loadLeaderboard () {
        viewModelScope.launch {
            leaderboardList = generateLeaderboardList()
            sortListBy(Performance.Explosiveness)
        }
    }

    /**
     * set the sorting of the leaderboard
     */
    fun sortListBy (performance: Performance) {
        leaderboardItems.value = if (performance == Performance.Explosiveness)
            leaderboardList.sortedByDescending { it.explosiveness }
        else
            leaderboardList.sortedByDescending { it.endurance }
    }

    // Generate some content for the csv
    // In a real case scenario it should load the values from a DB reflecting what was visible in the leaderboard
    private suspend fun generateLeaderboardList(): MutableList<Leader> = withContext(Dispatchers.IO) {
        val json = App.context.resources.openRawResource(R.raw.randomuser).bufferedReader().use { it.readText() }
        val playerJsonArray: String? = JSONObject(json).optJSONArray("results")?.toString()
        val players = Gson().fromJson<List<Player>>(playerJsonArray!!, object : TypeToken<List<Player>>() {}.type)
        val leaderboardList = mutableListOf<Leader>()
        players.forEach {
            leaderboardList.add(Leader(it, randomSpeed(), randomLaps()))
        }
        return@withContext leaderboardList
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

    enum class Performance {
        Explosiveness, Endurance
    }

    /**
     * start the csv export Service
     */
    fun exportToCSV() {
        val intent = Intent(App.context, ExportJobIntentService::class.java)
        ExportJobIntentService.enqueueWork(App.context, intent)
    }



}
