package com.andreborud.thecoachtimer

import android.content.Context
import android.util.TypedValue
import androidx.multidex.MultiDexApplication

class App : MultiDexApplication() {

    init {
        appContext = this
    }

    companion object {

        private lateinit var appContext: App

        val context: Context
            get() = appContext

    }
}

// Very handy function to convert dp values to pixels
fun dpToPx (dp: Int): Int {
    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp.toFloat(), App.context.resources.displayMetrics).toInt()
}