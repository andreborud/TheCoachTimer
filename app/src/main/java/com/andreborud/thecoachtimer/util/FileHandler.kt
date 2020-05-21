package com.andreborud.thecoachtimer.util

import android.content.Context
import com.andreborud.thecoachtimer.App
import java.io.*

object FileHandler {

    /**
     * Write file to internal storage. For a live app I would write to the external storage. But a time is limited I used internal storage for this example
     */
    fun writeFileToInternalStorage(content: String, fileName: String) {
        try {
            App.context.openFileOutput(fileName, Context.MODE_PRIVATE).write(content.toByteArray())
        } catch (e: Exception){
            e.printStackTrace()
        }
    }

    /**
     * read file from internal storage
     */
    fun readFileFromInternalStorage(fileName: String): String {
        val bufferedReader = BufferedReader(InputStreamReader(App.context.openFileInput(fileName)))
        val stringBuilder = StringBuilder()
        var text: String? = null
        while ({ text = bufferedReader.readLine(); text }() != null) {
            stringBuilder.append(text)
        }
        return stringBuilder.toString()
    }

    /**
     * list all the files in the internal storage, in a production app I would also filter for csv's
     */
    fun listFiles(): List<File> {
        return App.context.filesDir.listFiles()!!.toList()
    }
}