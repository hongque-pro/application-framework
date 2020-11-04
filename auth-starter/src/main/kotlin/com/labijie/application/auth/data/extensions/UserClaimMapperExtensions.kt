/*
 * Auto-generated file. Created by MyBatis Generator
 */
package com.labijie.application.auth.data.extensions

import com.labijie.application.auth.AuthDynamicTableSupport
import com.labijie.application.auth.data.UserClaimRecord
import com.labijie.application.auth.data.mapper.RoleDynamicSqlSupport
import com.labijie.application.auth.data.mapper.UserClaimDynamicSqlSupport.UserClaim
import com.labijie.application.auth.data.mapper.UserClaimDynamicSqlSupport.UserClaim.claimType
import com.labijie.application.auth.data.mapper.UserClaimDynamicSqlSupport.UserClaim.claimValue
import com.labijie.application.auth.data.mapper.UserClaimDynamicSqlSupport.UserClaim.id
import com.labijie.application.auth.data.mapper.UserClaimDynamicSqlSupport.UserClaim.userId
import com.labijie.application.auth.data.mapper.UserClaimMapper
import org.mybatis.dynamic.sql.SqlBuilder.isEqualTo
import org.mybatis.dynamic.sql.util.kotlin.*
import org.mybatis.dynamic.sql.util.kotlin.mybatis3.*


fun UserClaimMapper.count(completer: CountCompleter) =
    countFrom(this::count, AuthDynamicTableSupport.getTable(UserClaim), completer)

fun UserClaimMapper.delete(completer: DeleteCompleter) =
    deleteFrom(this::delete, AuthDynamicTableSupport.getTable(UserClaim), completer)

fun UserClaimMapper.deleteByPrimaryKey(id_: Long) =
    delete {
        where(id, isEqualTo(id_))
    }

fun UserClaimMapper.insert(record: UserClaimRecord) =
    insert(this::insert, record, AuthDynamicTableSupport.getTable(UserClaim)) {
        map(id).toProperty("id")
        map(claimType).toProperty("claimType")
        map(claimValue).toProperty("claimValue")
        map(userId).toProperty("userId")
    }

fun UserClaimMapper.insertMultiple(records: Collection<UserClaimRecord>) =
    insertMultiple(this::insertMultiple, records, AuthDynamicTableSupport.getTable(UserClaim)) {
        map(id).toProperty("id")
        map(claimType).toProperty("claimType")
        map(claimValue).toProperty("claimValue")
        map(userId).toProperty("userId")
    }

fun UserClaimMapper.insertMultiple(vararg records: UserClaimRecord) =
    insertMultiple(records.toList())

fun UserClaimMapper.insertSelective(record: UserClaimRecord) =
    insert(this::insert, record, AuthDynamicTableSupport.getTable(UserClaim)) {
        map(id).toPropertyWhenPresent("id", record::id)
        map(claimType).toPropertyWhenPresent("claimType", record::claimType)
        map(claimValue).toPropertyWhenPresent("claimValue", record::claimValue)
        map(userId).toPropertyWhenPresent("userId", record::userId)
    }

private val columnList = listOf(id, claimType, claimValue, userId)

fun UserClaimMapper.selectOne(completer: SelectCompleter) =
    selectOne(this::selectOne,
        columnList, AuthDynamicTableSupport.getTable(UserClaim), completer)

fun UserClaimMapper.select(completer: SelectCompleter) =
    selectList(this::selectMany,
        columnList, AuthDynamicTableSupport.getTable(UserClaim), completer)

fun UserClaimMapper.selectDistinct(completer: SelectCompleter) =
    selectDistinct(this::selectMany,
        columnList, AuthDynamicTableSupport.getTable(UserClaim), completer)

fun UserClaimMapper.selectByPrimaryKey(id_: Long) =
    selectOne {
        where(id, isEqualTo(id_))
    }

fun UserClaimMapper.update(completer: UpdateCompleter) =
    update(this::update, AuthDynamicTableSupport.getTable(UserClaim), completer)

fun KotlinUpdateBuilder.updateAllColumns(record: UserClaimRecord) =
    apply {
        set(id).equalTo(record::id)
        set(claimType).equalTo(record::claimType)
        set(claimValue).equalTo(record::claimValue)
        set(userId).equalTo(record::userId)
    }

fun KotlinUpdateBuilder.updateSelectiveColumns(record: UserClaimRecord) =
    apply {
        set(id).equalToWhenPresent(record::id)
        set(claimType).equalToWhenPresent(record::claimType)
        set(claimValue).equalToWhenPresent(record::claimValue)
        set(userId).equalToWhenPresent(record::userId)
    }

fun UserClaimMapper.updateByPrimaryKey(record: UserClaimRecord) =
    update {
        set(claimType).equalTo(record::claimType)
        set(claimValue).equalTo(record::claimValue)
        set(userId).equalTo(record::userId)
        where(id, isEqualTo(record::id))
    }

fun UserClaimMapper.updateByPrimaryKeySelective(record: UserClaimRecord) =
    update {
        set(claimType).equalToWhenPresent(record::claimType)
        set(claimValue).equalToWhenPresent(record::claimValue)
        set(userId).equalToWhenPresent(record::userId)
        where(id, isEqualTo(record::id))
    }