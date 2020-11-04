/*
 * Auto-generated file. Created by MyBatis Generator
 */
package com.labijie.application.geo.data.mapper

import com.labijie.application.geo.data.GeoPointRecord
import org.apache.ibatis.annotations.DeleteProvider
import org.apache.ibatis.annotations.InsertProvider
import org.apache.ibatis.annotations.Mapper
import org.apache.ibatis.annotations.Result
import org.apache.ibatis.annotations.ResultMap
import org.apache.ibatis.annotations.Results
import org.apache.ibatis.annotations.SelectProvider
import org.apache.ibatis.annotations.UpdateProvider
import org.apache.ibatis.type.JdbcType
import org.mybatis.dynamic.sql.delete.render.DeleteStatementProvider
import org.mybatis.dynamic.sql.insert.render.InsertStatementProvider
import org.mybatis.dynamic.sql.insert.render.MultiRowInsertStatementProvider
import org.mybatis.dynamic.sql.select.render.SelectStatementProvider
import org.mybatis.dynamic.sql.update.render.UpdateStatementProvider
import org.mybatis.dynamic.sql.util.SqlProviderAdapter

@Mapper
interface GeoPointMapper {
    @SelectProvider(type=SqlProviderAdapter::class, method="select")
    fun count(selectStatement: SelectStatementProvider): Long

    @DeleteProvider(type=SqlProviderAdapter::class, method="delete")
    fun delete(deleteStatement: DeleteStatementProvider): Int

    @InsertProvider(type=SqlProviderAdapter::class, method="insert")
    fun insert(insertStatement: InsertStatementProvider<GeoPointRecord>): Int

    @InsertProvider(type=SqlProviderAdapter::class, method="insertMultiple")
    fun insertMultiple(multipleInsertStatement: MultiRowInsertStatementProvider<GeoPointRecord>): Int

    @SelectProvider(type=SqlProviderAdapter::class, method="select")
    @ResultMap("GeoPointRecordResult")
    fun selectOne(selectStatement: SelectStatementProvider): GeoPointRecord?

    @SelectProvider(type=SqlProviderAdapter::class, method="select")
    @Results(id="GeoPointRecordResult", value = [
        Result(column="id", property="id", jdbcType=JdbcType.BIGINT, id=true),
        Result(column="point_name", property="pointName", jdbcType=JdbcType.VARCHAR),
        Result(column="h3_cell_re3", property="h3CellRe3", jdbcType=JdbcType.BIGINT),
        Result(column="h3_cell_re4", property="h3CellRe4", jdbcType=JdbcType.BIGINT),
        Result(column="h3_cell_re5", property="h3CellRe5", jdbcType=JdbcType.BIGINT),
        Result(column="h3_cell_re6", property="h3CellRe6", jdbcType=JdbcType.BIGINT),
        Result(column="h3_cell_re7", property="h3CellRe7", jdbcType=JdbcType.BIGINT),
        Result(column="h3_cell_re8", property="h3CellRe8", jdbcType=JdbcType.BIGINT),
        Result(column="h3_cell_re9", property="h3CellRe9", jdbcType=JdbcType.BIGINT),
        Result(column="h3_cell_re10", property="h3CellRe10", jdbcType=JdbcType.BIGINT),
        Result(column="point_type", property="pointType", jdbcType=JdbcType.INTEGER),
        Result(column="map_lat_lng", property="mapLatLng", jdbcType=JdbcType.BINARY),
        Result(column="gps_lat_lng", property="gpsLatLng", jdbcType=JdbcType.BINARY)
    ])
    fun selectMany(selectStatement: SelectStatementProvider): List<GeoPointRecord>

    @UpdateProvider(type=SqlProviderAdapter::class, method="update")
    fun update(updateStatement: UpdateStatementProvider): Int
}