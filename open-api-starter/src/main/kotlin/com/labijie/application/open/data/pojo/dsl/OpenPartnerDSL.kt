@file:Suppress("RedundantVisibilityModifier")

package com.labijie.application.`open`.`data`.pojo.dsl

import com.labijie.application.`open`.`data`.OpenPartnerTable
import com.labijie.application.`open`.`data`.OpenPartnerTable.appCount
import com.labijie.application.`open`.`data`.OpenPartnerTable.contact
import com.labijie.application.`open`.`data`.OpenPartnerTable.email
import com.labijie.application.`open`.`data`.OpenPartnerTable.id
import com.labijie.application.`open`.`data`.OpenPartnerTable.name
import com.labijie.application.`open`.`data`.OpenPartnerTable.phoneNumber
import com.labijie.application.`open`.`data`.OpenPartnerTable.status
import com.labijie.application.`open`.`data`.OpenPartnerTable.timeExpired
import com.labijie.application.`open`.`data`.OpenPartnerTable.timeLatestPaid
import com.labijie.application.`open`.`data`.OpenPartnerTable.timeLatestUpdated
import com.labijie.application.`open`.`data`.pojo.OpenPartner
import com.labijie.application.`open`.model.OpenPartnerStatus
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
import kotlin.Pair
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
import org.jetbrains.exposed.sql.Expression
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
import org.jetbrains.exposed.sql.statements.UpsertStatement
import org.jetbrains.exposed.sql.update
import org.jetbrains.exposed.sql.upsert

/**
 * DSL support for OpenPartnerTable
 *
 * This class made by a code generation tool (https://github.com/hongque-pro/infra-orm).
 *
 * Don't modify these codes !!
 *
 * Origin Exposed Table:
 * @see com.labijie.application.open.data.OpenPartnerTable
 */
@kotlin.Suppress(
  "unused",
  "DuplicatedCode",
  "MemberVisibilityCanBePrivate",
  "RemoveRedundantQualifierName",
)
public object OpenPartnerDSL {
  public val OpenPartnerTable.allColumns: Array<Column<*>> by lazy {
    arrayOf(
    name,
    timeExpired,
    status,
    timeLatestPaid,
    timeLatestUpdated,
    phoneNumber,
    contact,
    email,
    appCount,
    id,
    )
  }

  public fun parseRow(raw: ResultRow): OpenPartner {
    val plain = OpenPartner()
    plain.name = raw[name]
    plain.timeExpired = raw[timeExpired]
    plain.status = raw[status]
    plain.timeLatestPaid = raw[timeLatestPaid]
    plain.timeLatestUpdated = raw[timeLatestUpdated]
    plain.phoneNumber = raw[phoneNumber]
    plain.contact = raw[contact]
    plain.email = raw[email]
    plain.appCount = raw[appCount]
    plain.id = raw[id]
    return plain
  }

  public fun parseRowSelective(row: ResultRow): OpenPartner {
    val plain = OpenPartner()
    if(row.hasValue(name)) {
      plain.name = row[name]
    }
    if(row.hasValue(timeExpired)) {
      plain.timeExpired = row[timeExpired]
    }
    if(row.hasValue(status)) {
      plain.status = row[status]
    }
    if(row.hasValue(timeLatestPaid)) {
      plain.timeLatestPaid = row[timeLatestPaid]
    }
    if(row.hasValue(timeLatestUpdated)) {
      plain.timeLatestUpdated = row[timeLatestUpdated]
    }
    if(row.hasValue(phoneNumber)) {
      plain.phoneNumber = row[phoneNumber]
    }
    if(row.hasValue(contact)) {
      plain.contact = row[contact]
    }
    if(row.hasValue(email)) {
      plain.email = row[email]
    }
    if(row.hasValue(appCount)) {
      plain.appCount = row[appCount]
    }
    if(row.hasValue(id)) {
      plain.id = row[id]
    }
    return plain
  }

  public fun <T> OpenPartnerTable.getColumnType(column: Column<T>): KClass<*> = when(column) {
    name->String::class
    timeExpired->Long::class
    status->OpenPartnerStatus::class
    timeLatestPaid->Long::class
    timeLatestUpdated->Long::class
    phoneNumber->String::class
    contact->String::class
    email->String::class
    appCount->Int::class
    id->Long::class
    else->throw IllegalArgumentException("""Unknown column <${column.name}> for 'OpenPartner'""")
  }

