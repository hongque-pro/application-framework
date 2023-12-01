@file:Suppress("RedundantVisibilityModifier")

package com.labijie.application.identity.`data`.pojo.dsl

import com.labijie.application.identity.`data`.UserOpenIdTable
import com.labijie.application.identity.`data`.UserOpenIdTable.appId
import com.labijie.application.identity.`data`.UserOpenIdTable.loginProvider
import com.labijie.application.identity.`data`.UserOpenIdTable.openId
import com.labijie.application.identity.`data`.UserOpenIdTable.userId
import com.labijie.application.identity.`data`.pojo.UserOpenId
import java.lang.IllegalArgumentException
import kotlin.Array
import kotlin.Boolean
import kotlin.Int
import kotlin.Long
import kotlin.Number
import kotlin.String
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
 * DSL support for UserOpenIdTable
 *
 * This class made by a code generation tool (https://github.com/hongque-pro/infra-orm).
 *
 * Don't modify these codes !!
 *
 * Origin Exposed Table:
 * @see com.labijie.application.identity.data.UserOpenIdTable
 */
@kotlin.Suppress(
  "unused",
  "DuplicatedCode",
  "MemberVisibilityCanBePrivate",
  "RemoveRedundantQualifierName",
)
public object UserOpenIdDSL {
  public val UserOpenIdTable.allColumns: Array<Column<*>> by lazy {
    arrayOf(
    userId,
    appId,
    loginProvider,
    openId,
    )
  }


  public fun parseRow(raw: ResultRow): UserOpenId {
    val plain = UserOpenId()
    plain.userId = raw[userId]
    plain.appId = raw[appId]
    plain.loginProvider = raw[loginProvider]
    plain.openId = raw[openId]
    return plain
  }

  public fun parseRowSelective(row: ResultRow): UserOpenId {
    val plain = UserOpenId()
    if(row.hasValue(userId)) {
      plain.userId = row[userId]
    }
    if(row.hasValue(appId)) {
      plain.appId = row[appId]
    }
    if(row.hasValue(loginProvider)) {
      plain.loginProvider = row[loginProvider]
    }
    if(row.hasValue(openId)) {
      plain.openId = row[openId]
    }
    return plain
  }

  public fun <T> UserOpenIdTable.getColumnType(column: Column<T>): KClass<*> = when(column) {
    userId->Long::class
    appId->String::class
    loginProvider->String::class
    openId->String::class
    else->throw IllegalArgumentException("""Unknown column <${column.name}> for 'UserOpenId'""")
  }

  @kotlin.Suppress("UNCHECKED_CAST")
  public fun <T> UserOpenId.getColumnValue(column: Column<T>): T = when(column) {
    UserOpenIdTable.userId->this.userId as T
    UserOpenIdTable.appId->this.appId as T
    UserOpenIdTable.loginProvider->this.loginProvider as T
    UserOpenIdTable.openId->this.openId as T
    else->throw IllegalArgumentException("""Unknown column <${column.name}> for 'UserOpenId'""")
  }

  public fun assign(
    builder: UpdateBuilder<*>,
    raw: UserOpenId,
    selective: Array<out Column<*>>? = null,
    vararg ignore: Column<*>,
  ) {
    val list = if(selective.isNullOrEmpty()) null else selective
    if((list == null || list.contains(userId)) && !ignore.contains(userId))
      builder[userId] = raw.userId
    if((list == null || list.contains(appId)) && !ignore.contains(appId))
      builder[appId] = raw.appId
    if((list == null || list.contains(loginProvider)) && !ignore.contains(loginProvider))
      builder[loginProvider] = raw.loginProvider
    if((list == null || list.contains(openId)) && !ignore.contains(openId))
      builder[openId] = raw.openId
  }

  public fun ResultRow.toUserOpenId(vararg selective: Column<*>): UserOpenId {
    if(selective.isNotEmpty()) {
      return parseRowSelective(this)
    }
    return parseRow(this)
  }

  public fun Iterable<ResultRow>.toUserOpenIdList(vararg selective: Column<*>): List<UserOpenId> =
      this.map {
    it.toUserOpenId(*selective)
  }

  public fun UserOpenIdTable.selectSlice(vararg selective: Column<*>): Query {
    val query = if(selective.isNotEmpty()) {
      slice(selective.toList()).selectAll()
    }
    else {
      selectAll()
    }
    return query
  }

  public fun UpdateBuilder<*>.setValue(raw: UserOpenId, vararg ignore: Column<*>): Unit =
      assign(this, raw, ignore = ignore)

  public fun UpdateBuilder<*>.setValueSelective(raw: UserOpenId, vararg selective: Column<*>): Unit
      = assign(this, raw, selective = selective)

  public fun UserOpenIdTable.insert(raw: UserOpenId): InsertStatement<Number> = insert {
    assign(it, raw)
  }

  public fun UserOpenIdTable.batchInsert(
    list: Iterable<UserOpenId>,
    ignoreErrors: Boolean = false,
    shouldReturnGeneratedValues: Boolean = false,
  ): List<ResultRow> {
    val rows = batchInsert(list, ignoreErrors, shouldReturnGeneratedValues) {
      entry -> assign(this, entry)
    }
    return rows
  }

  public fun UserOpenIdTable.update(
    raw: UserOpenId,
    selective: Array<out Column<*>>? = null,
    ignore: Array<out Column<*>>? = null,
    limit: Int? = null,
    `where`: SqlExpressionBuilder.() -> Op<Boolean>,
  ): Int = update(`where`, limit) {
    val ignoreColumns = ignore ?: arrayOf()
    assign(it, raw, selective = selective, *ignoreColumns)
  }

  public fun UserOpenIdTable.updateByPrimaryKey(raw: UserOpenId, vararg selective: Column<*>): Int =
      update(raw, selective = selective, ignore = arrayOf(userId, appId, loginProvider)) {
    UserOpenIdTable.userId.eq(raw.userId) and UserOpenIdTable.appId.eq(raw.appId) and
        UserOpenIdTable.loginProvider.eq(raw.loginProvider)
  }

  public fun UserOpenIdTable.updateByPrimaryKey(
    userId: Long,
    appId: String,
    loginProvider: String,
    builder: UserOpenIdTable.(UpdateStatement) -> Unit,
  ): Int = update({ UserOpenIdTable.userId.eq(userId) and UserOpenIdTable.appId.eq(appId) and
      UserOpenIdTable.loginProvider.eq(loginProvider) }, body = builder)

  public fun UserOpenIdTable.deleteByPrimaryKey(
    userId: Long,
    appId: String,
    loginProvider: String,
  ): Int = deleteWhere {
    UserOpenIdTable.userId.eq(userId) and UserOpenIdTable.appId.eq(appId) and
        UserOpenIdTable.loginProvider.eq(loginProvider)
  }

  public fun UserOpenIdTable.selectByPrimaryKey(
    userId: Long,
    appId: String,
    loginProvider: String,
    vararg selective: Column<*>,
  ): UserOpenId? {
    val query = selectSlice(*selective).andWhere {
      UserOpenIdTable.userId.eq(userId) and UserOpenIdTable.appId.eq(appId) and
          UserOpenIdTable.loginProvider.eq(loginProvider)
    }
    return query.firstOrNull()?.toUserOpenId(*selective)
  }

  public fun UserOpenIdTable.selectMany(vararg selective: Column<*>, `where`: Query.() -> Unit):
      List<UserOpenId> {
    val query = selectSlice(*selective)
    `where`.invoke(query)
    return query.toUserOpenIdList(*selective)
  }

  public fun UserOpenIdTable.selectOne(vararg selective: Column<*>, `where`: Query.() -> Unit):
      UserOpenId? {
    val query = selectSlice(*selective)
    `where`.invoke(query)
    return query.firstOrNull()?.toUserOpenId(*selective)
  }
}
