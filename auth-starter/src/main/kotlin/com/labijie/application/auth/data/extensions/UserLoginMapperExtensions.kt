/*
 * Auto-generated file. Created by MyBatis Generator
 */
package com.labijie.application.auth.data.extensions

import com.labijie.application.auth.AuthDynamicTableSupport
import com.labijie.application.auth.data.UserLoginRecord
import com.labijie.application.auth.data.mapper.UserLoginDynamicSqlSupport.UserLogin
import com.labijie.application.auth.data.mapper.UserLoginDynamicSqlSupport.UserLogin.loginProvider
import com.labijie.application.auth.data.mapper.UserLoginDynamicSqlSupport.UserLogin.providerDisplayName
import com.labijie.application.auth.data.mapper.UserLoginDynamicSqlSupport.UserLogin.providerKey
import com.labijie.application.auth.data.mapper.UserLoginDynamicSqlSupport.UserLogin.userId
import com.labijie.application.auth.data.mapper.UserLoginMapper
import org.mybatis.dynamic.sql.SqlBuilder.isEqualTo
import org.mybatis.dynamic.sql.util.kotlin.*
import org.mybatis.dynamic.sql.util.kotlin.mybatis3.*


fun UserLoginMapper.count(completer: CountCompleter) =
    countFrom(this::count, AuthDynamicTableSupport.getTable(UserLogin), completer)

fun UserLoginMapper.delete(completer: DeleteCompleter) =
    deleteFrom(this::delete, AuthDynamicTableSupport.getTable(UserLogin), completer)

fun UserLoginMapper.deleteByPrimaryKey(loginProvider_: String, providerKey_: String) =
    delete {
        where(loginProvider, isEqualTo(loginProvider_))
        and(providerKey, isEqualTo(providerKey_))
    }

fun UserLoginMapper.insert(record: UserLoginRecord) =
    insert(this::insert, record, AuthDynamicTableSupport.getTable(UserLogin)) {
        map(loginProvider).toProperty("loginProvider")
        map(providerKey).toProperty("providerKey")
        map(userId).toProperty("userId")
        map(providerDisplayName).toProperty("providerDisplayName")
    }

fun UserLoginMapper.insertMultiple(records: Collection<UserLoginRecord>) =
    insertMultiple(this::insertMultiple, records, AuthDynamicTableSupport.getTable(UserLogin)) {
        map(loginProvider).toProperty("loginProvider")
        map(providerKey).toProperty("providerKey")
        map(userId).toProperty("userId")
        map(providerDisplayName).toProperty("providerDisplayName")
    }

fun UserLoginMapper.insertMultiple(vararg records: UserLoginRecord) =
    insertMultiple(records.toList())

fun UserLoginMapper.insertSelective(record: UserLoginRecord) =
    insert(this::insert, record, AuthDynamicTableSupport.getTable(UserLogin)) {
        map(loginProvider).toPropertyWhenPresent("loginProvider", record::loginProvider)
        map(providerKey).toPropertyWhenPresent("providerKey", record::providerKey)
        map(userId).toPropertyWhenPresent("userId", record::userId)
        map(providerDisplayName).toPropertyWhenPresent("providerDisplayName", record::providerDisplayName)
    }

private val columnList = listOf(loginProvider, providerKey, userId, providerDisplayName)

fun UserLoginMapper.selectOne(completer: SelectCompleter) =
    selectOne(this::selectOne,
        columnList, AuthDynamicTableSupport.getTable(UserLogin), completer)

fun UserLoginMapper.select(completer: SelectCompleter) =
    selectList(this::selectMany,
        columnList, AuthDynamicTableSupport.getTable(UserLogin), completer)

fun UserLoginMapper.selectDistinct(completer: SelectCompleter) =
    selectDistinct(this::selectMany,
        columnList, AuthDynamicTableSupport.getTable(UserLogin), completer)

fun UserLoginMapper.selectByPrimaryKey(loginProvider_: String, providerKey_: String) =
    selectOne {
        where(loginProvider, isEqualTo(loginProvider_))
        and(providerKey, isEqualTo(providerKey_))
    }

fun UserLoginMapper.update(completer: UpdateCompleter) =
    update(this::update, AuthDynamicTableSupport.getTable(UserLogin), completer)

fun KotlinUpdateBuilder.updateAllColumns(record: UserLoginRecord) =
    apply {
        set(loginProvider).equalTo(record::loginProvider)
        set(providerKey).equalTo(record::providerKey)
        set(userId).equalTo(record::userId)
        set(providerDisplayName).equalTo(record::providerDisplayName)
    }

fun KotlinUpdateBuilder.updateSelectiveColumns(record: UserLoginRecord) =
    apply {
        set(loginProvider).equalToWhenPresent(record::loginProvider)
        set(providerKey).equalToWhenPresent(record::providerKey)
        set(userId).equalToWhenPresent(record::userId)
        set(providerDisplayName).equalToWhenPresent(record::providerDisplayName)
    }

fun UserLoginMapper.updateByPrimaryKey(record: UserLoginRecord) =
    update {
        set(userId).equalTo(record::userId)
        set(providerDisplayName).equalTo(record::providerDisplayName)
        where(loginProvider, isEqualTo(record::loginProvider))
        and(providerKey, isEqualTo(record::providerKey))
    }

fun UserLoginMapper.updateByPrimaryKeySelective(record: UserLoginRecord) =
    update {
        set(userId).equalToWhenPresent(record::userId)
        set(providerDisplayName).equalToWhenPresent(record::providerDisplayName)
        where(loginProvider, isEqualTo(record::loginProvider))
        and(providerKey, isEqualTo(record::providerKey))
    }