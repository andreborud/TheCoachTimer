package com.andreborud.thecoachtimer

import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.andreborud.thecoachtimer.data.database.DatabaseHandler
import com.andreborud.thecoachtimer.data.model.PlayerSession
import com.andreborud.thecoachtimer.util.FileHandler
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class CoachInstrumentedTest {

    lateinit var db: DatabaseHandler

    @Before
    fun setup() {
        db = DatabaseHandler(ApplicationProvider.getApplicationContext())
    }

    @Test
    fun dbInsert() {
        val saved = db.saveSession(PlayerSession("Mr Unit Test", 10, 12.6, longArrayOf(432143,56543,4522352,4523423)))
        assertEquals(true, saved)
    }

    @Test
    fun dbListAll() {
        val list = db.getSessions()
        assertEquals(true, list.isNotEmpty())
    }

    @Test
    fun writeReadFileInternalStorage() {
        FileHandler.writeFileToInternalStorage("Test content", "test.txt")
        val content = FileHandler.readFileFromInternalStorage("test.txt")
        assertEquals("Test content", content)
        val files = FileHandler.listFiles()
        assertEquals(true, files.isNotEmpty())
    }
}
