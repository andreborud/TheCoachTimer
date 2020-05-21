package com.andreborud.thecoachtimer.ui.viewmodel

import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.andreborud.thecoachtimer.App
import com.andreborud.thecoachtimer.R
import com.andreborud.thecoachtimer.data.model.FileItem
import com.andreborud.thecoachtimer.data.model.Player
import com.andreborud.thecoachtimer.util.FileHandler
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject

class FilesViewModel : ViewModel(), LifecycleObserver {

    val files: MutableLiveData<List<FileItem>> = MutableLiveData()

    fun loadFiles () {
        viewModelScope.launch {
            files.value = getFilesList()
        }
    }

    /**
     * get the list of file from the internal storage
     */
    private suspend fun getFilesList(): List<FileItem> = withContext(Dispatchers.IO) {
        val files = FileHandler.listFiles()
        val fileList = mutableListOf<FileItem>()
        files.forEach {
            fileList.add(FileItem(it))
        }
        return@withContext fileList
    }

}
