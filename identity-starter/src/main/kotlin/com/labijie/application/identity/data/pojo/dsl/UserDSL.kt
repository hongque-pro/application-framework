@file:Suppress("RedundantVisibilityModifier")

package com.labijie.application.identity.`data`.pojo.dsl

import com.labijie.application.identity.`data`.UserTable
import com.labijie.application.identity.`data`.UserTable.accessFailedCount
import com.labijie.application.identity.`data`.UserTable.approved
import com.labijie.application.identity.`data`.UserTable.approverId
import com.labijie.application.identity.`data`.UserTable.concurrencyStamp
import com.labijie.application.identity.`data`.UserTable.email
import com.labijie.application.identity.`data`.UserTable.emailConfirmed
import com.labijie.application.identity.`data`.UserTable.id
import com.labijie.application.identity.`data`.UserTable.language
import com.labijie.application.identity.`data`.UserTable.lastClientVersion
import com.labijie.application.identity.`data`.UserTable.lastSignInArea
import com.labijie.application.identity.`data`.UserTable.lastSignInIp
import com.labijie.application.identity.`data`.UserTable.lastSignInPlatform
import com.labijie.application.identity.`data`.UserTable.lockoutEnabled
import com.labijie.application.identity.`data`.UserTable.lockoutEnd
import com.labijie.application.identity.`data`.UserTable.passwordHash
import com.labijie.application.identity.`data`.UserTable.phoneCountryCode
import com.labijie.application.identity.`data`.UserTable.phoneNumber
import com.labijie.application.identity.`data`.UserTable.phoneNumberConfirmed
import com.labijie.application.identity.`data`.UserTable.securityStamp
import com.labijie.application.identity.`data`.UserTable.timeCreated
import com.labijie.application.identity.`data`.UserTable.timeExpired
import com.labijie.application.identity.`data`.UserTable.timeLastActivity
import com.labijie.application.identity.`data`.UserTable.timeLastLogin
import com.labijie.application.identity.`data`.UserTable.timeZone
import com.labijie.application.identity.`data`.UserTable.twoFactorEnabled
import com.labijie.application.identity.`data`.UserTable.userName
import com.labijie.application.identity.`data`.UserTable.userType
import com.labijie.application.identity.`data`.pojo.User
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
import kotlin.Pair
import kotlin.Short
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
 * DSL support for UserTable
 *
 * This class made by a code generation tool (https://github.com/hongque-pro/infra-orm).
 *
 * Don't modify these codes !!
 *
 * Origin Exposed Table:
 * @see com.labijie.application.identity.data.UserTable
 */
@kotlin.Suppress(
  "unused",
  "DuplicatedCode",
  "MemberVisibilityCanBePrivate",
  "RemoveRedundantQualifierName",
)
public object UserDSL {
  public val UserTable.allColumns: Array<Column<*>> by lazy {
    arrayOf(
    userName,
    userType,
    accessFailedCount,
    concurrencyStamp,
    email,
    emailConfirmed,
    language,
    lockoutEnabled,
    lockoutEnd,
    passwordHash,
    phoneCountryCode,
    phoneNumber,
    phoneNumberConfirmed,
    securityStamp,
    timeZone,
    twoFactorEnabled,
    approved,
    approverId,
    timeExpired,
    timeLastLogin,
    timeLastActivity,
    timeCreated,
    lastSignInIp,
    lastSignInPlatform,
    lastSignInArea,
    lastClientVersion,
    id,
    )
  }

  public fun parseRow(raw: ResultRow): User {
    val plain = User()
    plain.userName = raw[userName]
    plain.userType = raw[userType]
    plain.accessFailedCount = raw[accessFailedCount]
    plain.concurrencyStamp = raw[concurrencyStamp]
    plain.email = raw[email]
    plain.emailConfirmed = raw[emailConfirmed]
    plain.language = raw[language]
    plain.lockoutEnabled = raw[lockoutEnabled]
    plain.lockoutEnd = raw[lockoutEnd]
    plain.passwordHash = raw[passwordHash]
    plain.phoneCountryCode = raw[phoneCountryCode]
    plain.phoneNumber = raw[phoneNumber]
    plain.phoneNumberConfirmed = raw[phoneNumberConfirmed]
    plain.securityStamp = raw[securityStamp]
    plain.timeZone = raw[timeZone]
    plain.twoFactorEnabled = raw[twoFactorEnabled]
    plain.approved = raw[approved]
    plain.approverId = raw[approverId]
    plain.timeExpired = raw[timeExpired]
    plain.timeLastLogin = raw[timeLastLogin]
    plain.timeLastActivity = raw[timeLastActivity]
    plain.timeCreated = raw[timeCreated]
    plain.lastSignInIp = raw[lastSignInIp]
    plain.lastSignInPlatform = raw[lastSignInPlatform]
    plain.lastSignInArea = raw[lastSignInArea]
    plain.lastClientVersion = raw[lastClientVersion]
    plain.id = raw[id]
    return plain
  }

  public fun parseRowSelective(row: ResultRow): User {
    val plain = User()
    if(row.hasValue(userName)) {
      plain.userName = row[userName]
    }
    if(row.hasValue(userType)) {
      plain.userType = row[userType]
    }
    if(row.hasValue(accessFailedCount)) {
      plain.accessFailedCount = row[accessFailedCount]
    }
    if(row.hasValue(concurrencyStamp)) {
      plain.concurrencyStamp = row[concurrencyStamp]
    }
    if(row.hasValue(email)) {
      plain.email = row[email]
    }
    if(row.hasValue(emailConfirmed)) {
      plain.emailConfirmed = row[emailConfirmed]
    }
    if(row.hasValue(language)) {
      plain.language = row[language]
    }
    if(row.hasValue(lockoutEnabled)) {
      plain.lockoutEnabled = row[lockoutEnabled]
    }
    if(row.hasValue(lockoutEnd)) {
      plain.lockoutEnd = row[lockoutEnd]
    }
    if(row.hasValue(passwordHash)) {
      plain.passwordHash = row[passwordHash]
    }
    if(row.hasValue(phoneCountryCode)) {
      plain.phoneCountryCode = row[phoneCountryCode]
    }
    if(row.hasValue(phoneNumber)) {
      plain.phoneNumber = row[phoneNumber]
    }
    if(row.hasValue(phoneNumberConfirmed)) {
      plain.phoneNumberConfirmed = row[phoneNumberConfirmed]
    }
    if(row.hasValue(securityStamp)) {
      plain.securityStamp = row[securityStamp]
    }
    if(row.hasValue(timeZone)) {
      plain.timeZone = row[timeZone]
    }
    if(row.hasValue(twoFactorEnabled)) {
      plain.twoFactorEnabled = row[twoFactorEnabled]
    }
    if(row.hasValue(approved)) {
      plain.approved = row[approved]
    }
    if(row.hasValue(approverId)) {
      plain.approverId = row[approverId]
    }
    if(row.hasValue(timeExpired)) {
      plain.timeExpired = row[timeExpired]
    }
    if(row.hasValue(timeLastLogin)) {
      plain.timeLastLogin = row[timeLastLogin]
    }
    if(row.hasValue(timeLastActivity)) {
      plain.timeLastActivity = row[timeLastActivity]
    }
    if(row.hasValue(timeCreated)) {
      plain.timeCreated = row[timeCreated]
    }
    if(row.hasValue(lastSignInIp)) {
      plain.lastSignInIp = row[lastSignInIp]
    }
    if(row.hasValue(lastSignInPlatform)) {
      plain.lastSignInPlatform = row[lastSignInPlatform]
    }
    if(row.hasValue(lastSignInArea)) {
      plain.lastSignInArea = row[lastSignInArea]
    }
    if(row.hasValue(lastClientVersion)) {
      plain.lastClientVersion = row[lastClientVersion]
    }
    if(row.hasValue(id)) {
      plain.id = row[id]
    }
    return plain
  }

  public fun <T> UserTable.getColumnType(column: Column<T>): KClass<*> = when(column) {
    userName->String::class
    userType->Byte::class
    accessFailedCount->Short::class
    concurrencyStamp->String::class
    email->String::class
    emailConfirmed->Boolean::class
    language->String::class
    lockoutEnabled->Boolean::class
    lockoutEnd->Long::class
    passwordHash->String::class
    phoneCountryCode->Short::class
    phoneNumber->String::class
    phoneNumberConfirmed->Boolean::class
    securityStamp->String::class
    timeZone->String::class
    twoFactorEnabled->Boolean::class
    approved->Boolean::class
    approverId->Long::class
    timeExpired->Long::class
    timeLastLogin->Long::class
    timeLastActivity->Long::class
    timeCreated->Long::class
    lastSignInIp->String::class
    lastSignInPlatform->String::class
    lastSignInArea->String::class
    lastClientVersion->String::class
    id->Long::class
    else->throw IllegalArgumentException("""Unknown column <${column.name}> for 'User'""")
  }

  @kotlin.Suppress("UNCHECKED_CAST")
  public fun <T> User.getColumnValue(column: Column<T>): T = when(column) {
    UserTable.userName->this.userName as T
    UserTable.userType->this.userType as T
    UserTable.accessFailedCount->this.accessFailedCount as T
    UserTable.concurrencyStamp->this.concurrencyStamp as T
    UserTable.email->this.email as T
    UserTable.emailConfirmed->this.emailConfirmed as T
    UserTable.language->this.language as T
    UserTable.lockoutEnabled->this.lockoutEnabled as T
    UserTable.lockoutEnd->this.lockoutEnd as T
    UserTable.passwordHash->this.passwordHash as T
    UserTable.phoneCountryCode->this.phoneCountryCode as T
    UserTable.phoneNumber->this.phoneNumber as T
    UserTable.phoneNumberConfirmed->this.phoneNumberConfirmed as T
    UserTable.securityStamp->this.securityStamp as T
    UserTable.timeZone->this.timeZone as T
    UserTable.twoFactorEnabled->this.twoFactorEnabled as T
    UserTable.approved->this.approved as T
    UserTable.approverId->this.approverId as T
    UserTable.timeExpired->this.timeExpired as T
    UserTable.timeLastLogin->this.timeLastLogin as T
    UserTable.timeLastActivity->this.timeLastActivity as T
    UserTable.timeCreated->this.timeCreated as T
    UserTable.lastSignInIp->this.lastSignInIp as T
    UserTable.lastSignInPlatform->this.lastSignInPlatform as T
    UserTable.lastSignInArea->this.lastSignInArea as T
    UserTable.lastClientVersion->this.lastClientVersion as T
    UserTable.id->this.id as T
    else->throw IllegalArgumentException("""Unknown column <${column.name}> for 'User'""")
  }

  public fun assign(
    builder: UpdateBuilder<*>,
    raw: User,
    selective: Array<out Column<*>>? = null,
    vararg ignore: Column<*>,
  ) {
    val list = if(selective.isNullOrEmpty()) null else selective
    if((list == null || list.contains(userName)) && !ignore.contains(userName))
      builder[userName] = raw.userName
    if((list == null || list.contains(userType)) && !ignore.contains(userType))
      builder[userType] = raw.userType
    if((list == null || list.contains(accessFailedCount)) && !ignore.contains(accessFailedCount))
      builder[accessFailedCount] = raw.accessFailedCount
    if((list == null || list.contains(concurrencyStamp)) && !ignore.contains(concurrencyStamp))
      builder[concurrencyStamp] = raw.concurrencyStamp
    if((list == null || list.contains(email)) && !ignore.contains(email))
      builder[email] = raw.email
    if((list == null || list.contains(emailConfirmed)) && !ignore.contains(emailConfirmed))
      builder[emailConfirmed] = raw.emailConfirmed
    if((list == null || list.contains(language)) && !ignore.contains(language))
      builder[language] = raw.language
    if((list == null || list.contains(lockoutEnabled)) && !ignore.contains(lockoutEnabled))
      builder[lockoutEnabled] = raw.lockoutEnabled
    if((list == null || list.contains(lockoutEnd)) && !ignore.contains(lockoutEnd))
      builder[lockoutEnd] = raw.lockoutEnd
    if((list == null || list.contains(passwordHash)) && !ignore.contains(passwordHash))
      builder[passwordHash] = raw.passwordHash
    if((list == null || list.contains(phoneCountryCode)) && !ignore.contains(phoneCountryCode))
      builder[phoneCountryCode] = raw.phoneCountryCode
    if((list == null || list.contains(phoneNumber)) && !ignore.contains(phoneNumber))
      builder[phoneNumber] = raw.phoneNumber
    if((list == null || list.contains(phoneNumberConfirmed)) &&
        !ignore.contains(phoneNumberConfirmed))
      builder[phoneNumberConfirmed] = raw.phoneNumberConfirmed
    if((list == null || list.contains(securityStamp)) && !ignore.contains(securityStamp))
      builder[securityStamp] = raw.securityStamp
    if((list == null || list.contains(timeZone)) && !ignore.contains(timeZone))
      builder[timeZone] = raw.timeZone
    if((list == null || list.contains(twoFactorEnabled)) && !ignore.contains(twoFactorEnabled))
      builder[twoFactorEnabled] = raw.twoFactorEnabled
    if((list == null || list.contains(approved)) && !ignore.contains(approved))
      builder[approved] = raw.approved
    if((list == null || list.contains(approverId)) && !ignore.contains(approverId))
      builder[approverId] = raw.approverId
    if((list == null || list.contains(timeExpired)) && !ignore.contains(timeExpired))
      builder[timeExpired] = raw.timeExpired
    if((list == null || list.contains(timeLastLogin)) && !ignore.contains(timeLastLogin))
      builder[timeLastLogin] = raw.timeLastLogin
    if((list == null || list.contains(timeLastActivity)) && !ignore.contains(timeLastActivity))
      builder[timeLastActivity] = raw.timeLastActivity
    if((list == null || list.contains(timeCreated)) && !ignore.contains(timeCreated))
      builder[timeCreated] = raw.timeCreated
    if((list == null || list.contains(lastSignInIp)) && !ignore.contains(lastSignInIp))
      builder[lastSignInIp] = raw.lastSignInIp
    if((list == null || list.contains(lastSignInPlatform)) && !ignore.contains(lastSignInPlatform))
      builder[lastSignInPlatform] = raw.lastSignInPlatform
    if((list == null || list.contains(lastSignInArea)) && !ignore.contains(lastSignInArea))
      builder[lastSignInArea] = raw.lastSignInArea
    if((list == null || list.contains(lastClientVersion)) && !ignore.contains(lastClientVersion))
      builder[lastClientVersion] = raw.lastClientVersion
    if((list == null || list.contains(id)) && !ignore.contains(id))
      builder[id] = raw.id
  }

  public fun ResultRow.toUser(vararg selective: Column<*>): User {
    if(selective.isNotEmpty()) {
      return parseRowSelective(this)
    }
    return parseRow(this)
  }

  public fun Iterable<ResultRow>.toUserList(vararg selective: Column<*>): List<User> = this.map {
    it.toUser(*selective)
  }

  public fun UserTable.selectSlice(vararg selective: Column<*>): Query {
    val query = if(selective.isNotEmpty()) {
      select(selective.toList())
    }
    else {
      selectAll()
    }
    return query
  }

  public fun UpdateBuilder<*>.setValue(raw: User, vararg ignore: Column<*>): Unit = assign(this,
      raw, ignore = ignore)

  public fun UpdateBuilder<*>.setValueSelective(raw: User, vararg selective: Column<*>): Unit =
      assign(this, raw, selective = selective)

  public fun UserTable.insert(raw: User): InsertStatement<Number> = insert {
    assign(it, raw)
  }

  public fun UserTable.upsert(
    raw: User,
    onUpdate: List<Pair<Column<*>, Expression<*>>>? = null,
    onUpdateExclude: List<Column<*>>? = null,
    `where`: (SqlExpressionBuilder.() -> Op<Boolean>)? = null,
  ): UpsertStatement<Long> = upsert(where = where, onUpdate = onUpdate, onUpdateExclude =
      onUpdateExclude) {
    assign(it, raw)
  }

  public fun UserTable.batchInsert(
    list: Iterable<User>,
    ignoreErrors: Boolean = false,
    shouldReturnGeneratedValues: Boolean = false,
  ): List<ResultRow> {
    val rows = batchInsert(list, ignoreErrors, shouldReturnGeneratedValues) {
      entry -> assign(this, entry)
    }
    return rows
  }

  public fun UserTable.update(
    raw: User,
    selective: Array<out Column<*>>? = null,
    ignore: Array<out Column<*>>? = null,
    limit: Int? = null,
    `where`: SqlExpressionBuilder.() -> Op<Boolean>,
  ): Int = update(`where`, limit) {
    val ignoreColumns = ignore ?: arrayOf()
    assign(it, raw, selective = selective, *ignoreColumns)
  }

  public fun UserTable.updateByPrimaryKey(raw: User, vararg selective: Column<*>): Int = update(raw,
      selective = selective, ignore = arrayOf(id)) {
    UserTable.id.eq(raw.id)
  }

  public fun UserTable.updateByPrimaryKey(id: Long, builder: UserTable.(UpdateStatement) -> Unit):
      Int = update({ UserTable.id.eq(id) }, body = builder)

  public fun UserTable.deleteByPrimaryKey(id: Long): Int = deleteWhere {
    UserTable.id.eq(id)
  }

  public fun UserTable.selectByPrimaryKey(id: Long, vararg selective: Column<*>): User? {
    val query = selectSlice(*selective).andWhere {
      UserTable.id.eq(id)
    }
    return query.firstOrNull()?.toUser(*selective)
  }

  public fun UserTable.selectByPrimaryKeys(ids: Iterable<Long>, vararg selective: Column<*>):
      List<User> {
    val query = selectSlice(*selective).andWhere {
      UserTable.id inList ids
    }
    return query.toUserList(*selective)
  }

  public fun UserTable.selectMany(vararg selective: Column<*>, `where`: Query.() -> Unit):
      List<User> {
    val query = selectSlice(*selective)
    `where`.invoke(query)
    return query.toUserList(*selective)
  }

  public fun UserTable.selectOne(vararg selective: Column<*>, `where`: Query.() -> Unit): User? {
    val query = selectSlice(*selective)
    `where`.invoke(query)
    return query.firstOrNull()?.toUser(*selective)
  }

  public fun UserTable.selectForwardByPrimaryKey(
    forwardToken: String? = null,
    order: SortOrder = SortOrder.DESC,
    pageSize: Int = 50,
    selective: Collection<Column<*>> = listOf(),
    `where`: (Query.() -> Unit)? = null,
  ): OffsetList<User> {
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
    val list = sorted.limit(pageSize).toUserList(*selective.toTypedArray())
    val token = if(list.size >= pageSize) {
      val lastId = list.last().id.toString().toByteArray(Charsets.UTF_8)
      Base64.getUrlEncoder().encodeToString(lastId)
    }
    else {
      null
    }
    return OffsetList(list, token)
  }

  public fun <T : Comparable<T>> UserTable.selectForward(
    sortColumn: Column<T>,
    forwardToken: String? = null,
    order: SortOrder = SortOrder.DESC,
    pageSize: Int = 50,
    selective: Collection<Column<*>> = listOf(),
    `where`: (Query.() -> Unit)? = null,
  ): OffsetList<User> {
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
    val list = sorted.limit(pageSize).toUserList(*selective.toTypedArray())
    val token = if(list.size < pageSize) null else encodeToken(list, { getColumnValue(sortColumn) },
        User::id)
    return OffsetList(list, token)
  }

  public fun UserTable.replace(raw: User): ReplaceStatement<Long> = replace {
    assign(it, raw)
  }
}
