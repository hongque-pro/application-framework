@file:Suppress("RedundantVisibilityModifier")

package com.labijie.application.`data`.pojo.dsl

import com.labijie.application.`data`.LocalizationCodeTable
import com.labijie.application.`data`.LocalizationCodeTable.code
import com.labijie.application.`data`.pojo.LocalizationCode
import com.labijie.infra.orm.OffsetList
import com.labijie.infra.orm.OffsetList.Companion.decodeToken
import com.labijie.infra.orm.OffsetList.Companion.encodeToken
import java.lang.IllegalArgumentException
import java.util.Base64
import kotlin.Array
import kotlin.Boolean
import kotlin.Comparable
import kotlin.Int
import kotlin.Long
import kotlin.Number
import kotlin.String
import kotlin.Unit
import kotlin.collections.Collection
import kotlin.collections.Iterable
import kotlin.collections.List
import kotlin.collections.isNotEmpty
import kotlin.collections.last
import kotlin.collections.toList
import kotlin.reflect.KClass
import kotlin.text.Charsets
import kotlin.text.toByteArray
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Op
import org.jetbrains.exposed.sql.Query
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.SqlExpressionBuilder
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.andWhere
import org.jetbrains.exposed.sql.batchInsert
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.replace
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.statements.InsertStatement
import org.jetbrains.exposed.sql.statements.ReplaceStatement
import org.jetbrains.exposed.sql.statements.UpdateBuilder
import org.jetbrains.exposed.sql.statements.UpdateStatement
import org.jetbrains.exposed.sql.statements.UpsertBuilder
import org.jetbrains.exposed.sql.statements.UpsertStatement
import org.jetbrains.exposed.sql.update
import org.jetbrains.exposed.sql.upsert

/**
 * DSL support for LocalizationCodeTable
 *
 * This class made by a code generation tool (https://github.com/hongque-pro/infra-orm).
 *
 * Don't modify these codes !!
 *
 * Origin Exposed Table:
 * @see com.labijie.application.data.LocalizationCodeTable
 */
@kotlin.Suppress(
  "unused",
  "DuplicatedCode",
  "MemberVisibilityCanBePrivate",
  "RemoveRedundantQualifierName",
)
public object LocalizationCodeDSL {
  public val LocalizationCodeTable.allColumns: Array<Column<*>> by lazy {
    arrayOf(
    code,
    )
  }

  public fun parseRow(raw: ResultRow): LocalizationCode {
    val plain = LocalizationCode()
    plain.code = raw[code]
    return plain
  }

  public fun parseRowSelective(row: ResultRow): LocalizationCode {
    val plain = LocalizationCode()
    if(row.hasValue(code)) {
      plain.code = row[code]
    }
    return plain
  }

  public fun <T> LocalizationCodeTable.getColumnType(column: Column<T>): KClass<*> = when(column) {
    code->String::class
    else->throw
        IllegalArgumentException("""Unknown column <${column.name}> for 'LocalizationCode'""")
  }

  @kotlin.Suppress("UNCHECKED_CAST")
  public fun <T> LocalizationCode.getColumnValue(column: Column<T>): T = when(column) {
    LocalizationCodeTable.code->this.code as T
    else->throw
        IllegalArgumentException("""Unknown column <${column.name}> for 'LocalizationCode'""")
  }

  public fun assign(
    builder: UpdateBuilder<*>,
    raw: LocalizationCode,
    selective: Array<out Column<*>>? = null,
    vararg ignore: Column<*>,
  ) {
    val list = if(selective.isNullOrEmpty()) null else selective
    if((list == null || list.contains(code)) && !ignore.contains(code))
      builder[code] = raw.code
  }

  public fun ResultRow.toLocalizationCode(vararg selective: Column<*>): LocalizationCode {
    if(selective.isNotEmpty()) {
      return parseRowSelective(this)
    }
    return parseRow(this)
  }

  public fun Iterable<ResultRow>.toLocalizationCodeList(vararg selective: Column<*>):
      List<LocalizationCode> = this.map {
    it.toLocalizationCode(*selective)
  }

  public fun LocalizationCodeTable.selectSlice(vararg selective: Column<*>): Query {
    val query = if(selective.isNotEmpty()) {
      select(selective.toList())
    }
    else {
      selectAll()
    }
    return query
  }

  public fun UpdateBuilder<*>.setValue(raw: LocalizationCode, vararg ignore: Column<*>): Unit =
      assign(this, raw, ignore = ignore)

  public fun UpdateBuilder<*>.setValueSelective(raw: LocalizationCode, vararg selective: Column<*>):
      Unit = assign(this, raw, selective = selective)

  public fun LocalizationCodeTable.insert(raw: LocalizationCode): InsertStatement<Number> = insert {
    assign(it, raw)
  }

  public fun LocalizationCodeTable.upsert(
    raw: LocalizationCode,
    onUpdateExclude: List<Column<*>>? = null,
    onUpdate: (UpsertBuilder.(UpdateStatement) -> Unit)? = null,
    `where`: (SqlExpressionBuilder.() -> Op<Boolean>)? = null,
  ): UpsertStatement<Long> = upsert(where = where, onUpdate = onUpdate, onUpdateExclude =
      onUpdateExclude) {
    assign(it, raw)
  }

  public fun LocalizationCodeTable.batchInsert(
    list: Iterable<LocalizationCode>,
    ignoreErrors: Boolean = false,
    shouldReturnGeneratedValues: Boolean = false,
  ): List<ResultRow> {
    val rows = batchInsert(list, ignoreErrors, shouldReturnGeneratedValues) {
      entry -> assign(this, entry)
    }
    return rows
  }

