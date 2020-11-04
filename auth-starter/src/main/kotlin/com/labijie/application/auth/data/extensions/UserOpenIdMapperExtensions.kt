/*
 * Auto-generated file. Created by MyBatis Generator
 */
package com.labijie.application.auth.data.extensions

import com.labijie.application.auth.AuthDynamicTableSupport
import com.labijie.application.auth.data.UserOpenIdRecord
import com.labijie.application.auth.data.mapper.UserOpenIdDynamicSqlSupport.UserOpenId
import com.labijie.application.auth.data.mapper.UserOpenIdDynamicSqlSupport.UserOpenId.appId
import com.labijie.application.auth.data.mapper.UserOpenIdDynamicSqlSupport.UserOpenId.loginProvider
import com.labijie.application.auth.data.mapper.UserOpenIdDynamicSqlSupport.UserOpenId.openId
import com.labijie.application.auth.data.mapper.UserOpenIdDynamicSqlSupport.UserOpenId.userId
import com.labijie.application.auth.data.mapper.UserOpenIdMapper
import org.mybatis.dynamic.sql.SqlBuilder.isEqualTo
import org.mybatis.dynamic.sql.util.kotlin.*
import org.mybatis.dynamic.sql.util.kotlin.mybatis3.*

fun UserOpenIdMapper.count(completer: CountCompleter) =
    countFrom(this::count, AuthDynamicTableSupport.getTable(UserOpenId), completer)

fun UserOpenIdMapper.delete(completer: DeleteCompleter) =
    deleteFrom(this::delete, AuthDynamicTableSupport.getTable(UserOpenId), completer)

fun UserOpenIdMapper.deleteByPrimaryKey(appId_: String, userId_: Long, loginProvider_: String) =
    delete {
        where(appId, isEqualTo(appId_))
        and(userId, isEqualTo(userId_))
        and(loginProvider, isEqualTo(loginProvider_))
    }

fun UserOpenIdMapper.insert(record: UserOpenIdRecord) =
    insert(this::insert, record, AuthDynamicTableSupport.getTable(UserOpenId)) {
        map(appId).toProperty("appId")
        map(userId).toProperty("userId")
        map(loginProvider).toProperty("loginProvider")
        map(openId).toProperty("openId")
    }

fun UserOpenIdMapper.insertMultiple(records: Collection<UserOpenIdRecord>) =
    insertMultiple(this::insertMultiple, records, AuthDynamicTableSupport.getTable(UserOpenId)) {
        map(appId).toProperty("appId")
        map(userId).toProperty("userId")
        map(loginProvider).toProperty("loginProvider")
        map(openId).toProperty("openId")
    }

fun UserOpenIdMapper.insertMultiple(vararg records: UserOpenIdRecord) =
    insertMultiple(records.toList())

fun UserOpenIdMapper.insertSelective(record: UserOpenIdRecord) =
    insert(this::insert, record, AuthDynamicTableSupport.getTable(UserOpenId)) {
        map(appId).toPropertyWhenPresent("appId", record::appId)
        map(userId).toPropertyWhenPresent("userId", record::userId)
        map(loginProvider).toPropertyWhenPresent("loginProvider", record::loginProvider)
        map(openId).toPropertyWhenPresent("openId", record::openId)
    }

private val columnList = listOf(appId, userId, loginProvider, openId)

fun UserOpenIdMapper.selectOne(completer: SelectCompleter) =
    selectOne(this::selectOne,
        columnList, AuthDynamicTableSupport.getTable(UserOpenId), completer)

fun UserOpenIdMapper.select(completer: SelectCompleter) =
    selectList(this::selectMany,
        columnList, AuthDynamicTableSupport.getTable(UserOpenId), completer)

fun UserOpenIdMapper.selectDistinct(completer: SelectCompleter) =
    selectDistinct(this::selectMany,
        columnList, AuthDynamicTableSupport.getTable(UserOpenId), completer)

fun UserOpenIdMapper.selectByPrimaryKey(appId_: String, userId_: Long, loginProvider_: String) =
    selectOne {
        where(appId, isEqualTo(appId_))
        and(userId, isEqualTo(userId_))
        and(loginProvider, isEqualTo(loginProvider_))
    }

fun UserOpenIdMapper.update(completer: UpdateCompleter) =
    update(this::update, AuthDynamicTableSupport.getTable(UserOpenId),completer)

fun KotlinUpdateBuilder.updateAllColumns(record: UserOpenIdRecord) =
    apply {
        set(appId).equalTo(record::appId)
        set(userId).equalTo(record::userId)
        set(loginProvider).equalTo(record::loginProvider)
        set(openId).equalTo(record::openId)
    }

fun KotlinUpdateBuilder.updateSelectiveColumns(record: UserOpenIdRecord) =
    apply {
        set(appId).equalToWhenPresent(record::appId)
        set(userId).equalToWhenPresent(record::userId)
        set(loginProvider).equalToWhenPresent(record::loginProvider)
        set(openId).equalToWhenPresent(record::openId)
    }

fun UserOpenIdMapper.updateByPrimaryKey(record: UserOpenIdRecord) =
    update {
        set(openId).equalTo(record::openId)
        where(appId, isEqualTo(record::appId))
        and(userId, isEqualTo(record::userId))
        and(loginProvider, isEqualTo(record::loginProvider))
    }

fun UserOpenIdMapper.updateByPrimaryKeySelective(record: UserOpenIdRecord) =
    update {
        set(openId).equalToWhenPresent(record::openId)
        where(appId, isEqualTo(record::appId))
        and(userId, isEqualTo(record::userId))
        and(loginProvider, isEqualTo(record::loginProvider))
    }