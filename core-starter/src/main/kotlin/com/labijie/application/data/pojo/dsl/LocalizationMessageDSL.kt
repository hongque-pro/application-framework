@file:Suppress("RedundantVisibilityModifier")

package com.labijie.application.`data`.pojo.dsl

import com.labijie.application.`data`.LocalizationMessageTable
import com.labijie.application.`data`.LocalizationMessageTable.code
import com.labijie.application.`data`.LocalizationMessageTable.locale
import com.labijie.application.`data`.LocalizationMessageTable.message
import com.labijie.application.`data`.pojo.LocalizationMessage
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
 * DSL support for LocalizationMessageTable
 *
 * This class made by a code generation tool (https://github.com/hongque-pro/infra-orm).
 *
 * Don't modify these codes !!
 *
 * Origin Exposed Table:
 * @see com.labijie.application.data.LocalizationMessageTable
 */
@kotlin.Suppress(
  "unused",
  "DuplicatedCode",
  "MemberVisibilityCanBePrivate",
  "RemoveRedundantQualifierName",
)
public object LocalizationMessageDSL {
  public val LocalizationMessageTable.allColumns: Array<Column<*>> by lazy {
    arrayOf(
    locale,
    code,
    message,
    )
  }

  public fun parseRow(raw: ResultRow): LocalizationMessage {
    val plain = LocalizationMessage()
    plain.locale = raw[locale]
    plain.code = raw[code]
    plain.message = raw[message]
    return plain
  }

  public fun parseRowSelective(row: ResultRow): LocalizationMessage {
    val plain = LocalizationMessage()
    if(row.hasValue(locale)) {
      plain.locale = row[locale]
    }
    if(row.hasValue(code)) {
      plain.code = row[code]
    }
    if(row.hasValue(message)) {
      plain.message = row[message]
    }
    return plain
  }

  public fun <T> LocalizationMessageTable.getColumnType(column: Column<T>): KClass<*> =
      when(column) {
    locale->String::class
    code->String::class
    message->String::class
    else->throw
        IllegalArgumentException("""Unknown column <${column.name}> for 'LocalizationMessage'""")
  }

  @kotlin.Suppress("UNCHECKED_CAST")
  public fun <T> LocalizationMessage.getColumnValue(column: Column<T>): T = when(column) {
    LocalizationMessageTable.locale->this.locale as T
    LocalizationMessageTable.code->this.code as T
    LocalizationMessageTable.message->this.message as T
    else->throw
        IllegalArgumentException("""Unknown column <${column.name}> for 'LocalizationMessage'""")
  }

  public fun assign(
    builder: UpdateBuilder<*>,
    raw: LocalizationMessage,
    selective: Array<out Column<*>>? = null,
    vararg ignore: Column<*>,
  ) {
    val list = if(selective.isNullOrEmpty()) null else selective
    if((list == null || list.contains(locale)) && !ignore.contains(locale))
      builder[locale] = raw.locale
    if((list == null || list.contains(code)) && !ignore.contains(code))
      builder[code] = raw.code
    if((list == null || list.contains(message)) && !ignore.contains(message))
      builder[message] = raw.message
  }

  public fun ResultRow.toLocalizationMessage(vararg selective: Column<*>): LocalizationMessage {
    if(selective.isNotEmpty()) {
      return parseRowSelective(this)
    }
    return parseRow(this)
  }

  public fun Iterable<ResultRow>.toLocalizationMessageList(vararg selective: Column<*>):
      List<LocalizationMessage> = this.map {
    it.toLocalizationMessage(*selective)
  }

  public fun LocalizationMessageTable.selectSlice(vararg selective: Column<*>): Query {
    val query = if(selective.isNotEmpty()) {
      select(selective.toList())
    }
    else {
      selectAll()
    }
    return query
  }

  public fun UpdateBuilder<*>.setValue(raw: LocalizationMessage, vararg ignore: Column<*>): Unit =
      assign(this, raw, ignore = ignore)

  public fun UpdateBuilder<*>.setValueSelective(raw: LocalizationMessage, vararg
      selective: Column<*>): Unit = assign(this, raw, selective = selective)

  public fun LocalizationMessageTable.insert(raw: LocalizationMessage): InsertStatement<Number> =
      insert {
    assign(it, raw)
  }

  public fun LocalizationMessageTable.upsert(
    raw: LocalizationMessage,
    onUpdateExclude: List<Column<*>>? = null,
    onUpdate: (UpsertBuilder.(UpdateStatement) -> Unit)? = null,
    `where`: (SqlExpressionBuilder.() -> Op<Boolean>)? = null,
  ): UpsertStatement<Long> = upsert(where = where, onUpdate = onUpdate, onUpdateExclude =
      onUpdateExclude) {
    assign(it, raw)
  }

  public fun LocalizationMessageTable.batchInsert(
    list: Iterable<LocalizationMessage>,
    ignoreErrors: Boolean = false,
    shouldReturnGeneratedValues: Boolean = false,
  ): List<ResultRow> {
    val rows = batchInsert(list, ignoreErrors, shouldReturnGeneratedValues) {
      entry -> assign(this, entry)
    }
    return rows
  }

  public fun LocalizationMessageTable.update(
    raw: LocalizationMessage,
    selective: Array<out Column<*>>? = null,
    ignore: Array<out Column<*>>? = null,
    limit: Int? = null,
    `where`: SqlExpressionBuilder.() -> Op<Boolean>,
  ): Int = update(`where`, limit) {
    val ignoreColumns = ignore ?: arrayOf()
    assign(it, raw, selective = selective, *ignoreColumns)
  }

  public fun LocalizationMessageTable.updateByPrimaryKey(raw: LocalizationMessage, vararg
      selective: Column<*>): Int = update(raw, selective = selective, ignore = arrayOf(locale,
      code)) {
    LocalizationMessageTable.locale.eq(raw.locale) and LocalizationMessageTable.code.eq(raw.code)
  }

  public fun LocalizationMessageTable.updateByPrimaryKey(
    locale: String,
    code: String,
    builder: LocalizationMessageTable.(UpdateStatement) -> Unit,
  ): Int = update({ LocalizationMessageTable.locale.eq(locale) and
      LocalizationMessageTable.code.eq(code) }, body = builder)

  public fun LocalizationMessageTable.deleteByPrimaryKey(locale: String, code: String): Int =
      deleteWhere {
    LocalizationMessageTable.locale.eq(locale) and LocalizationMessageTable.code.eq(code)
  }

  public fun LocalizationMessageTable.selectByPrimaryKey(
    locale: String,
    code: String,
    vararg selective: Column<*>,
  ): LocalizationMessage? {
    val query = selectSlice(*selective).andWhere {
      LocalizationMessageTable.locale.eq(locale) and LocalizationMessageTable.code.eq(code)
    }
    return query.firstOrNull()?.toLocalizationMessage(*selective)
  }

  public fun LocalizationMessageTable.selectMany(vararg selective: Column<*>,
      `where`: Query.() -> Query?): List<LocalizationMessage> {
    val query = selectSlice(*selective)
    `where`.invoke(query)
    return query.toLocalizationMessageList(*selective)
  }

  public fun LocalizationMessageTable.selectOne(vararg selective: Column<*>,
      `where`: Query.() -> Query?): LocalizationMessage? {
    val query = selectSlice(*selective)
    `where`.invoke(query)
    return query.firstOrNull()?.toLocalizationMessage(*selective)
  }

  public fun LocalizationMessageTable.replace(raw: LocalizationMessage): ReplaceStatement<Long> =
      replace {
    assign(it, raw)
  }
}
