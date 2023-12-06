@file:Suppress("RedundantVisibilityModifier")

package com.labijie.application.`data`.pojo.dsl

import com.labijie.application.`data`.LocalizationLanguageTable
import com.labijie.application.`data`.LocalizationLanguageTable.country
import com.labijie.application.`data`.LocalizationLanguageTable.disabled
import com.labijie.application.`data`.LocalizationLanguageTable.language
import com.labijie.application.`data`.LocalizationLanguageTable.locale
import com.labijie.application.`data`.pojo.LocalizationLanguage
import com.labijie.infra.orm.OffsetList
import com.labijie.infra.orm.OffsetList.Companion.decodeToken
import com.labijie.infra.orm.OffsetList.Companion.encodeToken
import java.lang.IllegalArgumentException
import java.util.Base64
import kotlin.Array
import kotlin.Boolean
import kotlin.Comparable
import kotlin.Int
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
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.statements.InsertStatement
import org.jetbrains.exposed.sql.statements.UpdateBuilder
import org.jetbrains.exposed.sql.statements.UpdateStatement
import org.jetbrains.exposed.sql.update

/**
 * DSL support for LocalizationLanguageTable
 *
 * This class made by a code generation tool (https://github.com/hongque-pro/infra-orm).
 *
 * Don't modify these codes !!
 *
 * Origin Exposed Table:
 * @see com.labijie.application.data.LocalizationLanguageTable
 */
@kotlin.Suppress(
  "unused",
  "DuplicatedCode",
  "MemberVisibilityCanBePrivate",
  "RemoveRedundantQualifierName",
)
public object LocalizationLanguageDSL {
  public val LocalizationLanguageTable.allColumns: Array<Column<*>> by lazy {
    arrayOf(
    locale,
    language,
    country,
    disabled,
    )
  }


  public fun parseRow(raw: ResultRow): LocalizationLanguage {
    val plain = LocalizationLanguage()
    plain.locale = raw[locale]
    plain.language = raw[language]
    plain.country = raw[country]
    plain.disabled = raw[disabled]
    return plain
  }

  public fun parseRowSelective(row: ResultRow): LocalizationLanguage {
    val plain = LocalizationLanguage()
    if(row.hasValue(locale)) {
      plain.locale = row[locale]
    }
    if(row.hasValue(language)) {
      plain.language = row[language]
    }
    if(row.hasValue(country)) {
      plain.country = row[country]
    }
    if(row.hasValue(disabled)) {
      plain.disabled = row[disabled]
    }
    return plain
  }

  public fun <T> LocalizationLanguageTable.getColumnType(column: Column<T>): KClass<*> =
      when(column) {
    locale->String::class
    language->String::class
    country->String::class
    disabled->Boolean::class
    else->throw
        IllegalArgumentException("""Unknown column <${column.name}> for 'LocalizationLanguage'""")
  }

  @kotlin.Suppress("UNCHECKED_CAST")
  public fun <T> LocalizationLanguage.getColumnValue(column: Column<T>): T = when(column) {
    LocalizationLanguageTable.locale->this.locale as T
    LocalizationLanguageTable.language->this.language as T
    LocalizationLanguageTable.country->this.country as T
    LocalizationLanguageTable.disabled->this.disabled as T
    else->throw
        IllegalArgumentException("""Unknown column <${column.name}> for 'LocalizationLanguage'""")
  }

  public fun assign(
    builder: UpdateBuilder<*>,
    raw: LocalizationLanguage,
    selective: Array<out Column<*>>? = null,
    vararg ignore: Column<*>,
  ) {
    val list = if(selective.isNullOrEmpty()) null else selective
    if((list == null || list.contains(locale)) && !ignore.contains(locale))
      builder[locale] = raw.locale
    if((list == null || list.contains(language)) && !ignore.contains(language))
      builder[language] = raw.language
    if((list == null || list.contains(country)) && !ignore.contains(country))
      builder[country] = raw.country
    if((list == null || list.contains(disabled)) && !ignore.contains(disabled))
      builder[disabled] = raw.disabled
  }

  public fun ResultRow.toLocalizationLanguage(vararg selective: Column<*>): LocalizationLanguage {
    if(selective.isNotEmpty()) {
      return parseRowSelective(this)
    }
    return parseRow(this)
  }

  public fun Iterable<ResultRow>.toLocalizationLanguageList(vararg selective: Column<*>):
      List<LocalizationLanguage> = this.map {
    it.toLocalizationLanguage(*selective)
  }

  public fun LocalizationLanguageTable.selectSlice(vararg selective: Column<*>): Query {
    val query = if(selective.isNotEmpty()) {
      slice(selective.toList()).selectAll()
    }
    else {
      selectAll()
    }
    return query
  }

  public fun UpdateBuilder<*>.setValue(raw: LocalizationLanguage, vararg ignore: Column<*>): Unit =
      assign(this, raw, ignore = ignore)

  public fun UpdateBuilder<*>.setValueSelective(raw: LocalizationLanguage, vararg
      selective: Column<*>): Unit = assign(this, raw, selective = selective)

  public fun LocalizationLanguageTable.insert(raw: LocalizationLanguage): InsertStatement<Number> =
      insert {
    assign(it, raw)
  }

