package com.labijie.application

import java.nio.ByteOrder

object BytesUtils {

    fun getInt(buf: ByteArray, byteOrder: ByteOrder): Int {
        return if (byteOrder == ByteOrder.BIG_ENDIAN) {
            ((buf[0].toInt() and 0xff) shl 24
                    or ((buf[1].toInt() and 0xff) shl 16)
                    or ((buf[2].toInt() and 0xff) shl 8)
                    or (buf[3].toInt() and 0xff))
        } else { // LITTLE_ENDIAN
            ((buf[3].toInt() and 0xff) shl 24
                    or ((buf[2].toInt() and 0xff) shl 16)
                    or ((buf[1].toInt() and 0xff) shl 8)
                    or (buf[0].toInt() and 0xff))
        }
    }

    fun putInt(intValue: Int, buf: ByteArray, byteOrder: ByteOrder) {
        if (byteOrder == ByteOrder.BIG_ENDIAN) {
            buf[0] = (intValue shr 24).toByte()
            buf[1] = (intValue shr 16).toByte()
            buf[2] = (intValue shr 8).toByte()
            buf[3] = intValue.toByte()
        } else { // LITTLE_ENDIAN
            buf[0] = intValue.toByte()
            buf[1] = (intValue shr 8).toByte()
            buf[2] = (intValue shr 16).toByte()
            buf[3] = (intValue shr 24).toByte()
        }
    }

    fun getLong(buf: ByteArray, byteOrder: ByteOrder): Long {
        return if (byteOrder == ByteOrder.BIG_ENDIAN) {
            (buf[0].toLong() and 0xff) shl 56 or ((buf[1].toLong() and 0xff) shl 48
                    ) or ((buf[2].toLong() and 0xff) shl 40
                    ) or ((buf[3].toLong() and 0xff) shl 32
                    ) or ((buf[4].toLong() and 0xff) shl 24
                    ) or ((buf[5].toLong() and 0xff) shl 16
                    ) or ((buf[6].toLong() and 0xff) shl 8
                    ) or (buf[7].toLong() and 0xff)
        } else { // LITTLE_ENDIAN
            (buf[7].toLong() and 0xff) shl 56 or ((buf[6].toLong() and 0xff) shl 48
                    ) or ((buf[5].toLong() and 0xff) shl 40
                    ) or ((buf[4].toLong() and 0xff) shl 32
                    ) or ((buf[3].toLong() and 0xff) shl 24
                    ) or ((buf[2].toLong() and 0xff) shl 16
                    ) or ((buf[1].toLong() and 0xff) shl 8
                    ) or (buf[0].toLong() and 0xff)
        }
    }

    fun putLong(longValue: Long, buf: ByteArray, byteOrder: ByteOrder) {
        if (byteOrder == ByteOrder.BIG_ENDIAN) {
            buf[0] = (longValue shr 56).toByte()
            buf[1] = (longValue shr 48).toByte()
            buf[2] = (longValue shr 40).toByte()
            buf[3] = (longValue shr 32).toByte()
            buf[4] = (longValue shr 24).toByte()
            buf[5] = (longValue shr 16).toByte()
            buf[6] = (longValue shr 8).toByte()
            buf[7] = longValue.toByte()
        } else {  // LITTLE_ENDIAN
            buf[0] = longValue.toByte()
            buf[1] = (longValue shr 8).toByte()
            buf[2] = (longValue shr 16).toByte()
            buf[3] = (longValue shr 24).toByte()
            buf[4] = (longValue shr 32).toByte()
            buf[5] = (longValue shr 40).toByte()
            buf[6] = (longValue shr 48).toByte()
            buf[7] = (longValue shr 56).toByte()
        }
    }

    fun getDouble(buf: ByteArray, byteOrder: ByteOrder): Double {
        val longVal = getLong(buf, byteOrder)
        return java.lang.Double.longBitsToDouble(longVal)
    }

    fun putDouble(doubleValue: Double, buf: ByteArray, byteOrder: ByteOrder) {
        val longVal = java.lang.Double.doubleToLongBits(doubleValue)
        putLong(longVal, buf, byteOrder)
    }
}