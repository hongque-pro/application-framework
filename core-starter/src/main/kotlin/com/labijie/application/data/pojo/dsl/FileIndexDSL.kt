@file:Suppress("RedundantVisibilityModifier")

package com.labijie.application.`data`.pojo.dsl

import com.labijie.application.`data`.FileIndexTable
import com.labijie.application.`data`.FileIndexTable.entityId
import com.labijie.application.`data`.FileIndexTable.fileAccess
import com.labijie.application.`data`.FileIndexTable.fileType
import com.labijie.application.`data`.FileIndexTable.id
import com.labijie.application.`data`.FileIndexTable.path
import com.labijie.application.`data`.FileIndexTable.timeCreated
import com.labijie.application.`data`.pojo.FileIndex
import com.labijie.application.model.FileModifier
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
 * DSL support for FileIndexTable
 *
 * This class made by a code generation tool (https://github.com/hongque-pro/infra-orm).
 *
 * Don't modify these codes !!
 *
 * Origin Exposed Table:
 * @see com.labijie.application.data.FileIndexTable
 */
@kotlin.Suppress(
  "unused",
  "DuplicatedCode",
  "MemberVisibilityCanBePrivate",
  "RemoveRedundantQualifierName",
)
public object FileIndexDSL {
  public val FileIndexTable.allColumns: Array<Column<*>> by lazy {
    arrayOf(
    path,
    timeCreated,
    fileType,
    entityId,
    fileAccess,
    id,
    )
  }


  public fun parseRow(raw: ResultRow): FileIndex {
    val plain = FileIndex()
    plain.path = raw[path]
    plain.timeCreated = raw[timeCreated]
    plain.fileType = raw[fileType]
    plain.entityId = raw[entityId]
    plain.fileAccess = raw[fileAccess]
    plain.id = raw[id]
    return plain
  }

  public fun parseRowSelective(row: ResultRow): FileIndex {
    val plain = FileIndex()
    if(row.hasValue(path)) {
      plain.path = row[path]
    }
    if(row.hasValue(timeCreated)) {
      plain.timeCreated = row[timeCreated]
    }
    if(row.hasValue(fileType)) {
      plain.fileType = row[fileType]
    }
    if(row.hasValue(entityId)) {
      plain.entityId = row[entityId]
    }
    if(row.hasValue(fileAccess)) {
      plain.fileAccess = row[fileAccess]
    }
    if(row.hasValue(id)) {
      plain.id = row[id]
    }
    return plain
  }

  public fun <T> FileIndexTable.getColumnType(column: Column<T>): KClass<*> = when(column) {
    path->String::class
    timeCreated->Long::class
    fileType->String::class
    entityId->Long::class
    fileAccess->FileModifier::class
    id->Long::class
    else->throw IllegalArgumentException("""Unknown column <${column.name}> for 'FileIndex'""")
  }

  @kotlin.Suppress("UNCHECKED_CAST")
  public fun <T> FileIndex.getColumnValue(column: Column<T>): T = when(column) {
    FileIndexTable.path->this.path as T
    FileIndexTable.timeCreated->this.timeCreated as T
    FileIndexTable.fileType->this.fileType as T
    FileIndexTable.entityId->this.entityId as T
    FileIndexTable.fileAccess->this.fileAccess as T
    FileIndexTable.id->this.id as T
    else->throw IllegalArgumentException("""Unknown column <${column.name}> for 'FileIndex'""")
  }

  public fun assign(
    builder: UpdateBuilder<*>,
    raw: FileIndex,
    selective: Array<out Column<*>>? = null,
    vararg ignore: Column<*>,
  ) {
    val list = if(selective.isNullOrEmpty()) null else selective
    if((list == null || list.contains(path)) && !ignore.contains(path))
      builder[path] = raw.path
    if((list == null || list.contains(timeCreated)) && !ignore.contains(timeCreated))
      builder[timeCreated] = raw.timeCreated
    if((list == null || list.contains(fileType)) && !ignore.contains(fileType))
      builder[fileType] = raw.fileType
    if((list == null || list.contains(entityId)) && !ignore.contains(entityId))
      builder[entityId] = raw.entityId
    if((list == null || list.contains(fileAccess)) && !ignore.contains(fileAccess))
      builder[fileAccess] = raw.fileAccess
    if((list == null || list.contains(id)) && !ignore.contains(id))
      builder[id] = raw.id
  }

  public fun ResultRow.toFileIndex(vararg selective: Column<*>): FileIndex {
    if(selective.isNotEmpty()) {
      return parseRowSelective(this)
    }
    return parseRow(this)
  }

  public fun Iterable<ResultRow>.toFileIndexList(vararg selective: Column<*>): List<FileIndex> =
      this.map {
    it.toFileIndex(*selective)
  }

  public fun FileIndexTable.selectSlice(vararg selective: Column<*>): Query {
    val query = if(selective.isNotEmpty()) {
      slice(selective.toList()).selectAll()
    }
    else {
      selectAll()
    }
    return query
  }

  public fun UpdateBuilder<*>.setValue(raw: FileIndex, vararg ignore: Column<*>): Unit =
      assign(this, raw, ignore = ignore)

  public fun UpdateBuilder<*>.setValueSelective(raw: FileIndex, vararg selective: Column<*>): Unit =
      assign(this, raw, selective = selective)

  public fun FileIndexTable.insert(raw: FileIndex): InsertStatement<Number> = insert {
    assign(it, raw)
  }

  public fun FileIndexTable.batchInsert(
    list: Iterable<FileIndex>,
    ignoreErrors: Boolean = false,
    shouldReturnGeneratedValues: Boolean = false,
  ): List<ResultRow> {
    val rows = batchInsert(list, ignoreErrors, shouldReturnGeneratedValues) {
      entry -> assign(this, entry)
    }
    return rows
  }

  public fun FileIndexTable.update(
    raw: FileIndex,
    selective: Array<out Column<*>>? = null,
    ignore: Array<out Column<*>>? = null,
    limit: Int? = null,
    `where`: SqlExpressionBuilder.() -> Op<Boolean>,
  ): Int = update(`where`, limit) {
    val ignoreColumns = ignore ?: arrayOf()
    assign(it, raw, selective = selective, *ignoreColumns)
  }

  public fun FileIndexTable.updateByPrimaryKey(raw: FileIndex, vararg selective: Column<*>): Int =
      update(raw, selective = selective, ignore = arrayOf(id)) {
    FileIndexTable.id.eq(raw.id)
  }

  public fun FileIndexTable.updateByPrimaryKey(id: Long,
      builder: FileIndexTable.(UpdateStatement) -> Unit): Int = update({ FileIndexTable.id.eq(id) },
      body = builder)

  public fun FileIndexTable.deleteByPrimaryKey(id: Long): Int = deleteWhere {
    FileIndexTable.id.eq(id)
  }

  public fun FileIndexTable.selectByPrimaryKey(id: Long, vararg selective: Column<*>): FileIndex? {
    val query = selectSlice(*selective).andWhere {
      FileIndexTable.id.eq(id)
    }
    return query.firstOrNull()?.toFileIndex(*selective)
  }

  public fun FileIndexTable.selectByPrimaryKeys(ids: Iterable<Long>, vararg selective: Column<*>):
      List<FileIndex> {
    val query = selectSlice(*selective).andWhere {
      FileIndexTable.id inList ids
    }
    return query.toFileIndexList(*selective)
  }

  public fun FileIndexTable.selectMany(vararg selective: Column<*>, `where`: Query.() -> Unit):
      List<FileIndex> {
    val query = selectSlice(*selective)
    `where`.invoke(query)
    return query.toFileIndexList(*selective)
  }

  public fun FileIndexTable.selectOne(vararg selective: Column<*>, `where`: Query.() -> Unit):
      FileIndex? {
    val query = selectSlice(*selective)
    `where`.invoke(query)
    return query.firstOrNull()?.toFileIndex(*selective)
  }

  public fun FileIndexTable.selectForwardByPrimaryKey(
    forwardToken: String? = null,
    order: SortOrder = SortOrder.DESC,
    pageSize: Int = 50,
    selective: Collection<Column<*>> = listOf(),
    `where`: (Query.() -> Unit)? = null,
  ): OffsetList<FileIndex> {
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
    val list = sorted.limit(pageSize).toFileIndexList(*selective.toTypedArray())
    val token = if(list.size >= pageSize) {
      val lastId = list.last().id.toString().toByteArray(Charsets.UTF_8)
      Base64.getUrlEncoder().encodeToString(lastId)
    }
    else {
      null
    }
    return OffsetList(list, token)
  }

  public fun <T : Comparable<T>> FileIndexTable.selectForward(
    sortColumn: Column<T>,
    forwardToken: String? = null,
    order: SortOrder = SortOrder.DESC,
    pageSize: Int = 50,
    selective: Collection<Column<*>> = listOf(),
    `where`: (Query.() -> Unit)? = null,
  ): OffsetList<FileIndex> {
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
    val list = sorted.limit(pageSize).toFileIndexList(*selective.toTypedArray())
    val token = if(list.size < pageSize) null else encodeToken(list, { getColumnValue(sortColumn) },
        FileIndex::id)
    return OffsetList(list, token)
  }
}
