@file:Suppress("RedundantVisibilityModifier")

package com.labijie.application.identity.`data`.pojo.dsl

import com.labijie.application.identity.`data`.OAuth2ClientTable
import com.labijie.application.identity.`data`.OAuth2ClientTable.accessTokenLiveSeconds
import com.labijie.application.identity.`data`.OAuth2ClientTable.additionalInformation
import com.labijie.application.identity.`data`.OAuth2ClientTable.authorities
import com.labijie.application.identity.`data`.OAuth2ClientTable.authorizedGrantTypes
import com.labijie.application.identity.`data`.OAuth2ClientTable.autoApprove
import com.labijie.application.identity.`data`.OAuth2ClientTable.clientId
import com.labijie.application.identity.`data`.OAuth2ClientTable.clientSecret
import com.labijie.application.identity.`data`.OAuth2ClientTable.enabled
import com.labijie.application.identity.`data`.OAuth2ClientTable.redirectUrls
import com.labijie.application.identity.`data`.OAuth2ClientTable.refreshTokenLiveSeconds
import com.labijie.application.identity.`data`.OAuth2ClientTable.resourceIds
import com.labijie.application.identity.`data`.OAuth2ClientTable.scopes
import com.labijie.application.identity.`data`.pojo.OAuth2Client
import java.lang.IllegalArgumentException
import kotlin.Array
import kotlin.Boolean
import kotlin.Int
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
import org.jetbrains.exposed.sql.batchInsert
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.statements.InsertStatement
import org.jetbrains.exposed.sql.statements.UpdateBuilder
import org.jetbrains.exposed.sql.update

/**
 * DSL support for OAuth2ClientTable
 *
 * This class made by a code generation tool (https://github.com/hongque-pro/infra-orm).
 *
 * Don't modify these codes !!
 *
 * Origin Exposed Table:
 * @see com.labijie.application.identity.data.OAuth2ClientTable
 */
@kotlin.Suppress(
  "unused",
  "DuplicatedCode",
  "MemberVisibilityCanBePrivate",
  "RemoveRedundantQualifierName",
)
public object OAuth2ClientDSL {
  public val OAuth2ClientTable.allColumns: Array<Column<*>> by lazy {
    arrayOf(
    clientId,
    resourceIds,
    clientSecret,
    scopes,
    authorizedGrantTypes,
    redirectUrls,
    authorities,
    additionalInformation,
    autoApprove,
    enabled,
    accessTokenLiveSeconds,
    refreshTokenLiveSeconds,
    )
  }


  public fun parseRow(raw: ResultRow): OAuth2Client {
    val plain = OAuth2Client()
    plain.clientId = raw[clientId]
    plain.resourceIds = raw[resourceIds]
    plain.clientSecret = raw[clientSecret]
    plain.scopes = raw[scopes]
    plain.authorizedGrantTypes = raw[authorizedGrantTypes]
    plain.redirectUrls = raw[redirectUrls]
    plain.authorities = raw[authorities]
    plain.additionalInformation = raw[additionalInformation]
    plain.autoApprove = raw[autoApprove]
    plain.enabled = raw[enabled]
    plain.accessTokenLiveSeconds = raw[accessTokenLiveSeconds]
    plain.refreshTokenLiveSeconds = raw[refreshTokenLiveSeconds]
    return plain
  }

  public fun parseRowSelective(row: ResultRow): OAuth2Client {
    val plain = OAuth2Client()
    if(row.hasValue(clientId)) {
      plain.clientId = row[clientId]
    }
    if(row.hasValue(resourceIds)) {
      plain.resourceIds = row[resourceIds]
    }
    if(row.hasValue(clientSecret)) {
      plain.clientSecret = row[clientSecret]
    }
    if(row.hasValue(scopes)) {
      plain.scopes = row[scopes]
    }
    if(row.hasValue(authorizedGrantTypes)) {
      plain.authorizedGrantTypes = row[authorizedGrantTypes]
    }
    if(row.hasValue(redirectUrls)) {
      plain.redirectUrls = row[redirectUrls]
    }
    if(row.hasValue(authorities)) {
      plain.authorities = row[authorities]
    }
    if(row.hasValue(additionalInformation)) {
      plain.additionalInformation = row[additionalInformation]
    }
    if(row.hasValue(autoApprove)) {
      plain.autoApprove = row[autoApprove]
    }
    if(row.hasValue(enabled)) {
      plain.enabled = row[enabled]
    }
    if(row.hasValue(accessTokenLiveSeconds)) {
      plain.accessTokenLiveSeconds = row[accessTokenLiveSeconds]
    }
    if(row.hasValue(refreshTokenLiveSeconds)) {
      plain.refreshTokenLiveSeconds = row[refreshTokenLiveSeconds]
    }
    return plain
  }

  public fun <T> OAuth2ClientTable.getColumnType(column: Column<T>): KClass<*> = when(column) {
    clientId->String::class
    resourceIds->String::class
    clientSecret->String::class
    scopes->String::class
    authorizedGrantTypes->String::class
    redirectUrls->String::class
    authorities->String::class
    additionalInformation->String::class
    autoApprove->Boolean::class
    enabled->Boolean::class
    accessTokenLiveSeconds->Int::class
    refreshTokenLiveSeconds->Int::class
    else->throw IllegalArgumentException("""Unknown column <${column.name}> for 'OAuth2Client'""")
  }

