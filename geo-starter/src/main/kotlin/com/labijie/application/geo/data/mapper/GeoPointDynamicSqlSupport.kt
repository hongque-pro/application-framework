/*
 * Auto-generated file. Created by MyBatis Generator
 */
package com.labijie.application.geo.data.mapper

import java.sql.JDBCType
import org.mybatis.dynamic.sql.SqlTable

object GeoPointDynamicSqlSupport {
    object GeoPoint : SqlTable("geo_points") {
        val id = column<Long>("id", JDBCType.BIGINT)

        val pointName = column<String>("point_name", JDBCType.VARCHAR)

        val h3CellRe3 = column<Long>("h3_cell_re3", JDBCType.BIGINT)

        val h3CellRe4 = column<Long>("h3_cell_re4", JDBCType.BIGINT)

        val h3CellRe5 = column<Long>("h3_cell_re5", JDBCType.BIGINT)

        val h3CellRe6 = column<Long>("h3_cell_re6", JDBCType.BIGINT)

        val h3CellRe7 = column<Long>("h3_cell_re7", JDBCType.BIGINT)

        val h3CellRe8 = column<Long>("h3_cell_re8", JDBCType.BIGINT)

        val h3CellRe9 = column<Long>("h3_cell_re9", JDBCType.BIGINT)

        val h3CellRe10 = column<Long>("h3_cell_re10", JDBCType.BIGINT)

        val pointType = column<Int>("point_type", JDBCType.INTEGER)

        val mapLatLng = column<ByteArray>("map_lat_lng", JDBCType.BINARY)

        val gpsLatLng = column<ByteArray>("gps_lat_lng", JDBCType.BINARY)
    }
}