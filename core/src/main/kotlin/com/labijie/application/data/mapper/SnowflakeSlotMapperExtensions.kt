/*
 * Auto-generated file. Created by MyBatis Generator
 */
package com.labijie.application.data.mapper

import com.labijie.application.data.SnowflakeSlotRecord
import com.labijie.application.data.mapper.SnowflakeSlotDynamicSqlSupport.SnowflakeSlot
import com.labijie.application.data.mapper.SnowflakeSlotDynamicSqlSupport.SnowflakeSlot.addr
import com.labijie.application.data.mapper.SnowflakeSlotDynamicSqlSupport.SnowflakeSlot.instance
import com.labijie.application.data.mapper.SnowflakeSlotDynamicSqlSupport.SnowflakeSlot.slotNumber
import com.labijie.application.data.mapper.SnowflakeSlotDynamicSqlSupport.SnowflakeSlot.timeExpired
import org.mybatis.dynamic.sql.SqlBuilder.isEqualTo
import org.mybatis.dynamic.sql.util.kotlin.*
import org.mybatis.dynamic.sql.util.kotlin.mybatis3.*

fun SnowflakeSlotMapper.count(completer: CountCompleter) =
    countFrom(this::count, SnowflakeSlot, completer)

fun SnowflakeSlotMapper.delete(completer: DeleteCompleter) =
    deleteFrom(this::delete, SnowflakeSlot, completer)

fun SnowflakeSlotMapper.deleteByPrimaryKey(slotNumber_: Short) =
    delete {
        where(slotNumber, isEqualTo(slotNumber_))
    }

fun SnowflakeSlotMapper.insert(record: SnowflakeSlotRecord) =
    insert(this::insert, record, SnowflakeSlot) {
        map(slotNumber).toProperty("slotNumber")
        map(instance).toProperty("instance")
        map(addr).toProperty("addr")
        map(timeExpired).toProperty("timeExpired")
    }

fun SnowflakeSlotMapper.insertMultiple(records: Collection<SnowflakeSlotRecord>) =
    insertMultiple(this::insertMultiple, records, SnowflakeSlot) {
        map(slotNumber).toProperty("slotNumber")
        map(instance).toProperty("instance")
        map(addr).toProperty("addr")
        map(timeExpired).toProperty("timeExpired")
    }

fun SnowflakeSlotMapper.insertMultiple(vararg records: SnowflakeSlotRecord) =
    insertMultiple(records.toList())

fun SnowflakeSlotMapper.insertSelective(record: SnowflakeSlotRecord) =
    insert(this::insert, record, SnowflakeSlot) {
        map(slotNumber).toPropertyWhenPresent("slotNumber", record::slotNumber)
        map(instance).toPropertyWhenPresent("instance", record::instance)
        map(addr).toPropertyWhenPresent("addr", record::addr)
        map(timeExpired).toPropertyWhenPresent("timeExpired", record::timeExpired)
    }

private val columnList = listOf(slotNumber, instance, addr, timeExpired)

fun SnowflakeSlotMapper.selectOne(completer: SelectCompleter) =
    selectOne(this::selectOne, columnList, SnowflakeSlot, completer)

fun SnowflakeSlotMapper.select(completer: SelectCompleter) =
    selectList(this::selectMany, columnList, SnowflakeSlot, completer)

fun SnowflakeSlotMapper.selectDistinct(completer: SelectCompleter) =
    selectDistinct(this::selectMany, columnList, SnowflakeSlot, completer)

fun SnowflakeSlotMapper.selectByPrimaryKey(slotNumber_: Short) =
    selectOne {
        where(slotNumber, isEqualTo(slotNumber_))
    }

fun SnowflakeSlotMapper.update(completer: UpdateCompleter) =
    update(this::update, SnowflakeSlot, completer)

fun KotlinUpdateBuilder.updateAllColumns(record: SnowflakeSlotRecord) =
    apply {
        set(slotNumber).equalTo(record::slotNumber)
        set(instance).equalTo(record::instance)
        set(addr).equalTo(record::addr)
        set(timeExpired).equalTo(record::timeExpired)
    }

fun KotlinUpdateBuilder.updateSelectiveColumns(record: SnowflakeSlotRecord) =
    apply {
        set(slotNumber).equalToWhenPresent(record::slotNumber)
        set(instance).equalToWhenPresent(record::instance)
        set(addr).equalToWhenPresent(record::addr)
        set(timeExpired).equalToWhenPresent(record::timeExpired)
    }

fun SnowflakeSlotMapper.updateByPrimaryKey(record: SnowflakeSlotRecord) =
    update {
        set(instance).equalTo(record::instance)
        set(addr).equalTo(record::addr)
        set(timeExpired).equalTo(record::timeExpired)
        where(slotNumber, isEqualTo(record::slotNumber))
    }

fun SnowflakeSlotMapper.updateByPrimaryKeySelective(record: SnowflakeSlotRecord) =
    update {
        set(instance).equalToWhenPresent(record::instance)
        set(addr).equalToWhenPresent(record::addr)
        set(timeExpired).equalToWhenPresent(record::timeExpired)
        where(slotNumber, isEqualTo(record::slotNumber))
    }