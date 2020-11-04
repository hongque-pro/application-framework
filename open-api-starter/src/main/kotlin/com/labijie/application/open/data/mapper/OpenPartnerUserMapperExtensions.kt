/*
 * Auto-generated file. Created by MyBatis Generator
 */
package com.labijie.application.open.data.mapper

import com.labijie.application.open.data.OpenPartnerUserRecord
import com.labijie.application.open.data.mapper.OpenPartnerUserDynamicSqlSupport.OpenPartnerUser
import com.labijie.application.open.data.mapper.OpenPartnerUserDynamicSqlSupport.OpenPartnerUser.partnerId
import com.labijie.application.open.data.mapper.OpenPartnerUserDynamicSqlSupport.OpenPartnerUser.userId
import org.mybatis.dynamic.sql.SqlBuilder.isEqualTo
import org.mybatis.dynamic.sql.util.kotlin.*
import org.mybatis.dynamic.sql.util.kotlin.mybatis3.*

fun OpenPartnerUserMapper.count(completer: CountCompleter) =
    countFrom(this::count, OpenPartnerUser, completer)

fun OpenPartnerUserMapper.delete(completer: DeleteCompleter) =
    deleteFrom(this::delete, OpenPartnerUser, completer)

fun OpenPartnerUserMapper.deleteByPrimaryKey(partnerId_: Long, userId_: Long) =
    delete {
        where(partnerId, isEqualTo(partnerId_))
        and(userId, isEqualTo(userId_))
    }

fun OpenPartnerUserMapper.insert(record: OpenPartnerUserRecord) =
    insert(this::insert, record, OpenPartnerUser) {
        map(partnerId).toProperty("partnerId")
        map(userId).toProperty("userId")
    }

fun OpenPartnerUserMapper.insertMultiple(records: Collection<OpenPartnerUserRecord>) =
    insertMultiple(this::insertMultiple, records, OpenPartnerUser) {
        map(partnerId).toProperty("partnerId")
        map(userId).toProperty("userId")
    }

fun OpenPartnerUserMapper.insertMultiple(vararg records: OpenPartnerUserRecord) =
    insertMultiple(records.toList())

fun OpenPartnerUserMapper.insertSelective(record: OpenPartnerUserRecord) =
    insert(this::insert, record, OpenPartnerUser) {
        map(partnerId).toPropertyWhenPresent("partnerId", record::partnerId)
        map(userId).toPropertyWhenPresent("userId", record::userId)
    }

private val columnList = listOf(partnerId, userId)

fun OpenPartnerUserMapper.selectOne(completer: SelectCompleter) =
    selectOne(this::selectOne, columnList, OpenPartnerUser, completer)

fun OpenPartnerUserMapper.select(completer: SelectCompleter) =
    selectList(this::selectMany, columnList, OpenPartnerUser, completer)

fun OpenPartnerUserMapper.selectDistinct(completer: SelectCompleter) =
    selectDistinct(this::selectMany, columnList, OpenPartnerUser, completer)

fun OpenPartnerUserMapper.update(completer: UpdateCompleter) =
    update(this::update, OpenPartnerUser, completer)

fun KotlinUpdateBuilder.updateAllColumns(record: OpenPartnerUserRecord) =
    apply {
        set(partnerId).equalTo(record::partnerId)
        set(userId).equalTo(record::userId)
    }

fun KotlinUpdateBuilder.updateSelectiveColumns(record: OpenPartnerUserRecord) =
    apply {
        set(partnerId).equalToWhenPresent(record::partnerId)
        set(userId).equalToWhenPresent(record::userId)
    }