  @kotlin.Suppress("UNCHECKED_CAST")
  public fun <T> OpenPartner.getColumnValue(column: Column<T>): T = when(column) {
    OpenPartnerTable.name->this.name as T
    OpenPartnerTable.timeExpired->this.timeExpired as T
    OpenPartnerTable.status->this.status as T
    OpenPartnerTable.timeLatestPaid->this.timeLatestPaid as T
    OpenPartnerTable.timeLatestUpdated->this.timeLatestUpdated as T
    OpenPartnerTable.phoneNumber->this.phoneNumber as T
    OpenPartnerTable.contact->this.contact as T
    OpenPartnerTable.email->this.email as T
    OpenPartnerTable.appCount->this.appCount as T
    OpenPartnerTable.id->this.id as T
    else->throw IllegalArgumentException("""Unknown column <${column.name}> for 'OpenPartner'""")
  }

  public fun assign(
    builder: UpdateBuilder<*>,
    raw: OpenPartner,
    selective: Array<out Column<*>>? = null,
    vararg ignore: Column<*>,
  ) {
    val list = if(selective.isNullOrEmpty()) null else selective
    if((list == null || list.contains(name)) && !ignore.contains(name))
      builder[name] = raw.name
    if((list == null || list.contains(timeExpired)) && !ignore.contains(timeExpired))
      builder[timeExpired] = raw.timeExpired
    if((list == null || list.contains(status)) && !ignore.contains(status))
      builder[status] = raw.status
    if((list == null || list.contains(timeLatestPaid)) && !ignore.contains(timeLatestPaid))
      builder[timeLatestPaid] = raw.timeLatestPaid
    if((list == null || list.contains(timeLatestUpdated)) && !ignore.contains(timeLatestUpdated))
      builder[timeLatestUpdated] = raw.timeLatestUpdated
    if((list == null || list.contains(phoneNumber)) && !ignore.contains(phoneNumber))
      builder[phoneNumber] = raw.phoneNumber
    if((list == null || list.contains(contact)) && !ignore.contains(contact))
      builder[contact] = raw.contact
    if((list == null || list.contains(email)) && !ignore.contains(email))
      builder[email] = raw.email
    if((list == null || list.contains(appCount)) && !ignore.contains(appCount))
      builder[appCount] = raw.appCount
    if((list == null || list.contains(id)) && !ignore.contains(id))
      builder[id] = raw.id
  }

  public fun ResultRow.toOpenPartner(vararg selective: Column<*>): OpenPartner {
    if(selective.isNotEmpty()) {
      return parseRowSelective(this)
    }
    return parseRow(this)
  }

  public fun Iterable<ResultRow>.toOpenPartnerList(vararg selective: Column<*>): List<OpenPartner> =
      this.map {
    it.toOpenPartner(*selective)
  }

  public fun OpenPartnerTable.selectSlice(vararg selective: Column<*>): Query {
    val query = if(selective.isNotEmpty()) {
      select(selective.toList())
    }
    else {
      selectAll()
    }
    return query
  }

  public fun UpdateBuilder<*>.setValue(raw: OpenPartner, vararg ignore: Column<*>): Unit =
      assign(this, raw, ignore = ignore)

  public fun UpdateBuilder<*>.setValueSelective(raw: OpenPartner, vararg selective: Column<*>): Unit
      = assign(this, raw, selective = selective)

  public fun OpenPartnerTable.insert(raw: OpenPartner): InsertStatement<Number> = insert {
    assign(it, raw)
  }

  public fun OpenPartnerTable.upsert(
    raw: OpenPartner,
    onUpdate: List<Pair<Column<*>, Expression<*>>>? = null,
    onUpdateExclude: List<Column<*>>? = null,
    `where`: (SqlExpressionBuilder.() -> Op<Boolean>)? = null,
  ): UpsertStatement<Long> = upsert(where = where, onUpdate = onUpdate, onUpdateExclude =
      onUpdateExclude) {
    assign(it, raw)
  }

