@file:Suppress("RedundantVisibilityModifier")

package com.labijie.application.identity.`data`.pojo.dsl

import com.labijie.application.identity.`data`.UserRoleTable
import com.labijie.application.identity.`data`.UserRoleTable.roleId
import com.labijie.application.identity.`data`.UserRoleTable.userId
import com.labijie.application.identity.`data`.pojo.UserRole
import java.lang.IllegalArgumentException
import kotlin.Array
import kotlin.Boolean
import kotlin.Int
import kotlin.Long
import kotlin.Number
import kotlin.Unit
import kotlin.collections.Iterable
import kotlin.collections.List
import kotlin.collections.isNotEmpty
import kotlin.collections.toList
import kotlin.reflect.KClass
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Op
import org.jetbrains.exposed.sql.Query
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SqlExpressionBuilder
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.andWhere
import org.jetbrains.exposed.sql.batchInsert
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.statements.InsertStatement
import org.jetbrains.exposed.sql.statements.UpdateBuilder
import org.jetbrains.exposed.sql.statements.UpdateStatement
import org.jetbrains.exposed.sql.update

/**
 * DSL support for UserRoleTable
 *
 * This class made by a code generation tool (https://github.com/hongque-pro/infra-orm).
 *
 * Don't modify these codes !!
 *
 * Origin Exposed Table:
 * @see com.labijie.application.identity.data.UserRoleTable
 */
@kotlin.Suppress(
  "unused",
  "DuplicatedCode",
  "MemberVisibilityCanBePrivate",
  "RemoveRedundantQualifierName",
)
public object UserRoleDSL {
  public val UserRoleTable.allColumns: Array<Column<*>> by lazy {
    arrayOf(
    userId,
    roleId,
    )
  }


  public fun parseRow(raw: ResultRow): UserRole {
    val plain = UserRole()
    plain.userId = raw[userId]
    plain.roleId = raw[roleId]
    return plain
  }

  public fun parseRowSelective(row: ResultRow): UserRole {
    val plain = UserRole()
    if(row.hasValue(userId)) {
      plain.userId = row[userId]
    }
    if(row.hasValue(roleId)) {
      plain.roleId = row[roleId]
    }
    return plain
  }

  public fun <T> UserRoleTable.getColumnType(column: Column<T>): KClass<*> = when(column) {
    userId->Long::class
    roleId->Long::class
    else->throw IllegalArgumentException("""Unknown column <${column.name}> for 'UserRole'""")
  }

  @kotlin.Suppress("UNCHECKED_CAST")
  public fun <T> UserRole.getColumnValue(column: Column<T>): T = when(column) {
    UserRoleTable.userId->this.userId as T
    UserRoleTable.roleId->this.roleId as T
    else->throw IllegalArgumentException("""Unknown column <${column.name}> for 'UserRole'""")
  }

  public fun assign(
    builder: UpdateBuilder<*>,
    raw: UserRole,
    selective: Array<out Column<*>>? = null,
    vararg ignore: Column<*>,
  ) {
    val list = if(selective.isNullOrEmpty()) null else selective
    if((list == null || list.contains(userId)) && !ignore.contains(userId))
      builder[userId] = raw.userId
    if((list == null || list.contains(roleId)) && !ignore.contains(roleId))
      builder[roleId] = raw.roleId
  }

  public fun ResultRow.toUserRole(vararg selective: Column<*>): UserRole {
    if(selective.isNotEmpty()) {
      return parseRowSelective(this)
    }
    return parseRow(this)
  }

  public fun Iterable<ResultRow>.toUserRoleList(vararg selective: Column<*>): List<UserRole> =
      this.map {
    it.toUserRole(*selective)
  }

  public fun UserRoleTable.selectSlice(vararg selective: Column<*>): Query {
    val query = if(selective.isNotEmpty()) {
      slice(selective.toList()).selectAll()
    }
    else {
      selectAll()
    }
    return query
  }

  public fun UpdateBuilder<*>.setValue(raw: UserRole, vararg ignore: Column<*>): Unit = assign(this,
      raw, ignore = ignore)

  public fun UpdateBuilder<*>.setValueSelective(raw: UserRole, vararg selective: Column<*>): Unit =
      assign(this, raw, selective = selective)

  public fun UserRoleTable.insert(raw: UserRole): InsertStatement<Number> = insert {
    assign(it, raw)
  }

  public fun UserRoleTable.batchInsert(
    list: Iterable<UserRole>,
    ignoreErrors: Boolean = false,
    shouldReturnGeneratedValues: Boolean = false,
  ): List<ResultRow> {
    val rows = batchInsert(list, ignoreErrors, shouldReturnGeneratedValues) {
      entry -> assign(this, entry)
    }
    return rows
  }

  public fun UserRoleTable.update(
    raw: UserRole,
    selective: Array<out Column<*>>? = null,
    ignore: Array<out Column<*>>? = null,
    limit: Int? = null,
    `where`: SqlExpressionBuilder.() -> Op<Boolean>,
  ): Int = update(`where`, limit) {
    val ignoreColumns = ignore ?: arrayOf()
    assign(it, raw, selective = selective, *ignoreColumns)
  }

  public fun UserRoleTable.updateByPrimaryKey(raw: UserRole, vararg selective: Column<*>): Int =
      update(raw, selective = selective, ignore = arrayOf(userId, roleId)) {
    UserRoleTable.userId.eq(raw.userId) and UserRoleTable.roleId.eq(raw.roleId)
  }

  public fun UserRoleTable.updateByPrimaryKey(
    userId: Long,
    roleId: Long,
    builder: UserRoleTable.(UpdateStatement) -> Unit,
  ): Int = update({ UserRoleTable.userId.eq(userId) and UserRoleTable.roleId.eq(roleId) }, body =
      builder)

  public fun UserRoleTable.deleteByPrimaryKey(userId: Long, roleId: Long): Int = deleteWhere {
    UserRoleTable.userId.eq(userId) and UserRoleTable.roleId.eq(roleId)
  }

  public fun UserRoleTable.selectByPrimaryKey(
    userId: Long,
    roleId: Long,
    vararg selective: Column<*>,
  ): UserRole? {
    val query = selectSlice(*selective).andWhere {
      UserRoleTable.userId.eq(userId) and UserRoleTable.roleId.eq(roleId)
    }
    return query.firstOrNull()?.toUserRole(*selective)
  }

  public fun UserRoleTable.selectMany(vararg selective: Column<*>, `where`: Query.() -> Unit):
      List<UserRole> {
    val query = selectSlice(*selective)
    `where`.invoke(query)
    return query.toUserRoleList(*selective)
  }

  public fun UserRoleTable.selectOne(vararg selective: Column<*>, `where`: Query.() -> Unit):
      UserRole? {
    val query = selectSlice(*selective)
    `where`.invoke(query)
    return query.firstOrNull()?.toUserRole(*selective)
  }
}
