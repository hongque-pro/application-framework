@file:Suppress("RedundantVisibilityModifier")

package com.labijie.application.identity.`data`.pojo.dsl

import com.labijie.application.identity.`data`.RoleTable
import com.labijie.application.identity.`data`.RoleTable.concurrencyStamp
import com.labijie.application.identity.`data`.RoleTable.id
import com.labijie.application.identity.`data`.RoleTable.name
import com.labijie.application.identity.`data`.pojo.Role
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
 * DSL support for RoleTable
 *
 * This class made by a code generation tool (https://github.com/hongque-pro/infra-orm).
 *
 * Don't modify these codes !!
 *
 * Origin Exposed Table:
 * @see com.labijie.application.identity.data.RoleTable
 */
@kotlin.Suppress(
  "unused",
  "DuplicatedCode",
  "MemberVisibilityCanBePrivate",
  "RemoveRedundantQualifierName",
)
public object RoleDSL {
  public val RoleTable.allColumns: Array<Column<*>> by lazy {
    arrayOf(
    concurrencyStamp,
    name,
    id,
    )
  }

  public fun parseRow(raw: ResultRow): Role {
    val plain = Role()
    plain.concurrencyStamp = raw[concurrencyStamp]
    plain.name = raw[name]
    plain.id = raw[id]
    return plain
  }

  public fun parseRowSelective(row: ResultRow): Role {
    val plain = Role()
    if(row.hasValue(concurrencyStamp)) {
      plain.concurrencyStamp = row[concurrencyStamp]
    }
    if(row.hasValue(name)) {
      plain.name = row[name]
    }
    if(row.hasValue(id)) {
      plain.id = row[id]
    }
    return plain
  }

  public fun <T> RoleTable.getColumnType(column: Column<T>): KClass<*> = when(column) {
    concurrencyStamp->String::class
    name->String::class
    id->Long::class
    else->throw IllegalArgumentException("""Unknown column <${column.name}> for 'Role'""")
  }

  @kotlin.Suppress("UNCHECKED_CAST")
  public fun <T> Role.getColumnValue(column: Column<T>): T = when(column) {
    RoleTable.concurrencyStamp->this.concurrencyStamp as T
    RoleTable.name->this.name as T
    RoleTable.id->this.id as T
    else->throw IllegalArgumentException("""Unknown column <${column.name}> for 'Role'""")
  }

  public fun assign(
    builder: UpdateBuilder<*>,
    raw: Role,
    selective: Array<out Column<*>>? = null,
    vararg ignore: Column<*>,
  ) {
    val list = if(selective.isNullOrEmpty()) null else selective
    if((list == null || list.contains(concurrencyStamp)) && !ignore.contains(concurrencyStamp))
      builder[concurrencyStamp] = raw.concurrencyStamp
    if((list == null || list.contains(name)) && !ignore.contains(name))
      builder[name] = raw.name
    if((list == null || list.contains(id)) && !ignore.contains(id))
      builder[id] = raw.id
  }

  public fun ResultRow.toRole(vararg selective: Column<*>): Role {
    if(selective.isNotEmpty()) {
      return parseRowSelective(this)
    }
    return parseRow(this)
  }

  public fun Iterable<ResultRow>.toRoleList(vararg selective: Column<*>): List<Role> = this.map {
    it.toRole(*selective)
  }

  public fun RoleTable.selectSlice(vararg selective: Column<*>): Query {
    val query = if(selective.isNotEmpty()) {
      select(selective.toList())
    }
    else {
      selectAll()
    }
    return query
  }

  public fun UpdateBuilder<*>.setValue(raw: Role, vararg ignore: Column<*>): Unit = assign(this,
      raw, ignore = ignore)

  public fun UpdateBuilder<*>.setValueSelective(raw: Role, vararg selective: Column<*>): Unit =
      assign(this, raw, selective = selective)

  public fun RoleTable.insert(raw: Role): InsertStatement<Number> = insert {
    assign(it, raw)
  }

  public fun RoleTable.upsert(
    raw: Role,
    onUpdateExclude: List<Column<*>>? = null,
    onUpdate: (UpsertBuilder.(UpdateStatement) -> Unit)? = null,
    `where`: (SqlExpressionBuilder.() -> Op<Boolean>)? = null,
  ): UpsertStatement<Long> = upsert(where = where, onUpdate = onUpdate, onUpdateExclude =
      onUpdateExclude) {
    assign(it, raw)
  }

  public fun RoleTable.batchInsert(
    list: Iterable<Role>,
    ignoreErrors: Boolean = false,
    shouldReturnGeneratedValues: Boolean = false,
  ): List<ResultRow> {
    val rows = batchInsert(list, ignoreErrors, shouldReturnGeneratedValues) {
      entry -> assign(this, entry)
    }
    return rows
  }

  public fun RoleTable.update(
    raw: Role,
    selective: Array<out Column<*>>? = null,
    ignore: Array<out Column<*>>? = null,
    limit: Int? = null,
    `where`: SqlExpressionBuilder.() -> Op<Boolean>,
  ): Int = update(`where`, limit) {
    val ignoreColumns = ignore ?: arrayOf()
    assign(it, raw, selective = selective, *ignoreColumns)
  }

  public fun RoleTable.updateByPrimaryKey(raw: Role, vararg selective: Column<*>): Int = update(raw,
      selective = selective, ignore = arrayOf(id)) {
    RoleTable.id.eq(raw.id)
  }

  public fun RoleTable.updateByPrimaryKey(id: Long, builder: RoleTable.(UpdateStatement) -> Unit):
      Int = update({ RoleTable.id.eq(id) }, body = builder)

  public fun RoleTable.deleteByPrimaryKey(id: Long): Int = deleteWhere {
    RoleTable.id.eq(id)
  }

  public fun RoleTable.selectByPrimaryKey(id: Long, vararg selective: Column<*>): Role? {
    val query = selectSlice(*selective).andWhere {
      RoleTable.id.eq(id)
    }
    return query.firstOrNull()?.toRole(*selective)
  }

  public fun RoleTable.selectByPrimaryKeys(ids: Iterable<Long>, vararg selective: Column<*>):
      List<Role> {
    val query = selectSlice(*selective).andWhere {
      RoleTable.id inList ids
    }
    return query.toRoleList(*selective)
  }

  public fun RoleTable.selectMany(vararg selective: Column<*>, `where`: Query.() -> Query?):
      List<Role> {
    val query = selectSlice(*selective)
    `where`.invoke(query)
    return query.toRoleList(*selective)
  }

  public fun RoleTable.selectOne(vararg selective: Column<*>, `where`: Query.() -> Query?): Role? {
    val query = selectSlice(*selective)
    `where`.invoke(query)
    return query.firstOrNull()?.toRole(*selective)
  }

  public fun RoleTable.selectForwardByPrimaryKey(
    forwardToken: String? = null,
    order: SortOrder = SortOrder.DESC,
    pageSize: Int = 50,
    selective: Collection<Column<*>> = listOf(),
    `where`: (Query.() -> Query?)? = null,
  ): OffsetList<Role> {
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
    val list = sorted.limit(pageSize).toRoleList(*selective.toTypedArray())
    val token = if(list.size >= pageSize) {
      val lastId = list.last().id.toString().toByteArray(Charsets.UTF_8)
      Base64.getUrlEncoder().encodeToString(lastId)
    }
    else {
      null
    }
    return OffsetList(list, token)
  }

  public fun <T : Comparable<T>> RoleTable.selectForward(
    sortColumn: Column<T>,
    forwardToken: String? = null,
    order: SortOrder = SortOrder.DESC,
    pageSize: Int = 50,
    selective: Collection<Column<*>> = listOf(),
    `where`: (Query.() -> Query?)? = null,
  ): OffsetList<Role> {
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
    val list = sorted.limit(pageSize).toRoleList(*selective.toTypedArray())
    val token = if(list.size < pageSize) null else encodeToken(list, { getColumnValue(sortColumn) },
        Role::id)
    return OffsetList(list, token)
  }

  public fun RoleTable.replace(raw: Role): ReplaceStatement<Long> = replace {
    assign(it, raw)
  }
}
