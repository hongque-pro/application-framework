/*
 * Auto-generated file. Created by MyBatis Generator
 */
package com.labijie.application.geo.data.mapper

import com.labijie.application.geo.data.GeoPointRecord
import com.labijie.application.geo.data.mapper.GeoPointDynamicSqlSupport.GeoPoint
import com.labijie.application.geo.data.mapper.GeoPointDynamicSqlSupport.GeoPoint.gpsLatLng
import com.labijie.application.geo.data.mapper.GeoPointDynamicSqlSupport.GeoPoint.h3CellRe10
import com.labijie.application.geo.data.mapper.GeoPointDynamicSqlSupport.GeoPoint.h3CellRe3
import com.labijie.application.geo.data.mapper.GeoPointDynamicSqlSupport.GeoPoint.h3CellRe4
import com.labijie.application.geo.data.mapper.GeoPointDynamicSqlSupport.GeoPoint.h3CellRe5
import com.labijie.application.geo.data.mapper.GeoPointDynamicSqlSupport.GeoPoint.h3CellRe6
import com.labijie.application.geo.data.mapper.GeoPointDynamicSqlSupport.GeoPoint.h3CellRe7
import com.labijie.application.geo.data.mapper.GeoPointDynamicSqlSupport.GeoPoint.h3CellRe8
import com.labijie.application.geo.data.mapper.GeoPointDynamicSqlSupport.GeoPoint.h3CellRe9
import com.labijie.application.geo.data.mapper.GeoPointDynamicSqlSupport.GeoPoint.id
import com.labijie.application.geo.data.mapper.GeoPointDynamicSqlSupport.GeoPoint.mapLatLng
import com.labijie.application.geo.data.mapper.GeoPointDynamicSqlSupport.GeoPoint.pointName
import com.labijie.application.geo.data.mapper.GeoPointDynamicSqlSupport.GeoPoint.pointType
import org.mybatis.dynamic.sql.SqlBuilder.isEqualTo
import org.mybatis.dynamic.sql.util.kotlin.*
import org.mybatis.dynamic.sql.util.kotlin.mybatis3.*

fun GeoPointMapper.count(completer: CountCompleter) =
    countFrom(this::count, GeoPoint, completer)

fun GeoPointMapper.delete(completer: DeleteCompleter) =
    deleteFrom(this::delete, GeoPoint, completer)

fun GeoPointMapper.deleteByPrimaryKey(id_: Long) =
    delete {
        where(id, isEqualTo(id_))
    }

fun GeoPointMapper.insert(record: GeoPointRecord) =
    insert(this::insert, record, GeoPoint) {
        map(id).toProperty("id")
        map(pointName).toProperty("pointName")
        map(h3CellRe3).toProperty("h3CellRe3")
        map(h3CellRe4).toProperty("h3CellRe4")
        map(h3CellRe5).toProperty("h3CellRe5")
        map(h3CellRe6).toProperty("h3CellRe6")
        map(h3CellRe7).toProperty("h3CellRe7")
        map(h3CellRe8).toProperty("h3CellRe8")
        map(h3CellRe9).toProperty("h3CellRe9")
        map(h3CellRe10).toProperty("h3CellRe10")
        map(pointType).toProperty("pointType")
        map(mapLatLng).toProperty("mapLatLng")
        map(gpsLatLng).toProperty("gpsLatLng")
    }

fun GeoPointMapper.insertMultiple(records: Collection<GeoPointRecord>) =
    insertMultiple(this::insertMultiple, records, GeoPoint) {
        map(id).toProperty("id")
        map(pointName).toProperty("pointName")
        map(h3CellRe3).toProperty("h3CellRe3")
        map(h3CellRe4).toProperty("h3CellRe4")
        map(h3CellRe5).toProperty("h3CellRe5")
        map(h3CellRe6).toProperty("h3CellRe6")
        map(h3CellRe7).toProperty("h3CellRe7")
        map(h3CellRe8).toProperty("h3CellRe8")
        map(h3CellRe9).toProperty("h3CellRe9")
        map(h3CellRe10).toProperty("h3CellRe10")
        map(pointType).toProperty("pointType")
        map(mapLatLng).toProperty("mapLatLng")
        map(gpsLatLng).toProperty("gpsLatLng")
    }

fun GeoPointMapper.insertMultiple(vararg records: GeoPointRecord) =
    insertMultiple(records.toList())

fun GeoPointMapper.insertSelective(record: GeoPointRecord) =
    insert(this::insert, record, GeoPoint) {
        map(id).toPropertyWhenPresent("id", record::id)
        map(pointName).toPropertyWhenPresent("pointName", record::pointName)
        map(h3CellRe3).toPropertyWhenPresent("h3CellRe3", record::h3CellRe3)
        map(h3CellRe4).toPropertyWhenPresent("h3CellRe4", record::h3CellRe4)
        map(h3CellRe5).toPropertyWhenPresent("h3CellRe5", record::h3CellRe5)
        map(h3CellRe6).toPropertyWhenPresent("h3CellRe6", record::h3CellRe6)
        map(h3CellRe7).toPropertyWhenPresent("h3CellRe7", record::h3CellRe7)
        map(h3CellRe8).toPropertyWhenPresent("h3CellRe8", record::h3CellRe8)
        map(h3CellRe9).toPropertyWhenPresent("h3CellRe9", record::h3CellRe9)
        map(h3CellRe10).toPropertyWhenPresent("h3CellRe10", record::h3CellRe10)
        map(pointType).toPropertyWhenPresent("pointType", record::pointType)
        map(mapLatLng).toPropertyWhenPresent("mapLatLng", record::mapLatLng)
        map(gpsLatLng).toPropertyWhenPresent("gpsLatLng", record::gpsLatLng)
    }

