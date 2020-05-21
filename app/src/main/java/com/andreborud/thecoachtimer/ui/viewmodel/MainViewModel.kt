package com.andreborud.thecoachtimer.ui.viewmodel

import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.andreborud.thecoachtimer.App
import com.andreborud.thecoachtimer.R
import com.andreborud.thecoachtimer.data.api.Api
import com.andreborud.thecoachtimer.data.model.Player
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject


class MainViewModel : ViewModel(), LifecycleObserver {

    val players: MutableLiveData<List<Player>> = MutableLiveData()

    fun loadPlayers () {
        viewModelScope.launch {
            players.value = getLocalPlayersList()
        }
    }

    /**
     * The list of users is stored as a json in the raw folder, load them in background from there
     */
    private suspend fun getLocalPlayersList(): List<Player> = withContext(Dispatchers.IO) {
        val json = App.context.resources.openRawResource(R.raw.randomuser).bufferedReader().use { it.readText() }
        val playerJsonArray: String? = JSONObject(json).optJSONArray("results")?.toString()
        return@withContext Gson().fromJson<List<Player>>(playerJsonArray!!, object : TypeToken<List<Player>>() {}.type)
    }

    fun loadPlayersFromServer () {
        viewModelScope.launch {
            players.value = getOnlinePlayersList()
        }
    }

    /**
     * Just for show, load the list of players from the service, can be triggered in the main activity toolbar options.
     */
    @Suppress("BlockingMethodInNonBlockingContext")
    private suspend fun getOnlinePlayersList(): List<Player> = withContext(Dispatchers.IO) {
        val json = Api().get("https://randomuser.me/api/?seed=empatica&inc=name,picture&gender=male&results=10&noinfo")
        val playerJsonArray: String? = JSONObject(json).optJSONArray("results")?.toString()
        return@withContext Gson().fromJson<List<Player>>(playerJsonArray!!, object : TypeToken<List<Player>>() {}.type)
    }

}
