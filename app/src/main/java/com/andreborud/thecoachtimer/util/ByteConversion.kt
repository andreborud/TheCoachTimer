package com.andreborud.thecoachtimer.util

import kotlinx.io.ByteArrayOutputStream
import java.io.DataOutputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.LongBuffer

object ByteConversion {

    /**
     * convert a longArray to a byteArray
     */
    fun lapTimesToByteArray(values: LongArray): ByteArray? {
        val byteStream = ByteArrayOutputStream()
        val dos = DataOutputStream(byteStream)
        for (i in values.indices) {
            dos.writeLong(values[i])
        }
        return byteStream.toByteArray()
    }

    /**
     * convert a byteArray to a longArray
     */
    fun byteArrayToLongArray(byteArray: ByteArray): LongArray {
        val longBuf: LongBuffer = ByteBuffer.wrap(byteArray)
            .order(ByteOrder.BIG_ENDIAN)
            .asLongBuffer()
        val array = LongArray(longBuf.remaining())
        longBuf.get(array)
        return array
    }
}