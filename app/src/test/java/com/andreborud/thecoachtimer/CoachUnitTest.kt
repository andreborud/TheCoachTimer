package com.andreborud.thecoachtimer

import com.andreborud.thecoachtimer.util.ByteConversion
import com.andreborud.thecoachtimer.util.Format
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Test

import org.junit.Assert.*
import org.junit.Rule

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class CoachUnitTest {
    @Test
    fun timeFormats() {
        val milliseconds = Format.timeAndShorten(793)
        assertEquals("00.793", milliseconds)

        val seconds = Format.timeAndShorten(15793)
        assertEquals("15.793", seconds)

        val minutes = Format.timeAndShorten(32 * 60 * 1000 + 1793)
        assertEquals("32:01.793", minutes)

        val hours = Format.timeAndShorten(32 * 60 * 1000 + 1793 + 60 * 60 * 1000)
        assertEquals("1:32:01.793", hours)

        val full = Format.time(793)
        assertEquals("0:00:00.793", full)
    }

    @Test
    fun `Test LongArray to ByteArray and back`() {
        val longArray = longArrayOf(84393,843939,568383,39839)
        val byteArray = ByteConversion.lapTimesToByteArray(longArray)
        val newLongArray = ByteConversion.byteArrayToLongArray(byteArray!!)
        assertArrayEquals(longArray, newLongArray)
    }


}
