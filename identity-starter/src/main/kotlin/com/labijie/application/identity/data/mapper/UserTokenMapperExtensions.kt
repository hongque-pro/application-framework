/*
 * Auto-generated file. Created by MyBatis Generator
 */
package com.labijie.application.identity.data.mapper

import com.labijie.application.identity.data.UserTokenRecord
import com.labijie.application.identity.data.mapper.UserTokenDynamicSqlSupport.UserToken
import com.labijie.application.identity.data.mapper.UserTokenDynamicSqlSupport.UserToken.loginProvider
import com.labijie.application.identity.data.mapper.UserTokenDynamicSqlSupport.UserToken.name
import com.labijie.application.identity.data.mapper.UserTokenDynamicSqlSupport.UserToken.token
import com.labijie.application.identity.data.mapper.UserTokenDynamicSqlSupport.UserToken.userId
import org.mybatis.dynamic.sql.SqlBuilder.isEqualTo
import org.mybatis.dynamic.sql.util.kotlin.*
import org.mybatis.dynamic.sql.util.kotlin.mybatis3.*

fun UserTokenMapper.count(completer: CountCompleter) =
    countFrom(this::count, UserToken, completer)

fun UserTokenMapper.delete(completer: DeleteCompleter) =
    deleteFrom(this::delete, UserToken, completer)

fun UserTokenMapper.deleteByPrimaryKey(userId_: Long, loginProvider_: String, token_: String) =
    delete {
        where(userId, isEqualTo(userId_))
        and(loginProvider, isEqualTo(loginProvider_))
        and(token, isEqualTo(token_))
    }

fun UserTokenMapper.insert(record: UserTokenRecord) =
    insert(this::insert, record, UserToken) {
        map(userId).toProperty("userId")
        map(loginProvider).toProperty("loginProvider")
        map(token).toProperty("token")
        map(name).toProperty("name")
    }

fun UserTokenMapper.insertMultiple(records: Collection<UserTokenRecord>) =
    insertMultiple(this::insertMultiple, records, UserToken) {
        map(userId).toProperty("userId")
        map(loginProvider).toProperty("loginProvider")
        map(token).toProperty("token")
        map(name).toProperty("name")
    }

fun UserTokenMapper.insertMultiple(vararg records: UserTokenRecord) =
    insertMultiple(records.toList())

fun UserTokenMapper.insertSelective(record: UserTokenRecord) =
    insert(this::insert, record, UserToken) {
        map(userId).toPropertyWhenPresent("userId", record::userId)
        map(loginProvider).toPropertyWhenPresent("loginProvider", record::loginProvider)
        map(token).toPropertyWhenPresent("token", record::token)
        map(name).toPropertyWhenPresent("name", record::name)
    }

private val columnList = listOf(userId, loginProvider, token, name)

fun UserTokenMapper.selectOne(completer: SelectCompleter) =
    selectOne(this::selectOne, columnList, UserToken, completer)

fun UserTokenMapper.select(completer: SelectCompleter) =
    selectList(this::selectMany, columnList, UserToken, completer)

fun UserTokenMapper.selectDistinct(completer: SelectCompleter) =
    selectDistinct(this::selectMany, columnList, UserToken, completer)

fun UserTokenMapper.selectByPrimaryKey(userId_: Long, loginProvider_: String, token_: String) =
    selectOne {
        where(userId, isEqualTo(userId_))
        and(loginProvider, isEqualTo(loginProvider_))
        and(token, isEqualTo(token_))
    }

fun UserTokenMapper.update(completer: UpdateCompleter) =
    update(this::update, UserToken, completer)

fun KotlinUpdateBuilder.updateAllColumns(record: UserTokenRecord) =
    apply {
        set(userId).equalTo(record::userId)
        set(loginProvider).equalTo(record::loginProvider)
        set(token).equalTo(record::token)
        set(name).equalTo(record::name)
    }

fun KotlinUpdateBuilder.updateSelectiveColumns(record: UserTokenRecord) =
    apply {
        set(userId).equalToWhenPresent(record::userId)
        set(loginProvider).equalToWhenPresent(record::loginProvider)
        set(token).equalToWhenPresent(record::token)
        set(name).equalToWhenPresent(record::name)
    }

fun UserTokenMapper.updateByPrimaryKey(record: UserTokenRecord) =
    update {
        set(name).equalTo(record::name)
        where(userId, isEqualTo(record::userId))
        and(loginProvider, isEqualTo(record::loginProvider))
        and(token, isEqualTo(record::token))
    }

fun UserTokenMapper.updateByPrimaryKeySelective(record: UserTokenRecord) =
    update {
        set(name).equalToWhenPresent(record::name)
        where(userId, isEqualTo(record::userId))
        and(loginProvider, isEqualTo(record::loginProvider))
        and(token, isEqualTo(record::token))
    }