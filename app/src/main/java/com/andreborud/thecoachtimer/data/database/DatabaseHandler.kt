package com.andreborud.thecoachtimer.data.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.andreborud.thecoachtimer.data.model.PlayerSession
import com.andreborud.thecoachtimer.util.ByteConversion

class DatabaseHandler(context: Context): SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {

    override fun onCreate(db: SQLiteDatabase?) {
        val createTable =
            "CREATE TABLE $TABLE_NAME ($ID Integer PRIMARY KEY AUTOINCREMENT NOT NULL, $FULL_NAME TEXT, $LAPS INTEGER, $PEAK_SPEED REAL, $LAP_TIMES BLOB)"
        db?.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) { }

    // Store the session of a player
    fun saveSession(playerSession: PlayerSession): Boolean {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(FULL_NAME, playerSession.fullName)
        values.put(LAPS, playerSession.laps)
        values.put(PEAK_SPEED, playerSession.peakSpeed)

        // Convert the LongArray to a ByteArray and store as a blob
        values.put(LAP_TIMES, ByteConversion.lapTimesToByteArray(playerSession.lapTimes))
        val success = db.insert(TABLE_NAME, null, values)
        db.close()
        return (Integer.parseInt("$success") != -1)
    }

    // Git a list of all session stored locally
    fun getSessions(): List<PlayerSession> {
        val playerSessions: MutableList<PlayerSession> = mutableListOf()
        val db = readableDatabase
        val selectALLQuery = "SELECT * FROM $TABLE_NAME"
        val cursor = db.rawQuery(selectALLQuery, null)
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    val playerSession = PlayerSession(
                            fullName = cursor.getString(cursor.getColumnIndex(FULL_NAME)),
                            laps = cursor.getInt(cursor.getColumnIndex(LAPS)),
                            peakSpeed = cursor.getDouble(cursor.getColumnIndex(PEAK_SPEED)),

                            // Convert the ByteArray back to a "readable" LongArray
                            lapTimes = ByteConversion.byteArrayToLongArray(cursor.getBlob(cursor.getColumnIndex(LAP_TIMES))))
                    playerSession.id = cursor.getInt(cursor.getColumnIndex(ID))
                    playerSessions.add(playerSession)
                } while (cursor.moveToNext())
            }
        }
        cursor.close()
        db.close()
        return playerSessions
    }

    companion object {
        private const val DB_NAME = "TheCoachTimer"
        private const val DB_VERSION = 1
        private const val TABLE_NAME = "PlayerSession"
        private const val ID = "id"
        private const val FULL_NAME = "FirstName"
        private const val LAPS = "Laps"
        private const val PEAK_SPEED = "PeakSpeed"
        private const val LAP_TIMES = "LapTimes"
    }
}