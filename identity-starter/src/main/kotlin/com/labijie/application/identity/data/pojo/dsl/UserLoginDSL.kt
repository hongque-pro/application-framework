@file:Suppress("RedundantVisibilityModifier")

package com.labijie.application.identity.`data`.pojo.dsl

import com.labijie.application.identity.`data`.UserLoginTable
import com.labijie.application.identity.`data`.UserLoginTable.loginProvider
import com.labijie.application.identity.`data`.UserLoginTable.providerDisplayName
import com.labijie.application.identity.`data`.UserLoginTable.providerKey
import com.labijie.application.identity.`data`.UserLoginTable.userId
import com.labijie.application.identity.`data`.pojo.UserLogin
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
 * DSL support for UserLoginTable
 *
 * This class made by a code generation tool (https://github.com/hongque-pro/infra-orm).
 *
 * Don't modify these codes !!
 *
 * Origin Exposed Table:
 * @see com.labijie.application.identity.data.UserLoginTable
 */
@kotlin.Suppress(
  "unused",
  "DuplicatedCode",
  "MemberVisibilityCanBePrivate",
  "RemoveRedundantQualifierName",
)
public object UserLoginDSL {
  public val UserLoginTable.allColumns: Array<Column<*>> by lazy {
    arrayOf(
    loginProvider,
    providerKey,
    providerDisplayName,
    userId,
    )
  }


  public fun parseRow(raw: ResultRow): UserLogin {
    val plain = UserLogin()
    plain.loginProvider = raw[loginProvider]
    plain.providerKey = raw[providerKey]
    plain.providerDisplayName = raw[providerDisplayName]
    plain.userId = raw[userId]
    return plain
  }

  public fun parseRowSelective(row: ResultRow): UserLogin {
    val plain = UserLogin()
    if(row.hasValue(loginProvider)) {
      plain.loginProvider = row[loginProvider]
    }
    if(row.hasValue(providerKey)) {
      plain.providerKey = row[providerKey]
    }
    if(row.hasValue(providerDisplayName)) {
      plain.providerDisplayName = row[providerDisplayName]
    }
    if(row.hasValue(userId)) {
      plain.userId = row[userId]
    }
    return plain
  }

  public fun <T> UserLoginTable.getColumnType(column: Column<T>): KClass<*> = when(column) {
    loginProvider->String::class
    providerKey->String::class
    providerDisplayName->String::class
    userId->Long::class
    else->throw IllegalArgumentException("""Unknown column <${column.name}> for 'UserLogin'""")
  }

  @kotlin.Suppress("UNCHECKED_CAST")
  public fun <T> UserLogin.getColumnValue(column: Column<T>): T = when(column) {
    UserLoginTable.loginProvider->this.loginProvider as T
    UserLoginTable.providerKey->this.providerKey as T
    UserLoginTable.providerDisplayName->this.providerDisplayName as T
    UserLoginTable.userId->this.userId as T
    else->throw IllegalArgumentException("""Unknown column <${column.name}> for 'UserLogin'""")
  }

  public fun assign(
    builder: UpdateBuilder<*>,
    raw: UserLogin,
    selective: Array<out Column<*>>? = null,
    vararg ignore: Column<*>,
  ) {
    val list = if(selective.isNullOrEmpty()) null else selective
    if((list == null || list.contains(loginProvider)) && !ignore.contains(loginProvider))
      builder[loginProvider] = raw.loginProvider
    if((list == null || list.contains(providerKey)) && !ignore.contains(providerKey))
      builder[providerKey] = raw.providerKey
    if((list == null || list.contains(providerDisplayName)) &&
        !ignore.contains(providerDisplayName))
      builder[providerDisplayName] = raw.providerDisplayName
    if((list == null || list.contains(userId)) && !ignore.contains(userId))
      builder[userId] = raw.userId
  }

  public fun ResultRow.toUserLogin(vararg selective: Column<*>): UserLogin {
    if(selective.isNotEmpty()) {
      return parseRowSelective(this)
    }
    return parseRow(this)
  }

  public fun Iterable<ResultRow>.toUserLoginList(vararg selective: Column<*>): List<UserLogin> =
      this.map {
    it.toUserLogin(*selective)
  }

  public fun UserLoginTable.selectSlice(vararg selective: Column<*>): Query {
    val query = if(selective.isNotEmpty()) {
      slice(selective.toList()).selectAll()
    }
    else {
      selectAll()
    }
    return query
  }

  public fun UpdateBuilder<*>.setValue(raw: UserLogin, vararg ignore: Column<*>): Unit =
      assign(this, raw, ignore = ignore)

  public fun UpdateBuilder<*>.setValueSelective(raw: UserLogin, vararg selective: Column<*>): Unit =
      assign(this, raw, selective = selective)

  public fun UserLoginTable.insert(raw: UserLogin): InsertStatement<Number> = insert {
    assign(it, raw)
  }

  public fun UserLoginTable.batchInsert(
    list: Iterable<UserLogin>,
    ignoreErrors: Boolean = false,
    shouldReturnGeneratedValues: Boolean = false,
  ): List<ResultRow> {
    val rows = batchInsert(list, ignoreErrors, shouldReturnGeneratedValues) {
      entry -> assign(this, entry)
    }
    return rows
  }

  public fun UserLoginTable.update(
    raw: UserLogin,
    selective: Array<out Column<*>>? = null,
    ignore: Array<out Column<*>>? = null,
    limit: Int? = null,
    `where`: SqlExpressionBuilder.() -> Op<Boolean>,
  ): Int = update(`where`, limit) {
    val ignoreColumns = ignore ?: arrayOf()
    assign(it, raw, selective = selective, *ignoreColumns)
  }

  public fun UserLoginTable.updateByPrimaryKey(raw: UserLogin, vararg selective: Column<*>): Int =
      update(raw, selective = selective, ignore = arrayOf(loginProvider, providerKey)) {
    UserLoginTable.loginProvider.eq(raw.loginProvider) and
        UserLoginTable.providerKey.eq(raw.providerKey)
  }

  public fun UserLoginTable.updateByPrimaryKey(
    loginProvider: String,
    providerKey: String,
    builder: UserLoginTable.(UpdateStatement) -> Unit,
  ): Int = update({ UserLoginTable.loginProvider.eq(loginProvider) and
      UserLoginTable.providerKey.eq(providerKey) }, body = builder)

  public fun UserLoginTable.deleteByPrimaryKey(loginProvider: String, providerKey: String): Int =
      deleteWhere {
    UserLoginTable.loginProvider.eq(loginProvider) and UserLoginTable.providerKey.eq(providerKey)
  }

  public fun UserLoginTable.selectByPrimaryKey(
    loginProvider: String,
    providerKey: String,
    vararg selective: Column<*>,
  ): UserLogin? {
    val query = selectSlice(*selective).andWhere {
      UserLoginTable.loginProvider.eq(loginProvider) and UserLoginTable.providerKey.eq(providerKey)
    }
    return query.firstOrNull()?.toUserLogin(*selective)
  }

  public fun UserLoginTable.selectMany(vararg selective: Column<*>, `where`: Query.() -> Unit):
      List<UserLogin> {
    val query = selectSlice(*selective)
    `where`.invoke(query)
    return query.toUserLoginList(*selective)
  }

  public fun UserLoginTable.selectOne(vararg selective: Column<*>, `where`: Query.() -> Unit):
      UserLogin? {
    val query = selectSlice(*selective)
    `where`.invoke(query)
    return query.firstOrNull()?.toUserLogin(*selective)
  }
}
