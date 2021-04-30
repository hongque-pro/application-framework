/*
 * Auto-generated file. Created by MyBatis Generator
 */
package com.labijie.application.identity.data.extensions

import com.labijie.application.identity.DynamicTableSupport
import com.labijie.application.identity.data.UserRoleRecord
import com.labijie.application.identity.data.mapper.UserRoleDynamicSqlSupport.UserRole
import com.labijie.application.identity.data.mapper.UserRoleDynamicSqlSupport.UserRole.roleId
import com.labijie.application.identity.data.mapper.UserRoleDynamicSqlSupport.UserRole.userId
import com.labijie.application.identity.data.mapper.UserRoleMapper
import org.mybatis.dynamic.sql.SqlBuilder.isEqualTo
import org.mybatis.dynamic.sql.util.kotlin.*
import org.mybatis.dynamic.sql.util.kotlin.mybatis3.*

fun UserRoleMapper.count(completer: CountCompleter) =
    countFrom(this::count, DynamicTableSupport.getTable(UserRole), completer)

fun UserRoleMapper.delete(completer: DeleteCompleter) =
    deleteFrom(this::delete, DynamicTableSupport.getTable(UserRole), completer)

fun UserRoleMapper.deleteByPrimaryKey(userId_: Long, roleId_: Long) =
    delete {
        where(userId, isEqualTo(userId_))
        and(roleId, isEqualTo(roleId_))
    }

fun UserRoleMapper.insert(record: UserRoleRecord) =
    insert(this::insert, record, DynamicTableSupport.getTable(UserRole)) {
        map(userId).toProperty("userId")
        map(roleId).toProperty("roleId")
    }

fun UserRoleMapper.insertMultiple(records: Collection<UserRoleRecord>) =
    insertMultiple(this::insertMultiple, records, DynamicTableSupport.getTable(UserRole)) {
        map(userId).toProperty("userId")
        map(roleId).toProperty("roleId")
    }

fun UserRoleMapper.insertMultiple(vararg records: UserRoleRecord) =
    insertMultiple(records.toList())

fun UserRoleMapper.insertSelective(record: UserRoleRecord) =
    insert(this::insert, record, DynamicTableSupport.getTable(UserRole)) {
        map(userId).toPropertyWhenPresent("userId", record::userId)
        map(roleId).toPropertyWhenPresent("roleId", record::roleId)
    }

private val columnList = listOf(userId, roleId)

fun UserRoleMapper.selectOne(completer: SelectCompleter) =
    selectOne(this::selectOne,
        columnList, DynamicTableSupport.getTable(UserRole), completer)

fun UserRoleMapper.select(completer: SelectCompleter) =
    selectList(this::selectMany,
        columnList, DynamicTableSupport.getTable(UserRole), completer)

fun UserRoleMapper.selectDistinct(completer: SelectCompleter) =
    selectDistinct(this::selectMany,
        columnList, DynamicTableSupport.getTable(UserRole), completer)

fun UserRoleMapper.update(completer: UpdateCompleter) =
    update(this::update, DynamicTableSupport.getTable(UserRole), completer)

fun KotlinUpdateBuilder.updateAllColumns(record: UserRoleRecord) =
    apply {
        set(userId).equalTo(record::userId)
        set(roleId).equalTo(record::roleId)
    }

fun KotlinUpdateBuilder.updateSelectiveColumns(record: UserRoleRecord) =
    apply {
        set(userId).equalToWhenPresent(record::userId)
        set(roleId).equalToWhenPresent(record::roleId)
    }