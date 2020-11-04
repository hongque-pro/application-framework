/*
 * Auto-generated file. Created by MyBatis Generator
 */
package com.labijie.application.open.data.mapper

import com.labijie.application.open.data.OpenAppRecord
import com.labijie.application.open.data.mapper.OpenAppDynamicSqlSupport.OpenApp
import com.labijie.application.open.data.mapper.OpenAppDynamicSqlSupport.OpenApp.appId
import com.labijie.application.open.data.mapper.OpenAppDynamicSqlSupport.OpenApp.appSecret
import com.labijie.application.open.data.mapper.OpenAppDynamicSqlSupport.OpenApp.appType
import com.labijie.application.open.data.mapper.OpenAppDynamicSqlSupport.OpenApp.concurrencyStamp
import com.labijie.application.open.data.mapper.OpenAppDynamicSqlSupport.OpenApp.configuration
import com.labijie.application.open.data.mapper.OpenAppDynamicSqlSupport.OpenApp.displayName
import com.labijie.application.open.data.mapper.OpenAppDynamicSqlSupport.OpenApp.jsApiDomain
import com.labijie.application.open.data.mapper.OpenAppDynamicSqlSupport.OpenApp.jsApiKey
import com.labijie.application.open.data.mapper.OpenAppDynamicSqlSupport.OpenApp.logoUrl
import com.labijie.application.open.data.mapper.OpenAppDynamicSqlSupport.OpenApp.partnerId
import com.labijie.application.open.data.mapper.OpenAppDynamicSqlSupport.OpenApp.signAlgorithm
import com.labijie.application.open.data.mapper.OpenAppDynamicSqlSupport.OpenApp.status
import com.labijie.application.open.data.mapper.OpenAppDynamicSqlSupport.OpenApp.timeConfigUpdated
import com.labijie.application.open.data.mapper.OpenAppDynamicSqlSupport.OpenApp.timeCreated
import org.mybatis.dynamic.sql.SqlBuilder.isEqualTo
import org.mybatis.dynamic.sql.util.kotlin.*
import org.mybatis.dynamic.sql.util.kotlin.mybatis3.*

fun OpenAppMapper.count(completer: CountCompleter) =
    countFrom(this::count, OpenApp, completer)

fun OpenAppMapper.delete(completer: DeleteCompleter) =
    deleteFrom(this::delete, OpenApp, completer)

fun OpenAppMapper.deleteByPrimaryKey(appId_: Long) =
    delete {
        where(appId, isEqualTo(appId_))
    }

fun OpenAppMapper.insert(record: OpenAppRecord) =
    insert(this::insert, record, OpenApp) {
        map(appId).toProperty("appId")
        map(displayName).toProperty("displayName")
        map(appSecret).toProperty("appSecret")
        map(appType).toProperty("appType")
        map(signAlgorithm).toProperty("signAlgorithm")
        map(jsApiKey).toProperty("jsApiKey")
        map(jsApiDomain).toProperty("jsApiDomain")
        map(logoUrl).toProperty("logoUrl")
        map(status).toProperty("status")
        map(partnerId).toProperty("partnerId")
        map(timeCreated).toProperty("timeCreated")
        map(timeConfigUpdated).toProperty("timeConfigUpdated")
        map(concurrencyStamp).toProperty("concurrencyStamp")
        map(configuration).toProperty("configuration")
    }

fun OpenAppMapper.insertMultiple(records: Collection<OpenAppRecord>) =
    insertMultiple(this::insertMultiple, records, OpenApp) {
        map(appId).toProperty("appId")
        map(displayName).toProperty("displayName")
        map(appSecret).toProperty("appSecret")
        map(appType).toProperty("appType")
        map(signAlgorithm).toProperty("signAlgorithm")
        map(jsApiKey).toProperty("jsApiKey")
        map(jsApiDomain).toProperty("jsApiDomain")
        map(logoUrl).toProperty("logoUrl")
        map(status).toProperty("status")
        map(partnerId).toProperty("partnerId")
        map(timeCreated).toProperty("timeCreated")
        map(timeConfigUpdated).toProperty("timeConfigUpdated")
        map(concurrencyStamp).toProperty("concurrencyStamp")
        map(configuration).toProperty("configuration")
    }

fun OpenAppMapper.insertMultiple(vararg records: OpenAppRecord) =
    insertMultiple(records.toList())

fun OpenAppMapper.insertSelective(record: OpenAppRecord) =
    insert(this::insert, record, OpenApp) {
        map(appId).toPropertyWhenPresent("appId", record::appId)
        map(displayName).toPropertyWhenPresent("displayName", record::displayName)
        map(appSecret).toPropertyWhenPresent("appSecret", record::appSecret)
        map(appType).toPropertyWhenPresent("appType", record::appType)
        map(signAlgorithm).toPropertyWhenPresent("signAlgorithm", record::signAlgorithm)
        map(jsApiKey).toPropertyWhenPresent("jsApiKey", record::jsApiKey)
        map(jsApiDomain).toPropertyWhenPresent("jsApiDomain", record::jsApiDomain)
        map(logoUrl).toPropertyWhenPresent("logoUrl", record::logoUrl)
        map(status).toPropertyWhenPresent("status", record::status)
        map(partnerId).toPropertyWhenPresent("partnerId", record::partnerId)
        map(timeCreated).toPropertyWhenPresent("timeCreated", record::timeCreated)
        map(timeConfigUpdated).toPropertyWhenPresent("timeConfigUpdated", record::timeConfigUpdated)
        map(concurrencyStamp).toPropertyWhenPresent("concurrencyStamp", record::concurrencyStamp)
        map(configuration).toPropertyWhenPresent("configuration", record::configuration)
    }