  public fun LocalizationLanguageTable.batchInsert(
    list: Iterable<LocalizationLanguage>,
    ignoreErrors: Boolean = false,
    shouldReturnGeneratedValues: Boolean = false,
  ): List<ResultRow> {
    val rows = batchInsert(list, ignoreErrors, shouldReturnGeneratedValues) {
      entry -> assign(this, entry)
    }
    return rows
  }

  public fun LocalizationLanguageTable.update(
    raw: LocalizationLanguage,
    selective: Array<out Column<*>>? = null,
    ignore: Array<out Column<*>>? = null,
    limit: Int? = null,
    `where`: SqlExpressionBuilder.() -> Op<Boolean>,
  ): Int = update(`where`, limit) {
    val ignoreColumns = ignore ?: arrayOf()
    assign(it, raw, selective = selective, *ignoreColumns)
  }

  public fun LocalizationLanguageTable.updateByPrimaryKey(raw: LocalizationLanguage, vararg
      selective: Column<*>): Int = update(raw, selective = selective, ignore = arrayOf(locale)) {
    LocalizationLanguageTable.locale.eq(raw.locale)
  }

  public fun LocalizationLanguageTable.updateByPrimaryKey(locale: String,
      builder: LocalizationLanguageTable.(UpdateStatement) -> Unit): Int = update({
      LocalizationLanguageTable.locale.eq(locale) }, body = builder)

  public fun LocalizationLanguageTable.deleteByPrimaryKey(locale: String): Int = deleteWhere {
    LocalizationLanguageTable.locale.eq(locale)
  }

  public fun LocalizationLanguageTable.selectByPrimaryKey(locale: String, vararg
      selective: Column<*>): LocalizationLanguage? {
    val query = selectSlice(*selective).andWhere {
      LocalizationLanguageTable.locale.eq(locale)
    }
    return query.firstOrNull()?.toLocalizationLanguage(*selective)
  }

  public fun LocalizationLanguageTable.selectByPrimaryKeys(ids: Iterable<String>, vararg
      selective: Column<*>): List<LocalizationLanguage> {
    val query = selectSlice(*selective).andWhere {
      LocalizationLanguageTable.locale inList ids
    }
    return query.toLocalizationLanguageList(*selective)
  }

  public fun LocalizationLanguageTable.selectMany(vararg selective: Column<*>,
      `where`: Query.() -> Unit): List<LocalizationLanguage> {
    val query = selectSlice(*selective)
    `where`.invoke(query)
    return query.toLocalizationLanguageList(*selective)
  }

  public fun LocalizationLanguageTable.selectOne(vararg selective: Column<*>,
      `where`: Query.() -> Unit): LocalizationLanguage? {
    val query = selectSlice(*selective)
    `where`.invoke(query)
    return query.firstOrNull()?.toLocalizationLanguage(*selective)
  }

  public fun LocalizationLanguageTable.selectForwardByPrimaryKey(
    forwardToken: String? = null,
    order: SortOrder = SortOrder.DESC,
    pageSize: Int = 50,
    selective: Collection<Column<*>> = listOf(),
    `where`: (Query.() -> Unit)? = null,
  ): OffsetList<LocalizationLanguage> {
    if(pageSize < 1) {
      return OffsetList.empty()
    }
    val offsetKey = forwardToken?.let { Base64.getUrlDecoder().decode(it).toString(Charsets.UTF_8) }
    val query = selectSlice(*selective.toTypedArray())
    offsetKey?.let {
      when(order) {
        SortOrder.DESC, SortOrder.DESC_NULLS_FIRST, SortOrder.DESC_NULLS_LAST->
        query.andWhere { locale less it }
        else-> query.andWhere { locale greater it }
      }
    }
    `where`?.invoke(query)
    val sorted = query.orderBy(locale, order)
    val list = sorted.limit(pageSize).toLocalizationLanguageList(*selective.toTypedArray())
    val token = if(list.size >= pageSize) {
      val lastId = list.last().locale.toString().toByteArray(Charsets.UTF_8)
      Base64.getUrlEncoder().encodeToString(lastId)
    }
    else {
      null
    }
    return OffsetList(list, token)
  }

  public fun <T : Comparable<T>> LocalizationLanguageTable.selectForward(
    sortColumn: Column<T>,
    forwardToken: String? = null,
    order: SortOrder = SortOrder.DESC,
    pageSize: Int = 50,
    selective: Collection<Column<*>> = listOf(),
    `where`: (Query.() -> Unit)? = null,
  ): OffsetList<LocalizationLanguage> {
    if(pageSize < 1) {
      return OffsetList.empty()
    }
    if(sortColumn == locale) {
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
        query.andWhere { locale notInList it }
      }
    }
    `where`?.invoke(query)
    val sorted = query.orderBy(Pair(sortColumn, order), Pair(locale, order))
    val list = sorted.limit(pageSize).toLocalizationLanguageList(*selective.toTypedArray())
    val token = if(list.size < pageSize) null else encodeToken(list, { getColumnValue(sortColumn) },
        LocalizationLanguage::locale)
    return OffsetList(list, token)
  }
}
