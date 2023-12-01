@file:Suppress("RedundantVisibilityModifier")

package com.labijie.application.`open`.`data`.pojo.dsl

import com.labijie.application.`open`.`data`.OpenAppTable
import com.labijie.application.`open`.`data`.OpenAppTable.appSecret
import com.labijie.application.`open`.`data`.OpenAppTable.appType
import com.labijie.application.`open`.`data`.OpenAppTable.concurrencyStamp
import com.labijie.application.`open`.`data`.OpenAppTable.configuration
import com.labijie.application.`open`.`data`.OpenAppTable.displayName
import com.labijie.application.`open`.`data`.OpenAppTable.id
import com.labijie.application.`open`.`data`.OpenAppTable.jsApiDomain
import com.labijie.application.`open`.`data`.OpenAppTable.jsApiKey
import com.labijie.application.`open`.`data`.OpenAppTable.logoUrl
import com.labijie.application.`open`.`data`.OpenAppTable.partnerId
import com.labijie.application.`open`.`data`.OpenAppTable.signAlgorithm
import com.labijie.application.`open`.`data`.OpenAppTable.status
import com.labijie.application.`open`.`data`.OpenAppTable.timeConfigUpdated
import com.labijie.application.`open`.`data`.OpenAppTable.timeCreated
import com.labijie.application.`open`.`data`.pojo.OpenApp
import com.labijie.application.`open`.model.OpenAppStatus
import com.labijie.infra.orm.OffsetList
import com.labijie.infra.orm.OffsetList.Companion.decodeToken
import com.labijie.infra.orm.OffsetList.Companion.encodeToken
import java.lang.IllegalArgumentException
import java.util.Base64
import kotlin.Array
import kotlin.Boolean
import kotlin.Byte
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
import kotlin.collections.map
import kotlin.collections.toList
import kotlin.reflect.KClass
import kotlin.text.Charsets
import kotlin.text.toByteArray
import kotlin.text.toLong
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
 * DSL support for OpenAppTable
 *
 * This class made by a code generation tool (https://github.com/hongque-pro/infra-orm).
 *
 * Don't modify these codes !!
 *
 * Origin Exposed Table:
 * @see com.labijie.application.open.data.OpenAppTable
 */
@kotlin.Suppress(
  "unused",
  "DuplicatedCode",
  "MemberVisibilityCanBePrivate",
  "RemoveRedundantQualifierName",
)
public object OpenAppDSL {
  public val OpenAppTable.allColumns: Array<Column<*>> by lazy {
    arrayOf(
    displayName,
    appSecret,
    appType,
    signAlgorithm,
    jsApiKey,
    jsApiDomain,
    logoUrl,
    status,
    partnerId,
    timeCreated,
    timeConfigUpdated,
    concurrencyStamp,
    configuration,
    id,
    )
  }


  public fun parseRow(raw: ResultRow): OpenApp {
    val plain = OpenApp()
    plain.displayName = raw[displayName]
    plain.appSecret = raw[appSecret]
    plain.appType = raw[appType]
    plain.signAlgorithm = raw[signAlgorithm]
    plain.jsApiKey = raw[jsApiKey]
    plain.jsApiDomain = raw[jsApiDomain]
    plain.logoUrl = raw[logoUrl]
    plain.status = raw[status]
    plain.partnerId = raw[partnerId]
    plain.timeCreated = raw[timeCreated]
    plain.timeConfigUpdated = raw[timeConfigUpdated]
    plain.concurrencyStamp = raw[concurrencyStamp]
    plain.configuration = raw[configuration]
    plain.id = raw[id]
    return plain
  }

  public fun parseRowSelective(row: ResultRow): OpenApp {
    val plain = OpenApp()
    if(row.hasValue(displayName)) {
      plain.displayName = row[displayName]
    }
    if(row.hasValue(appSecret)) {
      plain.appSecret = row[appSecret]
    }
    if(row.hasValue(appType)) {
      plain.appType = row[appType]
    }
    if(row.hasValue(signAlgorithm)) {
      plain.signAlgorithm = row[signAlgorithm]
    }
    if(row.hasValue(jsApiKey)) {
      plain.jsApiKey = row[jsApiKey]
    }
    if(row.hasValue(jsApiDomain)) {
      plain.jsApiDomain = row[jsApiDomain]
    }
    if(row.hasValue(logoUrl)) {
      plain.logoUrl = row[logoUrl]
    }
    if(row.hasValue(status)) {
      plain.status = row[status]
    }
    if(row.hasValue(partnerId)) {
      plain.partnerId = row[partnerId]
    }
    if(row.hasValue(timeCreated)) {
      plain.timeCreated = row[timeCreated]
    }
    if(row.hasValue(timeConfigUpdated)) {
      plain.timeConfigUpdated = row[timeConfigUpdated]
    }
    if(row.hasValue(concurrencyStamp)) {
      plain.concurrencyStamp = row[concurrencyStamp]
    }
    if(row.hasValue(configuration)) {
      plain.configuration = row[configuration]
    }
    if(row.hasValue(id)) {
      plain.id = row[id]
    }
    return plain
  }

  public fun <T> OpenAppTable.getColumnType(column: Column<T>): KClass<*> = when(column) {
    displayName->String::class
    appSecret->String::class
    appType->Byte::class
    signAlgorithm->String::class
    jsApiKey->String::class
    jsApiDomain->String::class
    logoUrl->String::class
    status->OpenAppStatus::class
    partnerId->Long::class
    timeCreated->Long::class
    timeConfigUpdated->Long::class
    concurrencyStamp->String::class
    configuration->String::class
    id->Long::class
    else->throw IllegalArgumentException("""Unknown column <${column.name}> for 'OpenApp'""")
  }

  @kotlin.Suppress("UNCHECKED_CAST")
  public fun <T> OpenApp.getColumnValue(column: Column<T>): T = when(column) {
    OpenAppTable.displayName->this.displayName as T
    OpenAppTable.appSecret->this.appSecret as T
    OpenAppTable.appType->this.appType as T
    OpenAppTable.signAlgorithm->this.signAlgorithm as T
    OpenAppTable.jsApiKey->this.jsApiKey as T
    OpenAppTable.jsApiDomain->this.jsApiDomain as T
    OpenAppTable.logoUrl->this.logoUrl as T
    OpenAppTable.status->this.status as T
    OpenAppTable.partnerId->this.partnerId as T
    OpenAppTable.timeCreated->this.timeCreated as T
    OpenAppTable.timeConfigUpdated->this.timeConfigUpdated as T
    OpenAppTable.concurrencyStamp->this.concurrencyStamp as T
    OpenAppTable.configuration->this.configuration as T
    OpenAppTable.id->this.id as T
    else->throw IllegalArgumentException("""Unknown column <${column.name}> for 'OpenApp'""")
  }

  public fun assign(
    builder: UpdateBuilder<*>,
    raw: OpenApp,
    selective: Array<out Column<*>>? = null,
    vararg ignore: Column<*>,
  ) {
    val list = if(selective.isNullOrEmpty()) null else selective
    if((list == null || list.contains(displayName)) && !ignore.contains(displayName))
      builder[displayName] = raw.displayName
    if((list == null || list.contains(appSecret)) && !ignore.contains(appSecret))
      builder[appSecret] = raw.appSecret
    if((list == null || list.contains(appType)) && !ignore.contains(appType))
      builder[appType] = raw.appType
    if((list == null || list.contains(signAlgorithm)) && !ignore.contains(signAlgorithm))
      builder[signAlgorithm] = raw.signAlgorithm
    if((list == null || list.contains(jsApiKey)) && !ignore.contains(jsApiKey))
      builder[jsApiKey] = raw.jsApiKey
    if((list == null || list.contains(jsApiDomain)) && !ignore.contains(jsApiDomain))
      builder[jsApiDomain] = raw.jsApiDomain
    if((list == null || list.contains(logoUrl)) && !ignore.contains(logoUrl))
      builder[logoUrl] = raw.logoUrl
    if((list == null || list.contains(status)) && !ignore.contains(status))
      builder[status] = raw.status
    if((list == null || list.contains(partnerId)) && !ignore.contains(partnerId))
      builder[partnerId] = raw.partnerId
    if((list == null || list.contains(timeCreated)) && !ignore.contains(timeCreated))
      builder[timeCreated] = raw.timeCreated
    if((list == null || list.contains(timeConfigUpdated)) && !ignore.contains(timeConfigUpdated))
      builder[timeConfigUpdated] = raw.timeConfigUpdated
    if((list == null || list.contains(concurrencyStamp)) && !ignore.contains(concurrencyStamp))
      builder[concurrencyStamp] = raw.concurrencyStamp
    if((list == null || list.contains(configuration)) && !ignore.contains(configuration))
      builder[configuration] = raw.configuration
    if((list == null || list.contains(id)) && !ignore.contains(id))
      builder[id] = raw.id
  }

  public fun ResultRow.toOpenApp(vararg selective: Column<*>): OpenApp {
    if(selective.isNotEmpty()) {
      return parseRowSelective(this)
    }
    return parseRow(this)
  }

  public fun Iterable<ResultRow>.toOpenAppList(vararg selective: Column<*>): List<OpenApp> =
      this.map {
    it.toOpenApp(*selective)
  }

  public fun OpenAppTable.selectSlice(vararg selective: Column<*>): Query {
    val query = if(selective.isNotEmpty()) {
      slice(selective.toList()).selectAll()
    }
    else {
      selectAll()
    }
    return query
  }

  public fun UpdateBuilder<*>.setValue(raw: OpenApp, vararg ignore: Column<*>): Unit = assign(this,
      raw, ignore = ignore)

  public fun UpdateBuilder<*>.setValueSelective(raw: OpenApp, vararg selective: Column<*>): Unit =
      assign(this, raw, selective = selective)

  public fun OpenAppTable.insert(raw: OpenApp): InsertStatement<Number> = insert {
    assign(it, raw)
  }

  public fun OpenAppTable.batchInsert(
    list: Iterable<OpenApp>,
    ignoreErrors: Boolean = false,
    shouldReturnGeneratedValues: Boolean = false,
  ): List<ResultRow> {
    val rows = batchInsert(list, ignoreErrors, shouldReturnGeneratedValues) {
      entry -> assign(this, entry)
    }
    return rows
  }

  public fun OpenAppTable.update(
    raw: OpenApp,
    selective: Array<out Column<*>>? = null,
    ignore: Array<out Column<*>>? = null,
    limit: Int? = null,
    `where`: SqlExpressionBuilder.() -> Op<Boolean>,
  ): Int = update(`where`, limit) {
    val ignoreColumns = ignore ?: arrayOf()
    assign(it, raw, selective = selective, *ignoreColumns)
  }

  public fun OpenAppTable.updateByPrimaryKey(raw: OpenApp, vararg selective: Column<*>): Int =
      update(raw, selective = selective, ignore = arrayOf(id)) {
    OpenAppTable.id.eq(raw.id)
  }

  public fun OpenAppTable.updateByPrimaryKey(id: Long,
      builder: OpenAppTable.(UpdateStatement) -> Unit): Int = update({ OpenAppTable.id.eq(id) },
      body = builder)

  public fun OpenAppTable.deleteByPrimaryKey(id: Long): Int = deleteWhere {
    OpenAppTable.id.eq(id)
  }

  public fun OpenAppTable.selectByPrimaryKey(id: Long, vararg selective: Column<*>): OpenApp? {
    val query = selectSlice(*selective).andWhere {
      OpenAppTable.id.eq(id)
    }
    return query.firstOrNull()?.toOpenApp(*selective)
  }

  public fun OpenAppTable.selectByPrimaryKeys(ids: Iterable<Long>, vararg selective: Column<*>):
      List<OpenApp> {
    val query = selectSlice(*selective).andWhere {
      OpenAppTable.id inList ids
    }
    return query.toOpenAppList(*selective)
  }

  public fun OpenAppTable.selectMany(vararg selective: Column<*>, `where`: Query.() -> Unit):
      List<OpenApp> {
    val query = selectSlice(*selective)
    `where`.invoke(query)
    return query.toOpenAppList(*selective)
  }

  public fun OpenAppTable.selectOne(vararg selective: Column<*>, `where`: Query.() -> Unit):
      OpenApp? {
    val query = selectSlice(*selective)
    `where`.invoke(query)
    return query.firstOrNull()?.toOpenApp(*selective)
  }

  public fun OpenAppTable.selectForwardByPrimaryKey(
    forwardToken: String? = null,
    order: SortOrder = SortOrder.DESC,
    pageSize: Int = 50,
    selective: Collection<Column<*>> = listOf(),
    `where`: (Query.() -> Unit)? = null,
  ): OffsetList<OpenApp> {
    if(pageSize < 1) {
      return OffsetList.empty()
    }
    val offsetKey = forwardToken?.let { Base64.getUrlDecoder().decode(it).toString(Charsets.UTF_8) }
    val query = selectSlice(*selective.toTypedArray())
    offsetKey?.let {
      when(order) {
        SortOrder.DESC, SortOrder.DESC_NULLS_FIRST, SortOrder.DESC_NULLS_LAST->
        query.andWhere { id less it.toLong() }
        else-> query.andWhere { id greater it.toLong() }
      }
    }
    `where`?.invoke(query)
    val sorted = query.orderBy(id, order)
    val list = sorted.limit(pageSize).toOpenAppList(*selective.toTypedArray())
    val token = if(list.size >= pageSize) {
      val lastId = list.last().id.toString().toByteArray(Charsets.UTF_8)
      Base64.getUrlEncoder().encodeToString(lastId)
    }
    else {
      null
    }
    return OffsetList(list, token)
  }

  public fun <T : Comparable<T>> OpenAppTable.selectForward(
    sortColumn: Column<T>,
    forwardToken: String? = null,
    order: SortOrder = SortOrder.DESC,
    pageSize: Int = 50,
    selective: Collection<Column<*>> = listOf(),
    `where`: (Query.() -> Unit)? = null,
  ): OffsetList<OpenApp> {
    if(pageSize < 1) {
      return OffsetList.empty()
    }
    if(sortColumn == id) {
      return this.selectForwardByPrimaryKey(forwardToken, order, pageSize, selective, `where`)
    }
    val kp = forwardToken?.let { decodeToken(it) }
    val offsetKey = kp?.first
    val excludeKeys = kp?.second?.map { it.toLong() }
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
        query.andWhere { id notInList it }
      }
    }
    `where`?.invoke(query)
    val sorted = query.orderBy(Pair(sortColumn, order), Pair(id, order))
    val list = sorted.limit(pageSize).toOpenAppList(*selective.toTypedArray())
    val token = if(list.size < pageSize) null else encodeToken(list, { getColumnValue(sortColumn) },
        OpenApp::id)
    return OffsetList(list, token)
  }
}
