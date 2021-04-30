/*
 * Auto-generated file. Created by MyBatis Generator
 */
package com.labijie.application.identity.data.mapper

import com.labijie.application.identity.data.RoleClaimRecord
import com.labijie.application.identity.data.mapper.RoleClaimDynamicSqlSupport.RoleClaim
import com.labijie.application.identity.data.mapper.RoleClaimDynamicSqlSupport.RoleClaim.claimType
import com.labijie.application.identity.data.mapper.RoleClaimDynamicSqlSupport.RoleClaim.claimValue
import com.labijie.application.identity.data.mapper.RoleClaimDynamicSqlSupport.RoleClaim.id
import com.labijie.application.identity.data.mapper.RoleClaimDynamicSqlSupport.RoleClaim.roleId
import org.mybatis.dynamic.sql.SqlBuilder.isEqualTo
import org.mybatis.dynamic.sql.util.kotlin.*
import org.mybatis.dynamic.sql.util.kotlin.mybatis3.*

fun RoleClaimMapper.count(completer: CountCompleter) =
    countFrom(this::count, RoleClaim, completer)

fun RoleClaimMapper.delete(completer: DeleteCompleter) =
    deleteFrom(this::delete, RoleClaim, completer)

fun RoleClaimMapper.deleteByPrimaryKey(id_: Long) =
    delete {
        where(id, isEqualTo(id_))
    }

fun RoleClaimMapper.insert(record: RoleClaimRecord) =
    insert(this::insert, record, RoleClaim) {
        map(id).toProperty("id")
        map(claimType).toProperty("claimType")
        map(claimValue).toProperty("claimValue")
        map(roleId).toProperty("roleId")
    }

fun RoleClaimMapper.insertMultiple(records: Collection<RoleClaimRecord>) =
    insertMultiple(this::insertMultiple, records, RoleClaim) {
        map(id).toProperty("id")
        map(claimType).toProperty("claimType")
        map(claimValue).toProperty("claimValue")
        map(roleId).toProperty("roleId")
    }

fun RoleClaimMapper.insertMultiple(vararg records: RoleClaimRecord) =
    insertMultiple(records.toList())

fun RoleClaimMapper.insertSelective(record: RoleClaimRecord) =
    insert(this::insert, record, RoleClaim) {
        map(id).toPropertyWhenPresent("id", record::id)
        map(claimType).toPropertyWhenPresent("claimType", record::claimType)
        map(claimValue).toPropertyWhenPresent("claimValue", record::claimValue)
        map(roleId).toPropertyWhenPresent("roleId", record::roleId)
    }

private val columnList = listOf(id, claimType, claimValue, roleId)

fun RoleClaimMapper.selectOne(completer: SelectCompleter) =
    selectOne(this::selectOne, columnList, RoleClaim, completer)

fun RoleClaimMapper.select(completer: SelectCompleter) =
    selectList(this::selectMany, columnList, RoleClaim, completer)

fun RoleClaimMapper.selectDistinct(completer: SelectCompleter) =
    selectDistinct(this::selectMany, columnList, RoleClaim, completer)

fun RoleClaimMapper.selectByPrimaryKey(id_: Long) =
    selectOne {
        where(id, isEqualTo(id_))
    }

fun RoleClaimMapper.update(completer: UpdateCompleter) =
    update(this::update, RoleClaim, completer)

fun KotlinUpdateBuilder.updateAllColumns(record: RoleClaimRecord) =
    apply {
        set(id).equalTo(record::id)
        set(claimType).equalTo(record::claimType)
        set(claimValue).equalTo(record::claimValue)
        set(roleId).equalTo(record::roleId)
    }

fun KotlinUpdateBuilder.updateSelectiveColumns(record: RoleClaimRecord) =
    apply {
        set(id).equalToWhenPresent(record::id)
        set(claimType).equalToWhenPresent(record::claimType)
        set(claimValue).equalToWhenPresent(record::claimValue)
        set(roleId).equalToWhenPresent(record::roleId)
    }

fun RoleClaimMapper.updateByPrimaryKey(record: RoleClaimRecord) =
    update {
        set(claimType).equalTo(record::claimType)
        set(claimValue).equalTo(record::claimValue)
        set(roleId).equalTo(record::roleId)
        where(id, isEqualTo(record::id))
    }

fun RoleClaimMapper.updateByPrimaryKeySelective(record: RoleClaimRecord) =
    update {
        set(claimType).equalToWhenPresent(record::claimType)
        set(claimValue).equalToWhenPresent(record::claimValue)
        set(roleId).equalToWhenPresent(record::roleId)
        where(id, isEqualTo(record::id))
    }