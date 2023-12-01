@file:Suppress("RedundantVisibilityModifier")

package com.labijie.application.identity.`data`.pojo.dsl

import com.labijie.application.identity.`data`.RoleClaimTable
import com.labijie.application.identity.`data`.RoleClaimTable.claimType
import com.labijie.application.identity.`data`.RoleClaimTable.claimValue
import com.labijie.application.identity.`data`.RoleClaimTable.id
import com.labijie.application.identity.`data`.RoleClaimTable.roleId
import com.labijie.application.identity.`data`.pojo.RoleClaim
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
 * DSL support for RoleClaimTable
 *
 * This class made by a code generation tool (https://github.com/hongque-pro/infra-orm).
 *
 * Don't modify these codes !!
 *
 * Origin Exposed Table:
 * @see com.labijie.application.identity.data.RoleClaimTable
 */
@kotlin.Suppress(
  "unused",
  "DuplicatedCode",
  "MemberVisibilityCanBePrivate",
  "RemoveRedundantQualifierName",
)
public object RoleClaimDSL {
  public val RoleClaimTable.allColumns: Array<Column<*>> by lazy {
    arrayOf(
    claimType,
    claimValue,
    roleId,
    id,
    )
  }


  public fun parseRow(raw: ResultRow): RoleClaim {
    val plain = RoleClaim()
    plain.claimType = raw[claimType]
    plain.claimValue = raw[claimValue]
    plain.roleId = raw[roleId]
    plain.id = raw[id]
    return plain
  }

  public fun parseRowSelective(row: ResultRow): RoleClaim {
    val plain = RoleClaim()
    if(row.hasValue(claimType)) {
      plain.claimType = row[claimType]
    }
    if(row.hasValue(claimValue)) {
      plain.claimValue = row[claimValue]
    }
    if(row.hasValue(roleId)) {
      plain.roleId = row[roleId]
    }
    if(row.hasValue(id)) {
      plain.id = row[id]
    }
    return plain
  }

  public fun <T> RoleClaimTable.getColumnType(column: Column<T>): KClass<*> = when(column) {
    claimType->String::class
    claimValue->String::class
    roleId->Long::class
    id->Long::class
    else->throw IllegalArgumentException("""Unknown column <${column.name}> for 'RoleClaim'""")
  }

  @kotlin.Suppress("UNCHECKED_CAST")
  public fun <T> RoleClaim.getColumnValue(column: Column<T>): T = when(column) {
    RoleClaimTable.claimType->this.claimType as T
    RoleClaimTable.claimValue->this.claimValue as T
    RoleClaimTable.roleId->this.roleId as T
    RoleClaimTable.id->this.id as T
    else->throw IllegalArgumentException("""Unknown column <${column.name}> for 'RoleClaim'""")
  }

  public fun assign(
    builder: UpdateBuilder<*>,
    raw: RoleClaim,
    selective: Array<out Column<*>>? = null,
    vararg ignore: Column<*>,
  ) {
    val list = if(selective.isNullOrEmpty()) null else selective
    if((list == null || list.contains(claimType)) && !ignore.contains(claimType))
      builder[claimType] = raw.claimType
    if((list == null || list.contains(claimValue)) && !ignore.contains(claimValue))
      builder[claimValue] = raw.claimValue
    if((list == null || list.contains(roleId)) && !ignore.contains(roleId))
      builder[roleId] = raw.roleId
    if((list == null || list.contains(id)) && !ignore.contains(id))
      builder[id] = raw.id
  }

  public fun ResultRow.toRoleClaim(vararg selective: Column<*>): RoleClaim {
    if(selective.isNotEmpty()) {
      return parseRowSelective(this)
    }
    return parseRow(this)
  }

  public fun Iterable<ResultRow>.toRoleClaimList(vararg selective: Column<*>): List<RoleClaim> =
      this.map {
    it.toRoleClaim(*selective)
  }

  public fun RoleClaimTable.selectSlice(vararg selective: Column<*>): Query {
    val query = if(selective.isNotEmpty()) {
      slice(selective.toList()).selectAll()
    }
    else {
      selectAll()
    }
    return query
  }

  public fun UpdateBuilder<*>.setValue(raw: RoleClaim, vararg ignore: Column<*>): Unit =
      assign(this, raw, ignore = ignore)

  public fun UpdateBuilder<*>.setValueSelective(raw: RoleClaim, vararg selective: Column<*>): Unit =
      assign(this, raw, selective = selective)

  public fun RoleClaimTable.insert(raw: RoleClaim): InsertStatement<Number> = insert {
    assign(it, raw)
  }

  public fun RoleClaimTable.batchInsert(
    list: Iterable<RoleClaim>,
    ignoreErrors: Boolean = false,
    shouldReturnGeneratedValues: Boolean = false,
  ): List<ResultRow> {
    val rows = batchInsert(list, ignoreErrors, shouldReturnGeneratedValues) {
      entry -> assign(this, entry)
    }
    return rows
  }

  public fun RoleClaimTable.update(
    raw: RoleClaim,
    selective: Array<out Column<*>>? = null,
    ignore: Array<out Column<*>>? = null,
    limit: Int? = null,
    `where`: SqlExpressionBuilder.() -> Op<Boolean>,
  ): Int = update(`where`, limit) {
    val ignoreColumns = ignore ?: arrayOf()
    assign(it, raw, selective = selective, *ignoreColumns)
  }

  public fun RoleClaimTable.updateByPrimaryKey(raw: RoleClaim, vararg selective: Column<*>): Int =
      update(raw, selective = selective, ignore = arrayOf(id)) {
    RoleClaimTable.id.eq(raw.id)
  }

  public fun RoleClaimTable.updateByPrimaryKey(id: Long,
      builder: RoleClaimTable.(UpdateStatement) -> Unit): Int = update({ RoleClaimTable.id.eq(id) },
      body = builder)

  public fun RoleClaimTable.deleteByPrimaryKey(id: Long): Int = deleteWhere {
    RoleClaimTable.id.eq(id)
  }

  public fun RoleClaimTable.selectByPrimaryKey(id: Long, vararg selective: Column<*>): RoleClaim? {
    val query = selectSlice(*selective).andWhere {
      RoleClaimTable.id.eq(id)
    }
    return query.firstOrNull()?.toRoleClaim(*selective)
  }

  public fun RoleClaimTable.selectByPrimaryKeys(ids: Iterable<Long>, vararg selective: Column<*>):
      List<RoleClaim> {
    val query = selectSlice(*selective).andWhere {
      RoleClaimTable.id inList ids
    }
    return query.toRoleClaimList(*selective)
  }

  public fun RoleClaimTable.selectMany(vararg selective: Column<*>, `where`: Query.() -> Unit):
      List<RoleClaim> {
    val query = selectSlice(*selective)
    `where`.invoke(query)
    return query.toRoleClaimList(*selective)
  }

  public fun RoleClaimTable.selectOne(vararg selective: Column<*>, `where`: Query.() -> Unit):
      RoleClaim? {
    val query = selectSlice(*selective)
    `where`.invoke(query)
    return query.firstOrNull()?.toRoleClaim(*selective)
  }

  public fun RoleClaimTable.selectForwardByPrimaryKey(
    forwardToken: String? = null,
    order: SortOrder = SortOrder.DESC,
    pageSize: Int = 50,
    selective: Collection<Column<*>> = listOf(),
    `where`: (Query.() -> Unit)? = null,
  ): OffsetList<RoleClaim> {
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
    val list = sorted.limit(pageSize).toRoleClaimList(*selective.toTypedArray())
    val token = if(list.size >= pageSize) {
      val lastId = list.last().id.toString().toByteArray(Charsets.UTF_8)
      Base64.getUrlEncoder().encodeToString(lastId)
    }
    else {
      null
    }
    return OffsetList(list, token)
  }

  public fun <T : Comparable<T>> RoleClaimTable.selectForward(
    sortColumn: Column<T>,
    forwardToken: String? = null,
    order: SortOrder = SortOrder.DESC,
    pageSize: Int = 50,
    selective: Collection<Column<*>> = listOf(),
    `where`: (Query.() -> Unit)? = null,
  ): OffsetList<RoleClaim> {
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
    val list = sorted.limit(pageSize).toRoleClaimList(*selective.toTypedArray())
    val token = if(list.size < pageSize) null else encodeToken(list, { getColumnValue(sortColumn) },
        RoleClaim::id)
    return OffsetList(list, token)
  }
}