private val columnList = listOf(id, pointName, h3CellRe3, h3CellRe4, h3CellRe5, h3CellRe6, h3CellRe7, h3CellRe8, h3CellRe9, h3CellRe10, pointType, mapLatLng, gpsLatLng)

fun GeoPointMapper.selectOne(completer: SelectCompleter) =
    selectOne(this::selectOne, columnList, GeoPoint, completer)

fun GeoPointMapper.select(completer: SelectCompleter) =
    selectList(this::selectMany, columnList, GeoPoint, completer)

fun GeoPointMapper.selectDistinct(completer: SelectCompleter) =
    selectDistinct(this::selectMany, columnList, GeoPoint, completer)

fun GeoPointMapper.selectByPrimaryKey(id_: Long) =
    selectOne {
        where(id, isEqualTo(id_))
    }

fun GeoPointMapper.update(completer: UpdateCompleter) =
    update(this::update, GeoPoint, completer)

fun KotlinUpdateBuilder.updateAllColumns(record: GeoPointRecord) =
    apply {
        set(id).equalTo(record::id)
        set(pointName).equalTo(record::pointName)
        set(h3CellRe3).equalTo(record::h3CellRe3)
        set(h3CellRe4).equalTo(record::h3CellRe4)
        set(h3CellRe5).equalTo(record::h3CellRe5)
        set(h3CellRe6).equalTo(record::h3CellRe6)
        set(h3CellRe7).equalTo(record::h3CellRe7)
        set(h3CellRe8).equalTo(record::h3CellRe8)
        set(h3CellRe9).equalTo(record::h3CellRe9)
        set(h3CellRe10).equalTo(record::h3CellRe10)
        set(pointType).equalTo(record::pointType)
        set(mapLatLng).equalTo(record::mapLatLng)
        set(gpsLatLng).equalTo(record::gpsLatLng)
    }

fun KotlinUpdateBuilder.updateSelectiveColumns(record: GeoPointRecord) =
    apply {
        set(id).equalToWhenPresent(record::id)
        set(pointName).equalToWhenPresent(record::pointName)
        set(h3CellRe3).equalToWhenPresent(record::h3CellRe3)
        set(h3CellRe4).equalToWhenPresent(record::h3CellRe4)
        set(h3CellRe5).equalToWhenPresent(record::h3CellRe5)
        set(h3CellRe6).equalToWhenPresent(record::h3CellRe6)
        set(h3CellRe7).equalToWhenPresent(record::h3CellRe7)
        set(h3CellRe8).equalToWhenPresent(record::h3CellRe8)
        set(h3CellRe9).equalToWhenPresent(record::h3CellRe9)
        set(h3CellRe10).equalToWhenPresent(record::h3CellRe10)
        set(pointType).equalToWhenPresent(record::pointType)
        set(mapLatLng).equalToWhenPresent(record::mapLatLng)
        set(gpsLatLng).equalToWhenPresent(record::gpsLatLng)
    }

fun GeoPointMapper.updateByPrimaryKey(record: GeoPointRecord) =
    update {
        set(pointName).equalTo(record::pointName)
        set(h3CellRe3).equalTo(record::h3CellRe3)
        set(h3CellRe4).equalTo(record::h3CellRe4)
        set(h3CellRe5).equalTo(record::h3CellRe5)
        set(h3CellRe6).equalTo(record::h3CellRe6)
        set(h3CellRe7).equalTo(record::h3CellRe7)
        set(h3CellRe8).equalTo(record::h3CellRe8)
        set(h3CellRe9).equalTo(record::h3CellRe9)
        set(h3CellRe10).equalTo(record::h3CellRe10)
        set(pointType).equalTo(record::pointType)
        set(mapLatLng).equalTo(record::mapLatLng)
        set(gpsLatLng).equalTo(record::gpsLatLng)
        where(id, isEqualTo(record::id))
    }

fun GeoPointMapper.updateByPrimaryKeySelective(record: GeoPointRecord) =
    update {
        set(pointName).equalToWhenPresent(record::pointName)
        set(h3CellRe3).equalToWhenPresent(record::h3CellRe3)
        set(h3CellRe4).equalToWhenPresent(record::h3CellRe4)
        set(h3CellRe5).equalToWhenPresent(record::h3CellRe5)
        set(h3CellRe6).equalToWhenPresent(record::h3CellRe6)
        set(h3CellRe7).equalToWhenPresent(record::h3CellRe7)
        set(h3CellRe8).equalToWhenPresent(record::h3CellRe8)
        set(h3CellRe9).equalToWhenPresent(record::h3CellRe9)
        set(h3CellRe10).equalToWhenPresent(record::h3CellRe10)
        set(pointType).equalToWhenPresent(record::pointType)
        set(mapLatLng).equalToWhenPresent(record::mapLatLng)
        set(gpsLatLng).equalToWhenPresent(record::gpsLatLng)
        where(id, isEqualTo(record::id))
    }