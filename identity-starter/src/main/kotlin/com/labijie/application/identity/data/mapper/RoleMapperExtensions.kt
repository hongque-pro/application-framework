/*
 * Auto-generated file. Created by MyBatis Generator
 */
package com.labijie.application.identity.data.mapper

import com.labijie.application.identity.data.RoleRecord
import com.labijie.application.identity.data.mapper.RoleDynamicSqlSupport.Role
import com.labijie.application.identity.data.mapper.RoleDynamicSqlSupport.Role.concurrencyStamp
import com.labijie.application.identity.data.mapper.RoleDynamicSqlSupport.Role.id
import com.labijie.application.identity.data.mapper.RoleDynamicSqlSupport.Role.name
import org.mybatis.dynamic.sql.SqlBuilder.isEqualTo
import org.mybatis.dynamic.sql.util.kotlin.*
import org.mybatis.dynamic.sql.util.kotlin.mybatis3.*

fun RoleMapper.count(completer: CountCompleter) =
    countFrom(this::count, Role, completer)

fun RoleMapper.delete(completer: DeleteCompleter) =
    deleteFrom(this::delete, Role, completer)

fun RoleMapper.deleteByPrimaryKey(id_: Long) =
    delete {
        where(id, isEqualTo(id_))
    }

fun RoleMapper.insert(record: RoleRecord) =
    insert(this::insert, record, Role) {
        map(id).toProperty("id")
        map(concurrencyStamp).toProperty("concurrencyStamp")
        map(name).toProperty("name")
    }

fun RoleMapper.insertMultiple(records: Collection<RoleRecord>) =
    insertMultiple(this::insertMultiple, records, Role) {
        map(id).toProperty("id")
        map(concurrencyStamp).toProperty("concurrencyStamp")
        map(name).toProperty("name")
    }

fun RoleMapper.insertMultiple(vararg records: RoleRecord) =
    insertMultiple(records.toList())

fun RoleMapper.insertSelective(record: RoleRecord) =
    insert(this::insert, record, Role) {
        map(id).toPropertyWhenPresent("id", record::id)
        map(concurrencyStamp).toPropertyWhenPresent("concurrencyStamp", record::concurrencyStamp)
        map(name).toPropertyWhenPresent("name", record::name)
    }

private val columnList = listOf(id, concurrencyStamp, name)

fun RoleMapper.selectOne(completer: SelectCompleter) =
    selectOne(this::selectOne, columnList, Role, completer)

fun RoleMapper.select(completer: SelectCompleter) =
    selectList(this::selectMany, columnList, Role, completer)

fun RoleMapper.selectDistinct(completer: SelectCompleter) =
    selectDistinct(this::selectMany, columnList, Role, completer)

fun RoleMapper.selectByPrimaryKey(id_: Long) =
    selectOne {
        where(id, isEqualTo(id_))
    }

fun RoleMapper.update(completer: UpdateCompleter) =
    update(this::update, Role, completer)

fun KotlinUpdateBuilder.updateAllColumns(record: RoleRecord) =
    apply {
        set(id).equalTo(record::id)
        set(concurrencyStamp).equalTo(record::concurrencyStamp)
        set(name).equalTo(record::name)
    }

fun KotlinUpdateBuilder.updateSelectiveColumns(record: RoleRecord) =
    apply {
        set(id).equalToWhenPresent(record::id)
        set(concurrencyStamp).equalToWhenPresent(record::concurrencyStamp)
        set(name).equalToWhenPresent(record::name)
    }

fun RoleMapper.updateByPrimaryKey(record: RoleRecord) =
    update {
        set(concurrencyStamp).equalTo(record::concurrencyStamp)
        set(name).equalTo(record::name)
        where(id, isEqualTo(record::id))
    }

fun RoleMapper.updateByPrimaryKeySelective(record: RoleRecord) =
    update {
        set(concurrencyStamp).equalToWhenPresent(record::concurrencyStamp)
        set(name).equalToWhenPresent(record::name)
        where(id, isEqualTo(record::id))
    }