  @kotlin.Suppress("UNCHECKED_CAST")
  public fun <T> OAuth2Client.getColumnValue(column: Column<T>): T = when(column) {
    OAuth2ClientTable.clientId->this.clientId as T
    OAuth2ClientTable.resourceIds->this.resourceIds as T
    OAuth2ClientTable.clientSecret->this.clientSecret as T
    OAuth2ClientTable.scopes->this.scopes as T
    OAuth2ClientTable.authorizedGrantTypes->this.authorizedGrantTypes as T
    OAuth2ClientTable.redirectUrls->this.redirectUrls as T
    OAuth2ClientTable.authorities->this.authorities as T
    OAuth2ClientTable.additionalInformation->this.additionalInformation as T
    OAuth2ClientTable.autoApprove->this.autoApprove as T
    OAuth2ClientTable.enabled->this.enabled as T
    OAuth2ClientTable.accessTokenLiveSeconds->this.accessTokenLiveSeconds as T
    OAuth2ClientTable.refreshTokenLiveSeconds->this.refreshTokenLiveSeconds as T
    else->throw IllegalArgumentException("""Unknown column <${column.name}> for 'OAuth2Client'""")
  }

  public fun assign(
    builder: UpdateBuilder<*>,
    raw: OAuth2Client,
    selective: Array<out Column<*>>? = null,
    vararg ignore: Column<*>,
  ) {
    val list = if(selective.isNullOrEmpty()) null else selective
    if((list == null || list.contains(clientId)) && !ignore.contains(clientId))
      builder[clientId] = raw.clientId
    if((list == null || list.contains(resourceIds)) && !ignore.contains(resourceIds))
      builder[resourceIds] = raw.resourceIds
    if((list == null || list.contains(clientSecret)) && !ignore.contains(clientSecret))
      builder[clientSecret] = raw.clientSecret
    if((list == null || list.contains(scopes)) && !ignore.contains(scopes))
      builder[scopes] = raw.scopes
    if((list == null || list.contains(authorizedGrantTypes)) &&
        !ignore.contains(authorizedGrantTypes))
      builder[authorizedGrantTypes] = raw.authorizedGrantTypes
    if((list == null || list.contains(redirectUrls)) && !ignore.contains(redirectUrls))
      builder[redirectUrls] = raw.redirectUrls
    if((list == null || list.contains(authorities)) && !ignore.contains(authorities))
      builder[authorities] = raw.authorities
    if((list == null || list.contains(additionalInformation)) &&
        !ignore.contains(additionalInformation))
      builder[additionalInformation] = raw.additionalInformation
    if((list == null || list.contains(autoApprove)) && !ignore.contains(autoApprove))
      builder[autoApprove] = raw.autoApprove
    if((list == null || list.contains(enabled)) && !ignore.contains(enabled))
      builder[enabled] = raw.enabled
    if((list == null || list.contains(accessTokenLiveSeconds)) &&
        !ignore.contains(accessTokenLiveSeconds))
      builder[accessTokenLiveSeconds] = raw.accessTokenLiveSeconds
    if((list == null || list.contains(refreshTokenLiveSeconds)) &&
        !ignore.contains(refreshTokenLiveSeconds))
      builder[refreshTokenLiveSeconds] = raw.refreshTokenLiveSeconds
  }

  public fun ResultRow.toOAuth2Client(vararg selective: Column<*>): OAuth2Client {
    if(selective.isNotEmpty()) {
      return parseRowSelective(this)
    }
    return parseRow(this)
  }

  public fun Iterable<ResultRow>.toOAuth2ClientList(vararg selective: Column<*>): List<OAuth2Client>
      = this.map {
    it.toOAuth2Client(*selective)
  }

  public fun OAuth2ClientTable.selectSlice(vararg selective: Column<*>): Query {
    val query = if(selective.isNotEmpty()) {
      slice(selective.toList()).selectAll()
    }
    else {
      selectAll()
    }
    return query
  }

  public fun UpdateBuilder<*>.setValue(raw: OAuth2Client, vararg ignore: Column<*>): Unit =
      assign(this, raw, ignore = ignore)

  public fun UpdateBuilder<*>.setValueSelective(raw: OAuth2Client, vararg selective: Column<*>):
      Unit = assign(this, raw, selective = selective)

  public fun OAuth2ClientTable.insert(raw: OAuth2Client): InsertStatement<Number> = insert {
    assign(it, raw)
  }

  public fun OAuth2ClientTable.batchInsert(
    list: Iterable<OAuth2Client>,
    ignoreErrors: Boolean = false,
    shouldReturnGeneratedValues: Boolean = false,
  ): List<ResultRow> {
    val rows = batchInsert(list, ignoreErrors, shouldReturnGeneratedValues) {
      entry -> assign(this, entry)
    }
    return rows
  }

  public fun OAuth2ClientTable.update(
    raw: OAuth2Client,
    selective: Array<out Column<*>>? = null,
    ignore: Array<out Column<*>>? = null,
    limit: Int? = null,
    `where`: SqlExpressionBuilder.() -> Op<Boolean>,
  ): Int = update(`where`, limit) {
    val ignoreColumns = ignore ?: arrayOf()
    assign(it, raw, selective = selective, *ignoreColumns)
  }

  public fun OAuth2ClientTable.selectMany(vararg selective: Column<*>, `where`: Query.() -> Unit):
      List<OAuth2Client> {
    val query = selectSlice(*selective)
    `where`.invoke(query)
    return query.toOAuth2ClientList(*selective)
  }

  public fun OAuth2ClientTable.selectOne(vararg selective: Column<*>, `where`: Query.() -> Unit):
      OAuth2Client? {
    val query = selectSlice(*selective)
    `where`.invoke(query)
    return query.firstOrNull()?.toOAuth2Client(*selective)
  }
}
