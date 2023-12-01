@file:Suppress("RedundantVisibilityModifier")

package com.labijie.application.identity.`data`.pojo.dsl

import com.labijie.application.identity.`data`.UserClaimTable
import com.labijie.application.identity.`data`.UserClaimTable.claimType
import com.labijie.application.identity.`data`.UserClaimTable.claimValue
import com.labijie.application.identity.`data`.UserClaimTable.id
import com.labijie.application.identity.`data`.UserClaimTable.userId
import com.labijie.application.identity.`data`.pojo.UserClaim
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
 * DSL support for UserClaimTable
 *
 * This class made by a code generation tool (https://github.com/hongque-pro/infra-orm).
 *
 * Don't modify these codes !!
 *
 * Origin Exposed Table:
 * @see com.labijie.application.identity.data.UserClaimTable
 */
@kotlin.Suppress(
  "unused",
  "DuplicatedCode",
  "MemberVisibilityCanBePrivate",
  "RemoveRedundantQualifierName",
)
public object UserClaimDSL {
  public val UserClaimTable.allColumns: Array<Column<*>> by lazy {
    arrayOf(
    claimType,
    claimValue,
    userId,
    id,
    )
  }


  public fun parseRow(raw: ResultRow): UserClaim {
    val plain = UserClaim()
    plain.claimType = raw[claimType]
    plain.claimValue = raw[claimValue]
    plain.userId = raw[userId]
    plain.id = raw[id]
    return plain
  }

  public fun parseRowSelective(row: ResultRow): UserClaim {
    val plain = UserClaim()
    if(row.hasValue(claimType)) {
      plain.claimType = row[claimType]
    }
    if(row.hasValue(claimValue)) {
      plain.claimValue = row[claimValue]
    }
    if(row.hasValue(userId)) {
      plain.userId = row[userId]
    }
    if(row.hasValue(id)) {
      plain.id = row[id]
    }
    return plain
  }

  public fun <T> UserClaimTable.getColumnType(column: Column<T>): KClass<*> = when(column) {
    claimType->String::class
    claimValue->String::class
    userId->Long::class
    id->Long::class
    else->throw IllegalArgumentException("""Unknown column <${column.name}> for 'UserClaim'""")
  }

  @kotlin.Suppress("UNCHECKED_CAST")
  public fun <T> UserClaim.getColumnValue(column: Column<T>): T = when(column) {
    UserClaimTable.claimType->this.claimType as T
    UserClaimTable.claimValue->this.claimValue as T
    UserClaimTable.userId->this.userId as T
    UserClaimTable.id->this.id as T
    else->throw IllegalArgumentException("""Unknown column <${column.name}> for 'UserClaim'""")
  }

  public fun assign(
    builder: UpdateBuilder<*>,
    raw: UserClaim,
    selective: Array<out Column<*>>? = null,
    vararg ignore: Column<*>,
  ) {
    val list = if(selective.isNullOrEmpty()) null else selective
    if((list == null || list.contains(claimType)) && !ignore.contains(claimType))
      builder[claimType] = raw.claimType
    if((list == null || list.contains(claimValue)) && !ignore.contains(claimValue))
      builder[claimValue] = raw.claimValue
    if((list == null || list.contains(userId)) && !ignore.contains(userId))
      builder[userId] = raw.userId
    if((list == null || list.contains(id)) && !ignore.contains(id))
      builder[id] = raw.id
  }

  public fun ResultRow.toUserClaim(vararg selective: Column<*>): UserClaim {
    if(selective.isNotEmpty()) {
      return parseRowSelective(this)
    }
    return parseRow(this)
  }

  public fun Iterable<ResultRow>.toUserClaimList(vararg selective: Column<*>): List<UserClaim> =
      this.map {
    it.toUserClaim(*selective)
  }

  public fun UserClaimTable.selectSlice(vararg selective: Column<*>): Query {
    val query = if(selective.isNotEmpty()) {
      slice(selective.toList()).selectAll()
    }
    else {
      selectAll()
    }
    return query
  }

  public fun UpdateBuilder<*>.setValue(raw: UserClaim, vararg ignore: Column<*>): Unit =
      assign(this, raw, ignore = ignore)

  public fun UpdateBuilder<*>.setValueSelective(raw: UserClaim, vararg selective: Column<*>): Unit =
      assign(this, raw, selective = selective)

  public fun UserClaimTable.insert(raw: UserClaim): InsertStatement<Number> = insert {
    assign(it, raw)
  }

  public fun UserClaimTable.batchInsert(
    list: Iterable<UserClaim>,
    ignoreErrors: Boolean = false,
    shouldReturnGeneratedValues: Boolean = false,
  ): List<ResultRow> {
    val rows = batchInsert(list, ignoreErrors, shouldReturnGeneratedValues) {
      entry -> assign(this, entry)
    }
    return rows
  }

  public fun UserClaimTable.update(
    raw: UserClaim,
    selective: Array<out Column<*>>? = null,
    ignore: Array<out Column<*>>? = null,
    limit: Int? = null,
    `where`: SqlExpressionBuilder.() -> Op<Boolean>,
  ): Int = update(`where`, limit) {
    val ignoreColumns = ignore ?: arrayOf()
    assign(it, raw, selective = selective, *ignoreColumns)
  }

  public fun UserClaimTable.updateByPrimaryKey(raw: UserClaim, vararg selective: Column<*>): Int =
      update(raw, selective = selective, ignore = arrayOf(id)) {
    UserClaimTable.id.eq(raw.id)
  }

  public fun UserClaimTable.updateByPrimaryKey(id: Long,
      builder: UserClaimTable.(UpdateStatement) -> Unit): Int = update({ UserClaimTable.id.eq(id) },
      body = builder)

  public fun UserClaimTable.deleteByPrimaryKey(id: Long): Int = deleteWhere {
    UserClaimTable.id.eq(id)
  }

  public fun UserClaimTable.selectByPrimaryKey(id: Long, vararg selective: Column<*>): UserClaim? {
    val query = selectSlice(*selective).andWhere {
      UserClaimTable.id.eq(id)
    }
    return query.firstOrNull()?.toUserClaim(*selective)
  }

  public fun UserClaimTable.selectByPrimaryKeys(ids: Iterable<Long>, vararg selective: Column<*>):
      List<UserClaim> {
    val query = selectSlice(*selective).andWhere {
      UserClaimTable.id inList ids
    }
    return query.toUserClaimList(*selective)
  }

  public fun UserClaimTable.selectMany(vararg selective: Column<*>, `where`: Query.() -> Unit):
      List<UserClaim> {
    val query = selectSlice(*selective)
    `where`.invoke(query)
    return query.toUserClaimList(*selective)
  }

  public fun UserClaimTable.selectOne(vararg selective: Column<*>, `where`: Query.() -> Unit):
      UserClaim? {
    val query = selectSlice(*selective)
    `where`.invoke(query)
    return query.firstOrNull()?.toUserClaim(*selective)
  }

  public fun UserClaimTable.selectForwardByPrimaryKey(
    forwardToken: String? = null,
    order: SortOrder = SortOrder.DESC,
    pageSize: Int = 50,
    selective: Collection<Column<*>> = listOf(),
    `where`: (Query.() -> Unit)? = null,
  ): OffsetList<UserClaim> {
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
    val list = sorted.limit(pageSize).toUserClaimList(*selective.toTypedArray())
    val token = if(list.size >= pageSize) {
      val lastId = list.last().id.toString().toByteArray(Charsets.UTF_8)
      Base64.getUrlEncoder().encodeToString(lastId)
    }
    else {
      null
    }
    return OffsetList(list, token)
  }

  public fun <T : Comparable<T>> UserClaimTable.selectForward(
    sortColumn: Column<T>,
    forwardToken: String? = null,
    order: SortOrder = SortOrder.DESC,
    pageSize: Int = 50,
    selective: Collection<Column<*>> = listOf(),
    `where`: (Query.() -> Unit)? = null,
  ): OffsetList<UserClaim> {
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
    val list = sorted.limit(pageSize).toUserClaimList(*selective.toTypedArray())
    val token = if(list.size < pageSize) null else encodeToken(list, { getColumnValue(sortColumn) },
        UserClaim::id)
    return OffsetList(list, token)
  }
}
