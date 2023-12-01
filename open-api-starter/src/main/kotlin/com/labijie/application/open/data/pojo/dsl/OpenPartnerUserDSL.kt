@file:Suppress("RedundantVisibilityModifier")

package com.labijie.application.`open`.`data`.pojo.dsl

import com.labijie.application.`open`.`data`.OpenPartnerUserTable
import com.labijie.application.`open`.`data`.OpenPartnerUserTable.partnerId
import com.labijie.application.`open`.`data`.OpenPartnerUserTable.userId
import com.labijie.application.`open`.`data`.pojo.OpenPartnerUser
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
import org.jetbrains.exposed.sql.batchInsert
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.statements.InsertStatement
import org.jetbrains.exposed.sql.statements.UpdateBuilder
import org.jetbrains.exposed.sql.update

/**
 * DSL support for OpenPartnerUserTable
 *
 * This class made by a code generation tool (https://github.com/hongque-pro/infra-orm).
 *
 * Don't modify these codes !!
 *
 * Origin Exposed Table:
 * @see com.labijie.application.open.data.OpenPartnerUserTable
 */
@kotlin.Suppress(
  "unused",
  "DuplicatedCode",
  "MemberVisibilityCanBePrivate",
  "RemoveRedundantQualifierName",
)
public object OpenPartnerUserDSL {
  public val OpenPartnerUserTable.allColumns: Array<Column<*>> by lazy {
    arrayOf(
    userId,
    partnerId,
    )
  }


  public fun parseRow(raw: ResultRow): OpenPartnerUser {
    val plain = OpenPartnerUser()
    plain.userId = raw[userId]
    plain.partnerId = raw[partnerId]
    return plain
  }

  public fun parseRowSelective(row: ResultRow): OpenPartnerUser {
    val plain = OpenPartnerUser()
    if(row.hasValue(userId)) {
      plain.userId = row[userId]
    }
    if(row.hasValue(partnerId)) {
      plain.partnerId = row[partnerId]
    }
    return plain
  }

  public fun <T> OpenPartnerUserTable.getColumnType(column: Column<T>): KClass<*> = when(column) {
    userId->Long::class
    partnerId->Long::class
    else->throw
        IllegalArgumentException("""Unknown column <${column.name}> for 'OpenPartnerUser'""")
  }

  @kotlin.Suppress("UNCHECKED_CAST")
  public fun <T> OpenPartnerUser.getColumnValue(column: Column<T>): T = when(column) {
    OpenPartnerUserTable.userId->this.userId as T
    OpenPartnerUserTable.partnerId->this.partnerId as T
    else->throw
        IllegalArgumentException("""Unknown column <${column.name}> for 'OpenPartnerUser'""")
  }

  public fun assign(
    builder: UpdateBuilder<*>,
    raw: OpenPartnerUser,
    selective: Array<out Column<*>>? = null,
    vararg ignore: Column<*>,
  ) {
    val list = if(selective.isNullOrEmpty()) null else selective
    if((list == null || list.contains(userId)) && !ignore.contains(userId))
      builder[userId] = raw.userId
    if((list == null || list.contains(partnerId)) && !ignore.contains(partnerId))
      builder[partnerId] = raw.partnerId
  }

  public fun ResultRow.toOpenPartnerUser(vararg selective: Column<*>): OpenPartnerUser {
    if(selective.isNotEmpty()) {
      return parseRowSelective(this)
    }
    return parseRow(this)
  }

  public fun Iterable<ResultRow>.toOpenPartnerUserList(vararg selective: Column<*>):
      List<OpenPartnerUser> = this.map {
    it.toOpenPartnerUser(*selective)
  }

  public fun OpenPartnerUserTable.selectSlice(vararg selective: Column<*>): Query {
    val query = if(selective.isNotEmpty()) {
      slice(selective.toList()).selectAll()
    }
    else {
      selectAll()
    }
    return query
  }

  public fun UpdateBuilder<*>.setValue(raw: OpenPartnerUser, vararg ignore: Column<*>): Unit =
      assign(this, raw, ignore = ignore)

  public fun UpdateBuilder<*>.setValueSelective(raw: OpenPartnerUser, vararg selective: Column<*>):
      Unit = assign(this, raw, selective = selective)

  public fun OpenPartnerUserTable.insert(raw: OpenPartnerUser): InsertStatement<Number> = insert {
    assign(it, raw)
  }

  public fun OpenPartnerUserTable.batchInsert(
    list: Iterable<OpenPartnerUser>,
    ignoreErrors: Boolean = false,
    shouldReturnGeneratedValues: Boolean = false,
  ): List<ResultRow> {
    val rows = batchInsert(list, ignoreErrors, shouldReturnGeneratedValues) {
      entry -> assign(this, entry)
    }
    return rows
  }

  public fun OpenPartnerUserTable.update(
    raw: OpenPartnerUser,
    selective: Array<out Column<*>>? = null,
    ignore: Array<out Column<*>>? = null,
    limit: Int? = null,
    `where`: SqlExpressionBuilder.() -> Op<Boolean>,
  ): Int = update(`where`, limit) {
    val ignoreColumns = ignore ?: arrayOf()
    assign(it, raw, selective = selective, *ignoreColumns)
  }

  public fun OpenPartnerUserTable.selectMany(vararg selective: Column<*>,
      `where`: Query.() -> Unit): List<OpenPartnerUser> {
    val query = selectSlice(*selective)
    `where`.invoke(query)
    return query.toOpenPartnerUserList(*selective)
  }

  public fun OpenPartnerUserTable.selectOne(vararg selective: Column<*>, `where`: Query.() -> Unit):
      OpenPartnerUser? {
    val query = selectSlice(*selective)
    `where`.invoke(query)
    return query.firstOrNull()?.toOpenPartnerUser(*selective)
  }
}