  public fun LocalizationCodeTable.update(
    raw: LocalizationCode,
    selective: Array<out Column<*>>? = null,
    ignore: Array<out Column<*>>? = null,
    limit: Int? = null,
    `where`: SqlExpressionBuilder.() -> Op<Boolean>,
  ): Int = update(`where`, limit) {
    val ignoreColumns = ignore ?: arrayOf()
    assign(it, raw, selective = selective, *ignoreColumns)
  }

  public fun LocalizationCodeTable.updateByPrimaryKey(raw: LocalizationCode, vararg
      selective: Column<*>): Int = update(raw, selective = selective, ignore = arrayOf(code)) {
    LocalizationCodeTable.code.eq(raw.code)
  }

  public fun LocalizationCodeTable.updateByPrimaryKey(code: String,
      builder: LocalizationCodeTable.(UpdateStatement) -> Unit): Int = update({
      LocalizationCodeTable.code.eq(code) }, body = builder)

  public fun LocalizationCodeTable.deleteByPrimaryKey(code: String): Int = deleteWhere {
    LocalizationCodeTable.code.eq(code)
  }

  public fun LocalizationCodeTable.selectByPrimaryKey(code: String, vararg selective: Column<*>):
      LocalizationCode? {
    val query = selectSlice(*selective).andWhere {
      LocalizationCodeTable.code.eq(code)
    }
    return query.firstOrNull()?.toLocalizationCode(*selective)
  }

  public fun LocalizationCodeTable.selectByPrimaryKeys(ids: Iterable<String>, vararg
      selective: Column<*>): List<LocalizationCode> {
    val query = selectSlice(*selective).andWhere {
      LocalizationCodeTable.code inList ids
    }
    return query.toLocalizationCodeList(*selective)
  }

  public fun LocalizationCodeTable.selectMany(vararg selective: Column<*>,
      `where`: Query.() -> Query?): List<LocalizationCode> {
    val query = selectSlice(*selective)
    `where`.invoke(query)
    return query.toLocalizationCodeList(*selective)
  }

  public fun LocalizationCodeTable.selectOne(vararg selective: Column<*>,
      `where`: Query.() -> Query?): LocalizationCode? {
    val query = selectSlice(*selective)
    `where`.invoke(query)
    return query.firstOrNull()?.toLocalizationCode(*selective)
  }

  public fun LocalizationCodeTable.selectForwardByPrimaryKey(
    forwardToken: String? = null,
    order: SortOrder = SortOrder.DESC,
    pageSize: Int = 50,
    selective: Collection<Column<*>> = listOf(),
    `where`: (Query.() -> Query?)? = null,
  ): OffsetList<LocalizationCode> {
    if(pageSize < 1) {
      return OffsetList.empty()
    }
    val offsetKey = forwardToken?.let { Base64.getUrlDecoder().decode(it).toString(Charsets.UTF_8) }
    val query = selectSlice(*selective.toTypedArray())
    offsetKey?.let {
      when(order) {
        SortOrder.DESC, SortOrder.DESC_NULLS_FIRST, SortOrder.DESC_NULLS_LAST->
        query.andWhere { code less it }
        else-> query.andWhere { code greater it }
      }
    }
    `where`?.invoke(query)
    val sorted = query.orderBy(code, order)
    val list = sorted.limit(pageSize).toLocalizationCodeList(*selective.toTypedArray())
    val token = if(list.size >= pageSize) {
      val lastId = list.last().code.toString().toByteArray(Charsets.UTF_8)
      Base64.getUrlEncoder().encodeToString(lastId)
    }
    else {
      null
    }
    return OffsetList(list, token)
  }

  public fun <T : Comparable<T>> LocalizationCodeTable.selectForward(
    sortColumn: Column<T>,
    forwardToken: String? = null,
    order: SortOrder = SortOrder.DESC,
    pageSize: Int = 50,
    selective: Collection<Column<*>> = listOf(),
    `where`: (Query.() -> Query?)? = null,
  ): OffsetList<LocalizationCode> {
    if(pageSize < 1) {
      return OffsetList.empty()
    }
    if(sortColumn == code) {
      return this.selectForwardByPrimaryKey(forwardToken, order, pageSize, selective, `where`)
    }
    val kp = forwardToken?.let { decodeToken(it) }
    val offsetKey = kp?.first
    val excludeKeys = kp?.second
    val query = selectSlice(*selective.toTypedArray())
    offsetKey?.let {
      when(order) {
        SortOrder.DESC, SortOrder.DESC_NULLS_FIRST, SortOrder.DESC_NULLS_LAST->
        query.andWhere { sortColumn lessEq it }
        else-> query.andWhere { sortColumn greaterEq it }
      }
    }
    excludeKeys?.let {
      if(it.isNotEmpty()) {
        query.andWhere { code notInList it }
      }
    }
    `where`?.invoke(query)
    val sorted = query.orderBy(Pair(sortColumn, order), Pair(code, order))
    val list = sorted.limit(pageSize).toLocalizationCodeList(*selective.toTypedArray())
    val token = if(list.size < pageSize) null else encodeToken(list, { getColumnValue(sortColumn) },
        LocalizationCode::code)
    return OffsetList(list, token)
  }

  public fun LocalizationCodeTable.replace(raw: LocalizationCode): ReplaceStatement<Long> =
      replace {
    assign(it, raw)
  }
}
