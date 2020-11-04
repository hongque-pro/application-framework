/*
 * Auto-generated file. Created by MyBatis Generator
 */
package com.labijie.application.open.data.mapper

import com.labijie.application.open.data.OpenPartnerRecord
import com.labijie.application.open.data.mapper.OpenPartnerDynamicSqlSupport.OpenPartner
import com.labijie.application.open.data.mapper.OpenPartnerDynamicSqlSupport.OpenPartner.appCount
import com.labijie.application.open.data.mapper.OpenPartnerDynamicSqlSupport.OpenPartner.contact
import com.labijie.application.open.data.mapper.OpenPartnerDynamicSqlSupport.OpenPartner.email
import com.labijie.application.open.data.mapper.OpenPartnerDynamicSqlSupport.OpenPartner.id
import com.labijie.application.open.data.mapper.OpenPartnerDynamicSqlSupport.OpenPartner.name
import com.labijie.application.open.data.mapper.OpenPartnerDynamicSqlSupport.OpenPartner.phoneNumber
import com.labijie.application.open.data.mapper.OpenPartnerDynamicSqlSupport.OpenPartner.status
import com.labijie.application.open.data.mapper.OpenPartnerDynamicSqlSupport.OpenPartner.timeExpired
import com.labijie.application.open.data.mapper.OpenPartnerDynamicSqlSupport.OpenPartner.timeLatestPaid
import com.labijie.application.open.data.mapper.OpenPartnerDynamicSqlSupport.OpenPartner.timeLatestUpdated
import org.mybatis.dynamic.sql.SqlBuilder.isEqualTo
import org.mybatis.dynamic.sql.util.kotlin.*
import org.mybatis.dynamic.sql.util.kotlin.mybatis3.*

fun OpenPartnerMapper.count(completer: CountCompleter) =
    countFrom(this::count, OpenPartner, completer)

fun OpenPartnerMapper.delete(completer: DeleteCompleter) =
    deleteFrom(this::delete, OpenPartner, completer)

fun OpenPartnerMapper.deleteByPrimaryKey(id_: Long) =
    delete {
        where(id, isEqualTo(id_))
    }

fun OpenPartnerMapper.insert(record: OpenPartnerRecord) =
    insert(this::insert, record, OpenPartner) {
        map(id).toProperty("id")
        map(name).toProperty("name")
        map(timeExpired).toProperty("timeExpired")
        map(status).toProperty("status")
        map(timeLatestPaid).toProperty("timeLatestPaid")
        map(timeLatestUpdated).toProperty("timeLatestUpdated")
        map(phoneNumber).toProperty("phoneNumber")
        map(contact).toProperty("contact")
        map(email).toProperty("email")
        map(appCount).toProperty("appCount")
    }

fun OpenPartnerMapper.insertMultiple(records: Collection<OpenPartnerRecord>) =
    insertMultiple(this::insertMultiple, records, OpenPartner) {
        map(id).toProperty("id")
        map(name).toProperty("name")
        map(timeExpired).toProperty("timeExpired")
        map(status).toProperty("status")
        map(timeLatestPaid).toProperty("timeLatestPaid")
        map(timeLatestUpdated).toProperty("timeLatestUpdated")
        map(phoneNumber).toProperty("phoneNumber")
        map(contact).toProperty("contact")
        map(email).toProperty("email")
        map(appCount).toProperty("appCount")
    }

fun OpenPartnerMapper.insertMultiple(vararg records: OpenPartnerRecord) =
    insertMultiple(records.toList())

fun OpenPartnerMapper.insertSelective(record: OpenPartnerRecord) =
    insert(this::insert, record, OpenPartner) {
        map(id).toPropertyWhenPresent("id", record::id)
        map(name).toPropertyWhenPresent("name", record::name)
        map(timeExpired).toPropertyWhenPresent("timeExpired", record::timeExpired)
        map(status).toPropertyWhenPresent("status", record::status)
        map(timeLatestPaid).toPropertyWhenPresent("timeLatestPaid", record::timeLatestPaid)
        map(timeLatestUpdated).toPropertyWhenPresent("timeLatestUpdated", record::timeLatestUpdated)
        map(phoneNumber).toPropertyWhenPresent("phoneNumber", record::phoneNumber)
        map(contact).toPropertyWhenPresent("contact", record::contact)
        map(email).toPropertyWhenPresent("email", record::email)
        map(appCount).toPropertyWhenPresent("appCount", record::appCount)
    }

