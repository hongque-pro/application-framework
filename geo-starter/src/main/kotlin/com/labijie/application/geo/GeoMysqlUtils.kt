package com.labijie.application.geo

import com.labijie.application.BytesUtils
import com.labijie.application.toByteArray
import java.nio.ByteOrder

/**
 * 转换到 mysql 的帮助器类
 * mysql 存储格式参考：https://dev.mysql.com/doc/refman/8.0/en/gis-data-formats.html#gis-wkb-format
 * The value length is 25 bytes, made up of these components (as can be seen from the hexadecimal value):
 * 4 bytes for integer SRID (0)
 * 1 byte for integer byte order (1 = little-endian)
 * 4 bytes for integer type information (1 = Point)
 * 8 bytes for double-precision X coordinate (1)
 * 8 bytes for double-precision Y coordinate (−1)
 */
object GeoMysqlUtils {

    fun fromMysqlPoint(data: ByteArray): Coordinate {
        return fromMysqlPoint(data){
            x, y->
            Coordinate(y, x)
        }
    }

    fun <T> fromMysqlPoint(data: ByteArray, consumer: (x: Double, y: Double) -> T): T {
        val x: Double = BytesUtils.getDouble(data.copyOfRange(9, 9 + 8), ByteOrder.LITTLE_ENDIAN)
        val y: Double = BytesUtils.getDouble(data.copyOfRange(17, 17 + 8), ByteOrder.LITTLE_ENDIAN)
        return consumer(x, y)
    }

    fun toMysqlPoint(coordinate: ICoordinate, srid: Int = 0): ByteArray {
        return toMysqlPoint(coordinate.longitude, coordinate.latitude, srid)
    }

    fun toMysqlPoint(x: Double, y: Double, srid: Int = 0): ByteArray {
        val xBuffer = x.toByteArray(ByteOrder.LITTLE_ENDIAN)
        val yBuffer = y.toByteArray(ByteOrder.LITTLE_ENDIAN)
        val sridBuffer = srid.toByteArray(ByteOrder.BIG_ENDIAN)
        val pointTypeBuffer = 1.toByteArray(ByteOrder.LITTLE_ENDIAN) // wtb: 1 = point

        val d = ByteArray(25).apply {
            sridBuffer.copyInto(this, 0)
            ByteArray(1) { 1 }.copyInto(this, 4) // 1 = LITTLE_ENDIAN
            pointTypeBuffer.copyInto(this, 5)
            xBuffer.copyInto(this, 9)
            yBuffer.copyInto(this, 17)
        }
        return d
    }
}