private val columnList = listOf(appId, displayName, appSecret, appType, signAlgorithm, jsApiKey, jsApiDomain, logoUrl, status, partnerId, timeCreated, timeConfigUpdated, concurrencyStamp, configuration)

fun OpenAppMapper.selectOne(completer: SelectCompleter) =
    selectOne(this::selectOne, columnList, OpenApp, completer)

fun OpenAppMapper.select(completer: SelectCompleter) =
    selectList(this::selectMany, columnList, OpenApp, completer)

fun OpenAppMapper.selectDistinct(completer: SelectCompleter) =
    selectDistinct(this::selectMany, columnList, OpenApp, completer)

fun OpenAppMapper.selectByPrimaryKey(appId_: Long) =
    selectOne {
        where(appId, isEqualTo(appId_))
    }

fun OpenAppMapper.update(completer: UpdateCompleter) =
    update(this::update, OpenApp, completer)

fun KotlinUpdateBuilder.updateAllColumns(record: OpenAppRecord) =
    apply {
        set(appId).equalTo(record::appId)
        set(displayName).equalTo(record::displayName)
        set(appSecret).equalTo(record::appSecret)
        set(appType).equalTo(record::appType)
        set(signAlgorithm).equalTo(record::signAlgorithm)
        set(jsApiKey).equalTo(record::jsApiKey)
        set(jsApiDomain).equalTo(record::jsApiDomain)
        set(logoUrl).equalTo(record::logoUrl)
        set(status).equalTo(record::status)
        set(partnerId).equalTo(record::partnerId)
        set(timeCreated).equalTo(record::timeCreated)
        set(timeConfigUpdated).equalTo(record::timeConfigUpdated)
        set(concurrencyStamp).equalTo(record::concurrencyStamp)
        set(configuration).equalTo(record::configuration)
    }

fun KotlinUpdateBuilder.updateSelectiveColumns(record: OpenAppRecord) =
    apply {
        set(appId).equalToWhenPresent(record::appId)
        set(displayName).equalToWhenPresent(record::displayName)
        set(appSecret).equalToWhenPresent(record::appSecret)
        set(appType).equalToWhenPresent(record::appType)
        set(signAlgorithm).equalToWhenPresent(record::signAlgorithm)
        set(jsApiKey).equalToWhenPresent(record::jsApiKey)
        set(jsApiDomain).equalToWhenPresent(record::jsApiDomain)
        set(logoUrl).equalToWhenPresent(record::logoUrl)
        set(status).equalToWhenPresent(record::status)
        set(partnerId).equalToWhenPresent(record::partnerId)
        set(timeCreated).equalToWhenPresent(record::timeCreated)
        set(timeConfigUpdated).equalToWhenPresent(record::timeConfigUpdated)
        set(concurrencyStamp).equalToWhenPresent(record::concurrencyStamp)
        set(configuration).equalToWhenPresent(record::configuration)
    }

fun OpenAppMapper.updateByPrimaryKey(record: OpenAppRecord) =
    update {
        set(displayName).equalTo(record::displayName)
        set(appSecret).equalTo(record::appSecret)
        set(appType).equalTo(record::appType)
        set(signAlgorithm).equalTo(record::signAlgorithm)
        set(jsApiKey).equalTo(record::jsApiKey)
        set(jsApiDomain).equalTo(record::jsApiDomain)
        set(logoUrl).equalTo(record::logoUrl)
        set(status).equalTo(record::status)
        set(partnerId).equalTo(record::partnerId)
        set(timeCreated).equalTo(record::timeCreated)
        set(timeConfigUpdated).equalTo(record::timeConfigUpdated)
        set(concurrencyStamp).equalTo(record::concurrencyStamp)
        set(configuration).equalTo(record::configuration)
        where(appId, isEqualTo(record::appId))
    }

fun OpenAppMapper.updateByPrimaryKeySelective(record: OpenAppRecord) =
    update {
        set(displayName).equalToWhenPresent(record::displayName)
        set(appSecret).equalToWhenPresent(record::appSecret)
        set(appType).equalToWhenPresent(record::appType)
        set(signAlgorithm).equalToWhenPresent(record::signAlgorithm)
        set(jsApiKey).equalToWhenPresent(record::jsApiKey)
        set(jsApiDomain).equalToWhenPresent(record::jsApiDomain)
        set(logoUrl).equalToWhenPresent(record::logoUrl)
        set(status).equalToWhenPresent(record::status)
        set(partnerId).equalToWhenPresent(record::partnerId)
        set(timeCreated).equalToWhenPresent(record::timeCreated)
        set(timeConfigUpdated).equalToWhenPresent(record::timeConfigUpdated)
        set(concurrencyStamp).equalToWhenPresent(record::concurrencyStamp)
        set(configuration).equalToWhenPresent(record::configuration)
        where(appId, isEqualTo(record::appId))
    }