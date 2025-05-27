@file:Suppress("RedundantVisibilityModifier")

package com.labijie.application.`data`.pojo.dsl

import com.labijie.application.`data`.TempFileIndexTable
import com.labijie.application.`data`.TempFileIndexTable.expirationSeconds
import com.labijie.application.`data`.TempFileIndexTable.fileAccess
import com.labijie.application.`data`.TempFileIndexTable.id
import com.labijie.application.`data`.TempFileIndexTable.path
import com.labijie.application.`data`.TempFileIndexTable.sizeIntBytes
import com.labijie.application.`data`.TempFileIndexTable.timeCreated
import com.labijie.application.`data`.TempFileIndexTable.timeExpired
import com.labijie.application.`data`.pojo.TempFileIndex
import com.labijie.application.model.FileModifier
import com.labijie.infra.orm.OffsetList
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
import org.jetbrains.exposed.sql.batchUpsert
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.insertIgnore
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
 * DSL support for TempFileIndexTable
 *
 * This class made by a code generation tool (https://github.com/hongque-pro/infra-orm).
 *
 * Don't modify these codes !!
 *
 * Origin Exposed Table:
 * @see com.labijie.application.data.TempFileIndexTable
 */
@kotlin.Suppress(
  "unused",
  "DuplicatedCode",
  "MemberVisibilityCanBePrivate",
  "RemoveRedundantQualifierName",
)
public object TempFileIndexDSL {
  public val TempFileIndexTable.allColumns: Array<Column<*>> by lazy {
    arrayOf(
    path,
    timeCreated,
    sizeIntBytes,
    fileAccess,
    timeExpired,
    expirationSeconds,
    id,
    )
  }

  public fun parseRow(raw: ResultRow): TempFileIndex {
    val plain = TempFileIndex()
    plain.path = raw[path]
    plain.timeCreated = raw[timeCreated]
    plain.sizeIntBytes = raw[sizeIntBytes]
    plain.fileAccess = raw[fileAccess]
    plain.timeExpired = raw[timeExpired]
    plain.expirationSeconds = raw[expirationSeconds]
    plain.id = raw[id]
    return plain
  }

  public fun parseRowSelective(row: ResultRow): TempFileIndex {
    val plain = TempFileIndex()
    if(row.hasValue(path)) {
      plain.path = row[path]
    }
    if(row.hasValue(timeCreated)) {
      plain.timeCreated = row[timeCreated]
    }
    if(row.hasValue(sizeIntBytes)) {
      plain.sizeIntBytes = row[sizeIntBytes]
    }
    if(row.hasValue(fileAccess)) {
      plain.fileAccess = row[fileAccess]
    }
    if(row.hasValue(timeExpired)) {
      plain.timeExpired = row[timeExpired]
    }
    if(row.hasValue(expirationSeconds)) {
      plain.expirationSeconds = row[expirationSeconds]
    }
    if(row.hasValue(id)) {
      plain.id = row[id]
    }
    return plain
  }

  public fun <T> TempFileIndexTable.getColumnType(column: Column<T>): KClass<*> = when(column) {
    path->String::class
    timeCreated->Long::class
    sizeIntBytes->Long::class
    fileAccess->FileModifier::class
    timeExpired->Long::class
    expirationSeconds->Long::class
    id->Long::class
    else->throw IllegalArgumentException("""Unknown column <${column.name}> for 'TempFileIndex'""")
  }

  private fun <T> TempFileIndex.getColumnValueString(column: Column<T>): String = when(column) {
    TempFileIndexTable.path->this.path
    TempFileIndexTable.timeCreated->this.timeCreated.toString()

    TempFileIndexTable.sizeIntBytes->this.sizeIntBytes.toString()

    TempFileIndexTable.timeExpired->this.timeExpired.toString()

    TempFileIndexTable.expirationSeconds->this.expirationSeconds.toString()

    TempFileIndexTable.id->this.id.toString()

    else->throw
        IllegalArgumentException("""Can ot converter value of TempFileIndex::${column.name} to string.""")
  }

  @kotlin.Suppress("UNCHECKED_CAST")
  private fun <T> parseColumnValue(valueString: String, column: Column<T>): T {
    val value = when(column) {
      TempFileIndexTable.path -> valueString
      TempFileIndexTable.timeCreated ->valueString.toLong()
      TempFileIndexTable.sizeIntBytes ->valueString.toLong()
      TempFileIndexTable.timeExpired ->valueString.toLong()
      TempFileIndexTable.expirationSeconds ->valueString.toLong()
      TempFileIndexTable.id ->valueString.toLong()
      else->throw
          IllegalArgumentException("""Can ot converter value of TempFileIndex::${column.name} to string.""")
    }
    return value as T
  }

  @kotlin.Suppress("UNCHECKED_CAST")
  public fun <T> TempFileIndex.getColumnValue(column: Column<T>): T = when(column) {
    TempFileIndexTable.path->this.path as T
    TempFileIndexTable.timeCreated->this.timeCreated as T
    TempFileIndexTable.sizeIntBytes->this.sizeIntBytes as T
    TempFileIndexTable.fileAccess->this.fileAccess as T
    TempFileIndexTable.timeExpired->this.timeExpired as T
    TempFileIndexTable.expirationSeconds->this.expirationSeconds as T
    TempFileIndexTable.id->this.id as T
    else->throw IllegalArgumentException("""Unknown column <${column.name}> for 'TempFileIndex'""")
  }

  public fun assign(
    builder: UpdateBuilder<*>,
    raw: TempFileIndex,
    selective: Array<out Column<*>>? = null,
    vararg ignore: Column<*>,
  ) {
    val list = if(selective.isNullOrEmpty()) null else selective
    if((list == null || list.contains(path)) && !ignore.contains(path))
      builder[path] = raw.path
    if((list == null || list.contains(timeCreated)) && !ignore.contains(timeCreated))
      builder[timeCreated] = raw.timeCreated
    if((list == null || list.contains(sizeIntBytes)) && !ignore.contains(sizeIntBytes))
      builder[sizeIntBytes] = raw.sizeIntBytes
    if((list == null || list.contains(fileAccess)) && !ignore.contains(fileAccess))
      builder[fileAccess] = raw.fileAccess
    if((list == null || list.contains(timeExpired)) && !ignore.contains(timeExpired))
      builder[timeExpired] = raw.timeExpired
    if((list == null || list.contains(expirationSeconds)) && !ignore.contains(expirationSeconds))
      builder[expirationSeconds] = raw.expirationSeconds
    if((list == null || list.contains(id)) && !ignore.contains(id))
      builder[id] = raw.id
  }

  public fun ResultRow.toTempFileIndex(vararg selective: Column<*>): TempFileIndex {
    if(selective.isNotEmpty()) {
      return parseRowSelective(this)
    }
    return parseRow(this)
  }

  public fun Iterable<ResultRow>.toTempFileIndexList(vararg selective: Column<*>):
      List<TempFileIndex> = this.map {
    it.toTempFileIndex(*selective)
  }

  public fun TempFileIndexTable.selectSlice(vararg selective: Column<*>): Query {
    val query = if(selective.isNotEmpty()) {
      select(selective.toList())
    }
    else {
      selectAll()
    }
    return query
  }

  public fun UpdateBuilder<*>.setValue(raw: TempFileIndex, vararg ignore: Column<*>): Unit =
      assign(this, raw, ignore = ignore)

  public fun UpdateBuilder<*>.setValueSelective(raw: TempFileIndex, vararg selective: Column<*>):
      Unit = assign(this, raw, selective = selective)

  public fun TempFileIndexTable.insert(raw: TempFileIndex): InsertStatement<Number> = insert {
    assign(it, raw)
  }

  public fun TempFileIndexTable.insertIgnore(raw: TempFileIndex): InsertStatement<Long> =
      insertIgnore {
    assign(it, raw)
  }

  public fun TempFileIndexTable.upsert(
    raw: TempFileIndex,
    onUpdateExclude: List<Column<*>>? = null,
    onUpdate: (UpsertBuilder.(UpdateStatement) -> Unit)? = null,
    `where`: (SqlExpressionBuilder.() -> Op<Boolean>)? = null,
  ): UpsertStatement<Long> = upsert(where = where, onUpdate = onUpdate, onUpdateExclude =
      onUpdateExclude) {
    assign(it, raw)
  }

  public fun TempFileIndexTable.batchInsert(
    list: Iterable<TempFileIndex>,
    ignoreErrors: Boolean = false,
    shouldReturnGeneratedValues: Boolean = false,
  ): List<ResultRow> {
    val rows = batchInsert(list, ignoreErrors, shouldReturnGeneratedValues) {
      entry -> assign(this, entry)
    }
    return rows
  }

  public fun TempFileIndexTable.batchUpsert(
    list: Iterable<TempFileIndex>,
    onUpdateExclude: List<Column<*>>? = null,
    onUpdate: (UpsertBuilder.(UpdateStatement) -> Unit)? = null,
    shouldReturnGeneratedValues: Boolean = false,
    `where`: (SqlExpressionBuilder.() -> Op<Boolean>)? = null,
  ): List<ResultRow> {
    val rows =  batchUpsert(data = list, keys = arrayOf(id), onUpdate = onUpdate, onUpdateExclude =
        onUpdateExclude, where = where, shouldReturnGeneratedValues = shouldReturnGeneratedValues) {
      data: TempFileIndex-> assign(this, data)
    }
    return rows
  }

  public fun TempFileIndexTable.update(
    raw: TempFileIndex,
    selective: Array<out Column<*>>? = null,
    ignore: Array<out Column<*>>? = null,
    limit: Int? = null,
    `where`: SqlExpressionBuilder.() -> Op<Boolean>,
  ): Int = update(`where`, limit) {
    val ignoreColumns = ignore ?: arrayOf()
    assign(it, raw, selective = selective, *ignoreColumns)
  }

  public fun TempFileIndexTable.updateByPrimaryKey(raw: TempFileIndex, vararg selective: Column<*>):
      Int = update(raw, selective = selective, ignore = arrayOf(id)) {
    TempFileIndexTable.id.eq(raw.id)
  }

  public fun TempFileIndexTable.updateByPrimaryKey(id: Long,
      builder: TempFileIndexTable.(UpdateStatement) -> Unit): Int = update({
      TempFileIndexTable.id.eq(id) }, body = builder)

  public fun TempFileIndexTable.deleteByPrimaryKey(id: Long): Int = deleteWhere {
    TempFileIndexTable.id.eq(id)
  }

  public fun TempFileIndexTable.selectByPrimaryKey(id: Long, vararg selective: Column<*>):
      TempFileIndex? {
    val query = selectSlice(*selective).andWhere {
      TempFileIndexTable.id.eq(id)
    }
    return query.firstOrNull()?.toTempFileIndex(*selective)
  }

  public fun TempFileIndexTable.selectByPrimaryKeys(ids: Iterable<Long>, vararg
      selective: Column<*>): List<TempFileIndex> {
    val query = selectSlice(*selective).andWhere {
      TempFileIndexTable.id inList ids
    }
    return query.toTempFileIndexList(*selective)
  }

  public fun TempFileIndexTable.selectMany(vararg selective: Column<*>,
      `where`: Query.() -> Query?): List<TempFileIndex> {
    val query = selectSlice(*selective)
    `where`.invoke(query)
    return query.toTempFileIndexList(*selective)
  }

  public fun TempFileIndexTable.selectOne(vararg selective: Column<*>, `where`: Query.() -> Query?):
      TempFileIndex? {
    val query = selectSlice(*selective)
    `where`.invoke(query)
    return query.firstOrNull()?.toTempFileIndex(*selective)
  }

  public fun TempFileIndexTable.selectForwardByPrimaryKey(
    forwardToken: String? = null,
    order: SortOrder = SortOrder.DESC,
    pageSize: Int = 50,
    selective: Collection<Column<*>> = listOf(),
    `where`: (Query.() -> Query?)? = null,
  ): OffsetList<TempFileIndex> {
    if(pageSize < 1) {
      return OffsetList.empty()
    }
    val offsetKey = forwardToken?.let { Base64.getUrlDecoder().decode(it).toString(Charsets.UTF_8) }
    val query = selectSlice(*selective.toTypedArray())
    offsetKey?.let {
      val keyValue = parseColumnValue(it, id)
      when(order) {
        SortOrder.DESC, SortOrder.DESC_NULLS_FIRST, SortOrder.DESC_NULLS_LAST->
        query.andWhere { id less keyValue }
        else-> query.andWhere { id greater keyValue }
      }
    }
    `where`?.invoke(query)
    val sorted = query.orderBy(id, order)
    val list = sorted.limit(pageSize +
        1).toTempFileIndexList(*selective.toTypedArray()).toMutableList()
    val dataCount = list.size
    val token = if(dataCount > pageSize) {
      list.removeLast()
      val idString = list.last().getColumnValueString(id)
      val idArray = idString.toByteArray(Charsets.UTF_8)
      Base64.getUrlEncoder().encodeToString(idArray)
    }
    else {
      null
    }
    return OffsetList(list, token)
  }

  public fun <T : Comparable<T>> TempFileIndexTable.selectForward(
    sortColumn: Column<T>,
    forwardToken: String? = null,
    order: SortOrder = SortOrder.DESC,
    pageSize: Int = 50,
    selective: Collection<Column<*>> = listOf(),
    `where`: (Query.() -> Query?)? = null,
  ): OffsetList<TempFileIndex> {
    if(pageSize < 1) {
      return OffsetList.empty()
    }
    if(sortColumn == id) {
      return this.selectForwardByPrimaryKey(forwardToken, order, pageSize, selective, `where`)
    }
    val sortColAndId = forwardToken?.let { if(it.isNotBlank())
        Base64.getUrlDecoder().decode(it).toString(Charsets.UTF_8) else null }
    val kp = sortColAndId?.split(":::")
    val offsetKey = if(!kp.isNullOrEmpty()) parseColumnValue(kp.first(), sortColumn) else null
    val lastId = if(kp != null && kp.size > 1 && kp[1].isNotBlank()) parseColumnValue(kp[1], id)
        else null
    val query = selectSlice(*selective.toTypedArray())
    offsetKey?.let {
      when(order) {
        SortOrder.DESC, SortOrder.DESC_NULLS_FIRST, SortOrder.DESC_NULLS_LAST->
        query.andWhere { sortColumn lessEq it }
        else-> query.andWhere { sortColumn greaterEq it }
      }
    }
    lastId?.let {
      when(order) {
        SortOrder.DESC, SortOrder.DESC_NULLS_FIRST, SortOrder.DESC_NULLS_LAST->
        query.andWhere { id less it }
        else-> query.andWhere { id greater it }
      }
    }
    `where`?.invoke(query)
    val sorted = query.orderBy(Pair(sortColumn, order), Pair(id, order))
    val list = sorted.limit(pageSize +
        1).toTempFileIndexList(*selective.toTypedArray()).toMutableList()
    val dataCount = list.size
    val token = if(dataCount > pageSize) {
      list.removeLast()
      val idToEncode = list.last().getColumnValueString(id)
      val sortKey = list.last().getColumnValueString(sortColumn)
      val tokenValue = """${idToEncode}:::${sortKey}""".toByteArray(Charsets.UTF_8)
      Base64.getUrlEncoder().encodeToString(tokenValue)
    }
    else null
    return OffsetList(list, token)
  }

  public fun TempFileIndexTable.replace(raw: TempFileIndex): ReplaceStatement<Long> = replace {
    assign(it, raw)
  }
}