private val columnList = listOf(id, name, timeExpired, status, timeLatestPaid, timeLatestUpdated, phoneNumber, contact, email, appCount)

fun OpenPartnerMapper.selectOne(completer: SelectCompleter) =
    selectOne(this::selectOne, columnList, OpenPartner, completer)

fun OpenPartnerMapper.select(completer: SelectCompleter) =
    selectList(this::selectMany, columnList, OpenPartner, completer)

fun OpenPartnerMapper.selectDistinct(completer: SelectCompleter) =
    selectDistinct(this::selectMany, columnList, OpenPartner, completer)

fun OpenPartnerMapper.selectByPrimaryKey(id_: Long) =
    selectOne {
        where(id, isEqualTo(id_))
    }

fun OpenPartnerMapper.update(completer: UpdateCompleter) =
    update(this::update, OpenPartner, completer)

fun KotlinUpdateBuilder.updateAllColumns(record: OpenPartnerRecord) =
    apply {
        set(id).equalTo(record::id)
        set(name).equalTo(record::name)
        set(timeExpired).equalTo(record::timeExpired)
        set(status).equalTo(record::status)
        set(timeLatestPaid).equalTo(record::timeLatestPaid)
        set(timeLatestUpdated).equalTo(record::timeLatestUpdated)
        set(phoneNumber).equalTo(record::phoneNumber)
        set(contact).equalTo(record::contact)
        set(email).equalTo(record::email)
        set(appCount).equalTo(record::appCount)
    }

fun KotlinUpdateBuilder.updateSelectiveColumns(record: OpenPartnerRecord) =
    apply {
        set(id).equalToWhenPresent(record::id)
        set(name).equalToWhenPresent(record::name)
        set(timeExpired).equalToWhenPresent(record::timeExpired)
        set(status).equalToWhenPresent(record::status)
        set(timeLatestPaid).equalToWhenPresent(record::timeLatestPaid)
        set(timeLatestUpdated).equalToWhenPresent(record::timeLatestUpdated)
        set(phoneNumber).equalToWhenPresent(record::phoneNumber)
        set(contact).equalToWhenPresent(record::contact)
        set(email).equalToWhenPresent(record::email)
        set(appCount).equalToWhenPresent(record::appCount)
    }

fun OpenPartnerMapper.updateByPrimaryKey(record: OpenPartnerRecord) =
    update {
        set(name).equalTo(record::name)
        set(timeExpired).equalTo(record::timeExpired)
        set(status).equalTo(record::status)
        set(timeLatestPaid).equalTo(record::timeLatestPaid)
        set(timeLatestUpdated).equalTo(record::timeLatestUpdated)
        set(phoneNumber).equalTo(record::phoneNumber)
        set(contact).equalTo(record::contact)
        set(email).equalTo(record::email)
        set(appCount).equalTo(record::appCount)
        where(id, isEqualTo(record::id))
    }

fun OpenPartnerMapper.updateByPrimaryKeySelective(record: OpenPartnerRecord) =
    update {
        set(name).equalToWhenPresent(record::name)
        set(timeExpired).equalToWhenPresent(record::timeExpired)
        set(status).equalToWhenPresent(record::status)
        set(timeLatestPaid).equalToWhenPresent(record::timeLatestPaid)
        set(timeLatestUpdated).equalToWhenPresent(record::timeLatestUpdated)
        set(phoneNumber).equalToWhenPresent(record::phoneNumber)
        set(contact).equalToWhenPresent(record::contact)
        set(email).equalToWhenPresent(record::email)
        set(appCount).equalToWhenPresent(record::appCount)
        where(id, isEqualTo(record::id))
    }