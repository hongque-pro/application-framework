/*
 * Auto-generated file. Created by MyBatis Generator
 */
package com.labijie.application.identity.data.mapper

import com.labijie.application.identity.data.UserLoginRecord
import com.labijie.application.identity.data.mapper.UserLoginDynamicSqlSupport.UserLogin
import com.labijie.application.identity.data.mapper.UserLoginDynamicSqlSupport.UserLogin.loginProvider
import com.labijie.application.identity.data.mapper.UserLoginDynamicSqlSupport.UserLogin.providerDisplayName
import com.labijie.application.identity.data.mapper.UserLoginDynamicSqlSupport.UserLogin.providerKey
import com.labijie.application.identity.data.mapper.UserLoginDynamicSqlSupport.UserLogin.userId
import org.mybatis.dynamic.sql.SqlBuilder.isEqualTo
import org.mybatis.dynamic.sql.util.kotlin.*
import org.mybatis.dynamic.sql.util.kotlin.mybatis3.*

fun UserLoginMapper.count(completer: CountCompleter) =
    countFrom(this::count, UserLogin, completer)

fun UserLoginMapper.delete(completer: DeleteCompleter) =
    deleteFrom(this::delete, UserLogin, completer)

fun UserLoginMapper.deleteByPrimaryKey(loginProvider_: String, providerKey_: String) =
    delete {
        where(loginProvider, isEqualTo(loginProvider_))
        and(providerKey, isEqualTo(providerKey_))
    }

fun UserLoginMapper.insert(record: UserLoginRecord) =
    insert(this::insert, record, UserLogin) {
        map(loginProvider).toProperty("loginProvider")
        map(providerKey).toProperty("providerKey")
        map(providerDisplayName).toProperty("providerDisplayName")
        map(userId).toProperty("userId")
    }

fun UserLoginMapper.insertMultiple(records: Collection<UserLoginRecord>) =
    insertMultiple(this::insertMultiple, records, UserLogin) {
        map(loginProvider).toProperty("loginProvider")
        map(providerKey).toProperty("providerKey")
        map(providerDisplayName).toProperty("providerDisplayName")
        map(userId).toProperty("userId")
    }

fun UserLoginMapper.insertMultiple(vararg records: UserLoginRecord) =
    insertMultiple(records.toList())

fun UserLoginMapper.insertSelective(record: UserLoginRecord) =
    insert(this::insert, record, UserLogin) {
        map(loginProvider).toPropertyWhenPresent("loginProvider", record::loginProvider)
        map(providerKey).toPropertyWhenPresent("providerKey", record::providerKey)
        map(providerDisplayName).toPropertyWhenPresent("providerDisplayName", record::providerDisplayName)
        map(userId).toPropertyWhenPresent("userId", record::userId)
    }

private val columnList = listOf(loginProvider, providerKey, providerDisplayName, userId)

fun UserLoginMapper.selectOne(completer: SelectCompleter) =
    selectOne(this::selectOne, columnList, UserLogin, completer)

fun UserLoginMapper.select(completer: SelectCompleter) =
    selectList(this::selectMany, columnList, UserLogin, completer)

fun UserLoginMapper.selectDistinct(completer: SelectCompleter) =
    selectDistinct(this::selectMany, columnList, UserLogin, completer)

fun UserLoginMapper.selectByPrimaryKey(loginProvider_: String, providerKey_: String) =
    selectOne {
        where(loginProvider, isEqualTo(loginProvider_))
        and(providerKey, isEqualTo(providerKey_))
    }

fun UserLoginMapper.update(completer: UpdateCompleter) =
    update(this::update, UserLogin, completer)

fun KotlinUpdateBuilder.updateAllColumns(record: UserLoginRecord) =
    apply {
        set(loginProvider).equalTo(record::loginProvider)
        set(providerKey).equalTo(record::providerKey)
        set(providerDisplayName).equalTo(record::providerDisplayName)
        set(userId).equalTo(record::userId)
    }

fun KotlinUpdateBuilder.updateSelectiveColumns(record: UserLoginRecord) =
    apply {
        set(loginProvider).equalToWhenPresent(record::loginProvider)
        set(providerKey).equalToWhenPresent(record::providerKey)
        set(providerDisplayName).equalToWhenPresent(record::providerDisplayName)
        set(userId).equalToWhenPresent(record::userId)
    }

fun UserLoginMapper.updateByPrimaryKey(record: UserLoginRecord) =
    update {
        set(providerDisplayName).equalTo(record::providerDisplayName)
        set(userId).equalTo(record::userId)
        where(loginProvider, isEqualTo(record::loginProvider))
        and(providerKey, isEqualTo(record::providerKey))
    }

fun UserLoginMapper.updateByPrimaryKeySelective(record: UserLoginRecord) =
    update {
        set(providerDisplayName).equalToWhenPresent(record::providerDisplayName)
        set(userId).equalToWhenPresent(record::userId)
        where(loginProvider, isEqualTo(record::loginProvider))
        and(providerKey, isEqualTo(record::providerKey))
    }