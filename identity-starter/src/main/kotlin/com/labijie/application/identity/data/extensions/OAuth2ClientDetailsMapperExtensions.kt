/*
 * Auto-generated file. Created by MyBatis Generator
 */
package com.labijie.application.identity.data.extensions

import com.labijie.application.identity.DynamicTableSupport
import com.labijie.application.identity.data.OAuth2ClientDetailsRecord
import com.labijie.application.identity.data.mapper.OAuth2ClientDetailsDynamicSqlSupport.OAuth2ClientDetails
import com.labijie.application.identity.data.mapper.OAuth2ClientDetailsDynamicSqlSupport.OAuth2ClientDetails.accessTokenValidity
import com.labijie.application.identity.data.mapper.OAuth2ClientDetailsDynamicSqlSupport.OAuth2ClientDetails.additionalInformation
import com.labijie.application.identity.data.mapper.OAuth2ClientDetailsDynamicSqlSupport.OAuth2ClientDetails.authorities
import com.labijie.application.identity.data.mapper.OAuth2ClientDetailsDynamicSqlSupport.OAuth2ClientDetails.authorizedGrantTypes
import com.labijie.application.identity.data.mapper.OAuth2ClientDetailsDynamicSqlSupport.OAuth2ClientDetails.autoapprove
import com.labijie.application.identity.data.mapper.OAuth2ClientDetailsDynamicSqlSupport.OAuth2ClientDetails.clientId
import com.labijie.application.identity.data.mapper.OAuth2ClientDetailsDynamicSqlSupport.OAuth2ClientDetails.clientSecret
import com.labijie.application.identity.data.mapper.OAuth2ClientDetailsDynamicSqlSupport.OAuth2ClientDetails.enabled
import com.labijie.application.identity.data.mapper.OAuth2ClientDetailsDynamicSqlSupport.OAuth2ClientDetails.refreshTokenValidity
import com.labijie.application.identity.data.mapper.OAuth2ClientDetailsDynamicSqlSupport.OAuth2ClientDetails.resourceIds
import com.labijie.application.identity.data.mapper.OAuth2ClientDetailsDynamicSqlSupport.OAuth2ClientDetails.scope
import com.labijie.application.identity.data.mapper.OAuth2ClientDetailsDynamicSqlSupport.OAuth2ClientDetails.webServerRedirectUri
import com.labijie.application.identity.data.mapper.OAuth2ClientDetailsMapper
import org.mybatis.dynamic.sql.SqlBuilder.isEqualTo
import org.mybatis.dynamic.sql.util.kotlin.*
import org.mybatis.dynamic.sql.util.kotlin.mybatis3.*


fun OAuth2ClientDetailsMapper.count(completer: CountCompleter) =
    countFrom(this::count, DynamicTableSupport.getTable(OAuth2ClientDetails), completer)

fun OAuth2ClientDetailsMapper.delete(completer: DeleteCompleter) =
    deleteFrom(this::delete, DynamicTableSupport.getTable(OAuth2ClientDetails), completer)

fun OAuth2ClientDetailsMapper.deleteByPrimaryKey(clientId_: String) =
    delete {
        where(clientId, isEqualTo(clientId_))
    }

fun OAuth2ClientDetailsMapper.insert(record: OAuth2ClientDetailsRecord) =
    insert(this::insert, record, DynamicTableSupport.getTable(OAuth2ClientDetails)) {
        map(clientId).toProperty("clientId")
        map(resourceIds).toProperty("resourceIds")
        map(clientSecret).toProperty("clientSecret")
        map(scope).toProperty("scope")
        map(authorizedGrantTypes).toProperty("authorizedGrantTypes")
        map(webServerRedirectUri).toProperty("webServerRedirectUri")
        map(authorities).toProperty("authorities")
        map(accessTokenValidity).toProperty("accessTokenValidity")
        map(refreshTokenValidity).toProperty("refreshTokenValidity")
        map(additionalInformation).toProperty("additionalInformation")
        map(autoapprove).toProperty("autoapprove")
        map(enabled).toProperty("enabled")
    }

fun OAuth2ClientDetailsMapper.insertMultiple(records: Collection<OAuth2ClientDetailsRecord>) =
    insertMultiple(this::insertMultiple, records, DynamicTableSupport.getTable(OAuth2ClientDetails)) {
        map(clientId).toProperty("clientId")
        map(resourceIds).toProperty("resourceIds")
        map(clientSecret).toProperty("clientSecret")
        map(scope).toProperty("scope")
        map(authorizedGrantTypes).toProperty("authorizedGrantTypes")
        map(webServerRedirectUri).toProperty("webServerRedirectUri")
        map(authorities).toProperty("authorities")
        map(accessTokenValidity).toProperty("accessTokenValidity")
        map(refreshTokenValidity).toProperty("refreshTokenValidity")
        map(additionalInformation).toProperty("additionalInformation")
        map(autoapprove).toProperty("autoapprove")
        map(enabled).toProperty("enabled")
    }

fun OAuth2ClientDetailsMapper.insertMultiple(vararg records: OAuth2ClientDetailsRecord) =
    insertMultiple(records.toList())