  public fun OpenPartnerTable.batchInsert(
    list: Iterable<OpenPartner>,
    ignoreErrors: Boolean = false,
    shouldReturnGeneratedValues: Boolean = false,
  ): List<ResultRow> {
    val rows = batchInsert(list, ignoreErrors, shouldReturnGeneratedValues) {
      entry -> assign(this, entry)
    }
    return rows
  }

  public fun OpenPartnerTable.update(
    raw: OpenPartner,
    selective: Array<out Column<*>>? = null,
    ignore: Array<out Column<*>>? = null,
    limit: Int? = null,
    `where`: SqlExpressionBuilder.() -> Op<Boolean>,
  ): Int = update(`where`, limit) {
    val ignoreColumns = ignore ?: arrayOf()
    assign(it, raw, selective = selective, *ignoreColumns)
  }

  public fun OpenPartnerTable.updateByPrimaryKey(raw: OpenPartner, vararg selective: Column<*>): Int
      = update(raw, selective = selective, ignore = arrayOf(id)) {
    OpenPartnerTable.id.eq(raw.id)
  }

  public fun OpenPartnerTable.updateByPrimaryKey(id: Long,
      builder: OpenPartnerTable.(UpdateStatement) -> Unit): Int = update({
      OpenPartnerTable.id.eq(id) }, body = builder)

  public fun OpenPartnerTable.deleteByPrimaryKey(id: Long): Int = deleteWhere {
    OpenPartnerTable.id.eq(id)
  }

  public fun OpenPartnerTable.selectByPrimaryKey(id: Long, vararg selective: Column<*>):
      OpenPartner? {
    val query = selectSlice(*selective).andWhere {
      OpenPartnerTable.id.eq(id)
    }
    return query.firstOrNull()?.toOpenPartner(*selective)
  }

  public fun OpenPartnerTable.selectByPrimaryKeys(ids: Iterable<Long>, vararg selective: Column<*>):
      List<OpenPartner> {
    val query = selectSlice(*selective).andWhere {
      OpenPartnerTable.id inList ids
    }
    return query.toOpenPartnerList(*selective)
  }

  public fun OpenPartnerTable.selectMany(vararg selective: Column<*>, `where`: Query.() -> Unit):
      List<OpenPartner> {
    val query = selectSlice(*selective)
    `where`.invoke(query)
    return query.toOpenPartnerList(*selective)
  }

  public fun OpenPartnerTable.selectOne(vararg selective: Column<*>, `where`: Query.() -> Unit):
      OpenPartner? {
    val query = selectSlice(*selective)
    `where`.invoke(query)
    return query.firstOrNull()?.toOpenPartner(*selective)
  }

  public fun OpenPartnerTable.selectForwardByPrimaryKey(
    forwardToken: String? = null,
    order: SortOrder = SortOrder.DESC,
    pageSize: Int = 50,
    selective: Collection<Column<*>> = listOf(),
    `where`: (Query.() -> Unit)? = null,
  ): OffsetList<OpenPartner> {
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
    val list = sorted.limit(pageSize).toOpenPartnerList(*selective.toTypedArray())
    val token = if(list.size >= pageSize) {
      val lastId = list.last().id.toString().toByteArray(Charsets.UTF_8)
      Base64.getUrlEncoder().encodeToString(lastId)
    }
    else {
      null
    }
    return OffsetList(list, token)
  }

  public fun <T : Comparable<T>> OpenPartnerTable.selectForward(
    sortColumn: Column<T>,
    forwardToken: String? = null,
    order: SortOrder = SortOrder.DESC,
    pageSize: Int = 50,
    selective: Collection<Column<*>> = listOf(),
    `where`: (Query.() -> Unit)? = null,
  ): OffsetList<OpenPartner> {
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
    val list = sorted.limit(pageSize).toOpenPartnerList(*selective.toTypedArray())
    val token = if(list.size < pageSize) null else encodeToken(list, { getColumnValue(sortColumn) },
        OpenPartner::id)
    return OffsetList(list, token)
  }

  public fun OpenPartnerTable.replace(raw: OpenPartner): ReplaceStatement<Long> = replace {
    assign(it, raw)
  }
}