fun OAuth2ClientDetailsMapper.insertSelective(record: OAuth2ClientDetailsRecord) =
    insert(this::insert, record, DynamicTableSupport.getTable(OAuth2ClientDetails)) {
        map(clientId).toPropertyWhenPresent("clientId", record::clientId)
        map(resourceIds).toPropertyWhenPresent("resourceIds", record::resourceIds)
        map(clientSecret).toPropertyWhenPresent("clientSecret", record::clientSecret)
        map(scope).toPropertyWhenPresent("scope", record::scope)
        map(authorizedGrantTypes).toPropertyWhenPresent("authorizedGrantTypes", record::authorizedGrantTypes)
        map(webServerRedirectUri).toPropertyWhenPresent("webServerRedirectUri", record::webServerRedirectUri)
        map(authorities).toPropertyWhenPresent("authorities", record::authorities)
        map(accessTokenValidity).toPropertyWhenPresent("accessTokenValidity", record::accessTokenValidity)
        map(refreshTokenValidity).toPropertyWhenPresent("refreshTokenValidity", record::refreshTokenValidity)
        map(additionalInformation).toPropertyWhenPresent("additionalInformation", record::additionalInformation)
        map(autoapprove).toPropertyWhenPresent("autoapprove", record::autoapprove)
        map(enabled).toPropertyWhenPresent("enabled", record::enabled)
    }

private val columnList = listOf(clientId, resourceIds, clientSecret, scope, authorizedGrantTypes, webServerRedirectUri, authorities, accessTokenValidity, refreshTokenValidity, additionalInformation, autoapprove, enabled)

fun OAuth2ClientDetailsMapper.selectOne(completer: SelectCompleter) =
    selectOne(this::selectOne,
        columnList, DynamicTableSupport.getTable(OAuth2ClientDetails), completer)

fun OAuth2ClientDetailsMapper.select(completer: SelectCompleter) =
    selectList(this::selectMany,
        columnList, DynamicTableSupport.getTable(OAuth2ClientDetails), completer)

fun OAuth2ClientDetailsMapper.selectDistinct(completer: SelectCompleter) =
    selectDistinct(this::selectMany,
        columnList, DynamicTableSupport.getTable(OAuth2ClientDetails), completer)

fun OAuth2ClientDetailsMapper.selectByPrimaryKey(clientId_: String) =
    selectOne {
        where(clientId, isEqualTo(clientId_))
    }

fun OAuth2ClientDetailsMapper.update(completer: UpdateCompleter) =
    update(this::update, DynamicTableSupport.getTable(OAuth2ClientDetails), completer)

fun KotlinUpdateBuilder.updateAllColumns(record: OAuth2ClientDetailsRecord) =
    apply {
        set(clientId).equalTo(record::clientId)
        set(resourceIds).equalTo(record::resourceIds)
        set(clientSecret).equalTo(record::clientSecret)
        set(scope).equalTo(record::scope)
        set(authorizedGrantTypes).equalTo(record::authorizedGrantTypes)
        set(webServerRedirectUri).equalTo(record::webServerRedirectUri)
        set(authorities).equalTo(record::authorities)
        set(accessTokenValidity).equalTo(record::accessTokenValidity)
        set(refreshTokenValidity).equalTo(record::refreshTokenValidity)
        set(additionalInformation).equalTo(record::additionalInformation)
        set(autoapprove).equalTo(record::autoapprove)
        set(enabled).equalTo(record::enabled)
    }

fun KotlinUpdateBuilder.updateSelectiveColumns(record: OAuth2ClientDetailsRecord) =
    apply {
        set(clientId).equalToWhenPresent(record::clientId)
        set(resourceIds).equalToWhenPresent(record::resourceIds)
        set(clientSecret).equalToWhenPresent(record::clientSecret)
        set(scope).equalToWhenPresent(record::scope)
        set(authorizedGrantTypes).equalToWhenPresent(record::authorizedGrantTypes)
        set(webServerRedirectUri).equalToWhenPresent(record::webServerRedirectUri)
        set(authorities).equalToWhenPresent(record::authorities)
        set(accessTokenValidity).equalToWhenPresent(record::accessTokenValidity)
        set(refreshTokenValidity).equalToWhenPresent(record::refreshTokenValidity)
        set(additionalInformation).equalToWhenPresent(record::additionalInformation)
        set(autoapprove).equalToWhenPresent(record::autoapprove)
        set(enabled).equalToWhenPresent(record::enabled)
    }

fun OAuth2ClientDetailsMapper.updateByPrimaryKey(record: OAuth2ClientDetailsRecord) =
    update {
        set(resourceIds).equalTo(record::resourceIds)
        set(clientSecret).equalTo(record::clientSecret)
        set(scope).equalTo(record::scope)
        set(authorizedGrantTypes).equalTo(record::authorizedGrantTypes)
        set(webServerRedirectUri).equalTo(record::webServerRedirectUri)
        set(authorities).equalTo(record::authorities)
        set(accessTokenValidity).equalTo(record::accessTokenValidity)
        set(refreshTokenValidity).equalTo(record::refreshTokenValidity)
        set(additionalInformation).equalTo(record::additionalInformation)
        set(autoapprove).equalTo(record::autoapprove)
        set(enabled).equalTo(record::enabled)
        where(clientId, isEqualTo(record::clientId))
    }

fun OAuth2ClientDetailsMapper.updateByPrimaryKeySelective(record: OAuth2ClientDetailsRecord) =
    update {
        set(resourceIds).equalToWhenPresent(record::resourceIds)
        set(clientSecret).equalToWhenPresent(record::clientSecret)
        set(scope).equalToWhenPresent(record::scope)
        set(authorizedGrantTypes).equalToWhenPresent(record::authorizedGrantTypes)
        set(webServerRedirectUri).equalToWhenPresent(record::webServerRedirectUri)
        set(authorities).equalToWhenPresent(record::authorities)
        set(accessTokenValidity).equalToWhenPresent(record::accessTokenValidity)
        set(refreshTokenValidity).equalToWhenPresent(record::refreshTokenValidity)
        set(additionalInformation).equalToWhenPresent(record::additionalInformation)
        set(autoapprove).equalToWhenPresent(record::autoapprove)
        set(enabled).equalToWhenPresent(record::enabled)
        where(clientId, isEqualTo(record::clientId))
    }