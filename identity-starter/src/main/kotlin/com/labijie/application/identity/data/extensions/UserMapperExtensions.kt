/*
 * Auto-generated file. Created by MyBatis Generator
 */
package com.labijie.application.identity.data.extensions

import com.labijie.application.identity.DynamicTableSupport
import com.labijie.application.identity.data.UserRecord
import com.labijie.application.identity.data.mapper.UserDynamicSqlSupport.User
import com.labijie.application.identity.data.mapper.UserDynamicSqlSupport.User.accessFailedCount
import com.labijie.application.identity.data.mapper.UserDynamicSqlSupport.User.approved
import com.labijie.application.identity.data.mapper.UserDynamicSqlSupport.User.approverId
import com.labijie.application.identity.data.mapper.UserDynamicSqlSupport.User.concurrencyStamp
import com.labijie.application.identity.data.mapper.UserDynamicSqlSupport.User.email
import com.labijie.application.identity.data.mapper.UserDynamicSqlSupport.User.emailConfirmed
import com.labijie.application.identity.data.mapper.UserDynamicSqlSupport.User.id
import com.labijie.application.identity.data.mapper.UserDynamicSqlSupport.User.language
import com.labijie.application.identity.data.mapper.UserDynamicSqlSupport.User.lastClientVersion
import com.labijie.application.identity.data.mapper.UserDynamicSqlSupport.User.lastSignInArea
import com.labijie.application.identity.data.mapper.UserDynamicSqlSupport.User.lastSignInIp
import com.labijie.application.identity.data.mapper.UserDynamicSqlSupport.User.lastSignInPlatform
import com.labijie.application.identity.data.mapper.UserDynamicSqlSupport.User.lockoutEnabled
import com.labijie.application.identity.data.mapper.UserDynamicSqlSupport.User.lockoutEnd
import com.labijie.application.identity.data.mapper.UserDynamicSqlSupport.User.memberId
import com.labijie.application.identity.data.mapper.UserDynamicSqlSupport.User.passwordHash
import com.labijie.application.identity.data.mapper.UserDynamicSqlSupport.User.phoneNumber
import com.labijie.application.identity.data.mapper.UserDynamicSqlSupport.User.phoneNumberConfirmed
import com.labijie.application.identity.data.mapper.UserDynamicSqlSupport.User.securityStamp
import com.labijie.application.identity.data.mapper.UserDynamicSqlSupport.User.timeCreated
import com.labijie.application.identity.data.mapper.UserDynamicSqlSupport.User.timeExpired
import com.labijie.application.identity.data.mapper.UserDynamicSqlSupport.User.timeLastActivity
import com.labijie.application.identity.data.mapper.UserDynamicSqlSupport.User.timeLastLogin
import com.labijie.application.identity.data.mapper.UserDynamicSqlSupport.User.timeZone
import com.labijie.application.identity.data.mapper.UserDynamicSqlSupport.User.twoFactorEnabled
import com.labijie.application.identity.data.mapper.UserDynamicSqlSupport.User.userName
import com.labijie.application.identity.data.mapper.UserDynamicSqlSupport.User.userType
import com.labijie.application.identity.data.mapper.UserMapper
import org.mybatis.dynamic.sql.SqlBuilder.isEqualTo
import org.mybatis.dynamic.sql.SqlTable
import org.mybatis.dynamic.sql.util.kotlin.*
import org.mybatis.dynamic.sql.util.kotlin.mybatis3.*

fun UserMapper.count(completer: CountCompleter) =
    countFrom(this::count, DynamicTableSupport.getTable(User), completer)

fun UserMapper.delete(completer: DeleteCompleter) =
    deleteFrom(this::delete, DynamicTableSupport.getTable(User), completer)

fun UserMapper.deleteByPrimaryKey(id_: Long) =
    delete {
        where(id, isEqualTo(id_))
    }

fun UserMapper.insert(record: UserRecord, sqlTable: SqlTable? = null) =
    insert(this::insert, record, sqlTable ?: DynamicTableSupport.getTable(User)) {
        map(id).toProperty("id")
        map(userType).toProperty("userType")
        map(accessFailedCount).toProperty("accessFailedCount")
        map(concurrencyStamp).toProperty("concurrencyStamp")
        map(email).toProperty("email")
        map(emailConfirmed).toProperty("emailConfirmed")
        map(language).toProperty("language")
        map(lockoutEnabled).toProperty("lockoutEnabled")
        map(lockoutEnd).toProperty("lockoutEnd")
        map(passwordHash).toProperty("passwordHash")
        map(phoneNumber).toProperty("phoneNumber")
        map(phoneNumberConfirmed).toProperty("phoneNumberConfirmed")
        map(securityStamp).toProperty("securityStamp")
        map(timeZone).toProperty("timeZone")
        map(twoFactorEnabled).toProperty("twoFactorEnabled")
        map(userName).toProperty("userName")
        map(approved).toProperty("approved")
        map(approverId).toProperty("approverId")
        map(timeExpired).toProperty("timeExpired")
        map(timeLastLogin).toProperty("timeLastLogin")
        map(timeLastActivity).toProperty("timeLastActivity")
        map(timeCreated).toProperty("timeCreated")
        map(lastSignInIp).toProperty("lastSignInIp")
        map(lastSignInPlatform).toProperty("lastSignInPlatform")
        map(lastSignInArea).toProperty("lastSignInArea")
        map(lastClientVersion).toProperty("lastClientVersion")
        map(memberId).toProperty("memberId")
    }

fun UserMapper.insertMultiple(records: Collection<UserRecord>) =
    insertMultiple(this::insertMultiple, records, DynamicTableSupport.getTable(User)) {
        map(id).toProperty("id")
        map(userType).toProperty("userType")
        map(accessFailedCount).toProperty("accessFailedCount")
        map(concurrencyStamp).toProperty("concurrencyStamp")
        map(email).toProperty("email")
        map(emailConfirmed).toProperty("emailConfirmed")
        map(language).toProperty("language")
        map(lockoutEnabled).toProperty("lockoutEnabled")
        map(lockoutEnd).toProperty("lockoutEnd")
        map(passwordHash).toProperty("passwordHash")
        map(phoneNumber).toProperty("phoneNumber")
        map(phoneNumberConfirmed).toProperty("phoneNumberConfirmed")
        map(securityStamp).toProperty("securityStamp")
        map(timeZone).toProperty("timeZone")
        map(twoFactorEnabled).toProperty("twoFactorEnabled")
        map(userName).toProperty("userName")
        map(approved).toProperty("approved")
        map(approverId).toProperty("approverId")
        map(timeExpired).toProperty("timeExpired")
        map(timeLastLogin).toProperty("timeLastLogin")
        map(timeLastActivity).toProperty("timeLastActivity")
        map(timeCreated).toProperty("timeCreated")
        map(lastSignInIp).toProperty("lastSignInIp")
        map(lastSignInPlatform).toProperty("lastSignInPlatform")
        map(lastSignInArea).toProperty("lastSignInArea")
        map(lastClientVersion).toProperty("lastClientVersion")
        map(memberId).toProperty("memberId")
    }

fun UserMapper.insertMultiple(vararg records: UserRecord) =
    insertMultiple(records.toList())

fun UserMapper.insertSelective(record: UserRecord) =
    insert(this::insert, record, DynamicTableSupport.getTable(User)) {
        map(id).toPropertyWhenPresent("id", record::id)
        map(userType).toPropertyWhenPresent("userType", record::userType)
        map(accessFailedCount).toPropertyWhenPresent("accessFailedCount", record::accessFailedCount)
        map(concurrencyStamp).toPropertyWhenPresent("concurrencyStamp", record::concurrencyStamp)
        map(email).toPropertyWhenPresent("email", record::email)
        map(emailConfirmed).toPropertyWhenPresent("emailConfirmed", record::emailConfirmed)
        map(language).toPropertyWhenPresent("language", record::language)
        map(lockoutEnabled).toPropertyWhenPresent("lockoutEnabled", record::lockoutEnabled)
        map(lockoutEnd).toPropertyWhenPresent("lockoutEnd", record::lockoutEnd)
        map(passwordHash).toPropertyWhenPresent("passwordHash", record::passwordHash)
        map(phoneNumber).toPropertyWhenPresent("phoneNumber", record::phoneNumber)
        map(phoneNumberConfirmed).toPropertyWhenPresent("phoneNumberConfirmed", record::phoneNumberConfirmed)
        map(securityStamp).toPropertyWhenPresent("securityStamp", record::securityStamp)
        map(timeZone).toPropertyWhenPresent("timeZone", record::timeZone)
        map(twoFactorEnabled).toPropertyWhenPresent("twoFactorEnabled", record::twoFactorEnabled)
        map(userName).toPropertyWhenPresent("userName", record::userName)
        map(approved).toPropertyWhenPresent("approved", record::approved)
        map(approverId).toPropertyWhenPresent("approverId", record::approverId)
        map(timeExpired).toPropertyWhenPresent("timeExpired", record::timeExpired)
        map(timeLastLogin).toPropertyWhenPresent("timeLastLogin", record::timeLastLogin)
        map(timeLastActivity).toPropertyWhenPresent("timeLastActivity", record::timeLastActivity)
        map(timeCreated).toPropertyWhenPresent("timeCreated", record::timeCreated)
        map(lastSignInIp).toPropertyWhenPresent("lastSignInIp", record::lastSignInIp)
        map(lastSignInPlatform).toPropertyWhenPresent("lastSignInPlatform", record::lastSignInPlatform)
        map(lastSignInArea).toPropertyWhenPresent("lastSignInArea", record::lastSignInArea)
        map(lastClientVersion).toPropertyWhenPresent("lastClientVersion", record::lastClientVersion)
        map(memberId).toPropertyWhenPresent("memberId", record::memberId)
    }

private val columnList = listOf(id, userType, accessFailedCount, concurrencyStamp, email, emailConfirmed, language, lockoutEnabled, lockoutEnd, passwordHash, phoneNumber, phoneNumberConfirmed, securityStamp, timeZone, twoFactorEnabled, userName, approved, approverId, timeExpired, timeLastLogin, timeLastActivity, timeCreated, lastSignInIp, lastSignInPlatform, lastSignInArea, lastClientVersion, memberId)

fun UserMapper.selectOne(completer: SelectCompleter) =
    selectOne(this::selectOne,
        columnList, DynamicTableSupport.getTable(User), completer)

fun UserMapper.select(completer: SelectCompleter) =
    selectList(this::selectMany,
        columnList, DynamicTableSupport.getTable(User), completer)

fun UserMapper.selectDistinct(completer: SelectCompleter) =
    selectDistinct(this::selectMany,
        columnList, DynamicTableSupport.getTable(User), completer)

fun UserMapper.selectByPrimaryKey(id_: Long) =
    selectOne {
        where(id, isEqualTo(id_))
    }

fun UserMapper.update(completer: UpdateCompleter) =
    update(this::update, DynamicTableSupport.getTable(User), completer)

fun KotlinUpdateBuilder.updateAllColumns(record: UserRecord) =
    apply {
        set(id).equalTo(record::id)
        set(userType).equalTo(record::userType)
        set(accessFailedCount).equalTo(record::accessFailedCount)
        set(concurrencyStamp).equalTo(record::concurrencyStamp)
        set(email).equalTo(record::email)
        set(emailConfirmed).equalTo(record::emailConfirmed)
        set(language).equalTo(record::language)
        set(lockoutEnabled).equalTo(record::lockoutEnabled)
        set(lockoutEnd).equalTo(record::lockoutEnd)
        set(passwordHash).equalTo(record::passwordHash)
        set(phoneNumber).equalTo(record::phoneNumber)
        set(phoneNumberConfirmed).equalTo(record::phoneNumberConfirmed)
        set(securityStamp).equalTo(record::securityStamp)
        set(timeZone).equalTo(record::timeZone)
        set(twoFactorEnabled).equalTo(record::twoFactorEnabled)
        set(userName).equalTo(record::userName)
        set(approved).equalTo(record::approved)
        set(approverId).equalTo(record::approverId)
        set(timeExpired).equalTo(record::timeExpired)
        set(timeLastLogin).equalTo(record::timeLastLogin)
        set(timeLastActivity).equalTo(record::timeLastActivity)
        set(timeCreated).equalTo(record::timeCreated)
        set(lastSignInIp).equalTo(record::lastSignInIp)
        set(lastSignInPlatform).equalTo(record::lastSignInPlatform)
        set(lastSignInArea).equalTo(record::lastSignInArea)
        set(lastClientVersion).equalTo(record::lastClientVersion)
        set(memberId).equalTo(record::memberId)
    }

fun KotlinUpdateBuilder.updateSelectiveColumns(record: UserRecord) =
    apply {
        set(id).equalToWhenPresent(record::id)
        set(userType).equalToWhenPresent(record::userType)
        set(accessFailedCount).equalToWhenPresent(record::accessFailedCount)
        set(concurrencyStamp).equalToWhenPresent(record::concurrencyStamp)
        set(email).equalToWhenPresent(record::email)
        set(emailConfirmed).equalToWhenPresent(record::emailConfirmed)
        set(language).equalToWhenPresent(record::language)
        set(lockoutEnabled).equalToWhenPresent(record::lockoutEnabled)
        set(lockoutEnd).equalToWhenPresent(record::lockoutEnd)
        set(passwordHash).equalToWhenPresent(record::passwordHash)
        set(phoneNumber).equalToWhenPresent(record::phoneNumber)
        set(phoneNumberConfirmed).equalToWhenPresent(record::phoneNumberConfirmed)
        set(securityStamp).equalToWhenPresent(record::securityStamp)
        set(timeZone).equalToWhenPresent(record::timeZone)
        set(twoFactorEnabled).equalToWhenPresent(record::twoFactorEnabled)
        set(userName).equalToWhenPresent(record::userName)
        set(approved).equalToWhenPresent(record::approved)
        set(approverId).equalToWhenPresent(record::approverId)
        set(timeExpired).equalToWhenPresent(record::timeExpired)
        set(timeLastLogin).equalToWhenPresent(record::timeLastLogin)
        set(timeLastActivity).equalToWhenPresent(record::timeLastActivity)
        set(timeCreated).equalToWhenPresent(record::timeCreated)
        set(lastSignInIp).equalToWhenPresent(record::lastSignInIp)
        set(lastSignInPlatform).equalToWhenPresent(record::lastSignInPlatform)
        set(lastSignInArea).equalToWhenPresent(record::lastSignInArea)
        set(lastClientVersion).equalToWhenPresent(record::lastClientVersion)
        set(memberId).equalToWhenPresent(record::memberId)
    }

fun UserMapper.updateByPrimaryKey(record: UserRecord) =
    update {
        set(userType).equalTo(record::userType)
        set(accessFailedCount).equalTo(record::accessFailedCount)
        set(concurrencyStamp).equalTo(record::concurrencyStamp)
        set(email).equalTo(record::email)
        set(emailConfirmed).equalTo(record::emailConfirmed)
        set(language).equalTo(record::language)
        set(lockoutEnabled).equalTo(record::lockoutEnabled)
        set(lockoutEnd).equalTo(record::lockoutEnd)
        set(passwordHash).equalTo(record::passwordHash)
        set(phoneNumber).equalTo(record::phoneNumber)
        set(phoneNumberConfirmed).equalTo(record::phoneNumberConfirmed)
        set(securityStamp).equalTo(record::securityStamp)
        set(timeZone).equalTo(record::timeZone)
        set(twoFactorEnabled).equalTo(record::twoFactorEnabled)
        set(userName).equalTo(record::userName)
        set(approved).equalTo(record::approved)
        set(approverId).equalTo(record::approverId)
        set(timeExpired).equalTo(record::timeExpired)
        set(timeLastLogin).equalTo(record::timeLastLogin)
        set(timeLastActivity).equalTo(record::timeLastActivity)
        set(timeCreated).equalTo(record::timeCreated)
        set(lastSignInIp).equalTo(record::lastSignInIp)
        set(lastSignInPlatform).equalTo(record::lastSignInPlatform)
        set(lastSignInArea).equalTo(record::lastSignInArea)
        set(lastClientVersion).equalTo(record::lastClientVersion)
        set(memberId).equalTo(record::memberId)
        where(id, isEqualTo(record::id))
    }

fun UserMapper.updateByPrimaryKeySelective(record: UserRecord) =
    update {
        set(userType).equalToWhenPresent(record::userType)
        set(accessFailedCount).equalToWhenPresent(record::accessFailedCount)
        set(concurrencyStamp).equalToWhenPresent(record::concurrencyStamp)
        set(email).equalToWhenPresent(record::email)
        set(emailConfirmed).equalToWhenPresent(record::emailConfirmed)
        set(language).equalToWhenPresent(record::language)
        set(lockoutEnabled).equalToWhenPresent(record::lockoutEnabled)
        set(lockoutEnd).equalToWhenPresent(record::lockoutEnd)
        set(passwordHash).equalToWhenPresent(record::passwordHash)
        set(phoneNumber).equalToWhenPresent(record::phoneNumber)
        set(phoneNumberConfirmed).equalToWhenPresent(record::phoneNumberConfirmed)
        set(securityStamp).equalToWhenPresent(record::securityStamp)
        set(timeZone).equalToWhenPresent(record::timeZone)
        set(twoFactorEnabled).equalToWhenPresent(record::twoFactorEnabled)
        set(userName).equalToWhenPresent(record::userName)
        set(approved).equalToWhenPresent(record::approved)
        set(approverId).equalToWhenPresent(record::approverId)
        set(timeExpired).equalToWhenPresent(record::timeExpired)
        set(timeLastLogin).equalToWhenPresent(record::timeLastLogin)
        set(timeLastActivity).equalToWhenPresent(record::timeLastActivity)
        set(timeCreated).equalToWhenPresent(record::timeCreated)
        set(lastSignInIp).equalToWhenPresent(record::lastSignInIp)
        set(lastSignInPlatform).equalToWhenPresent(record::lastSignInPlatform)
        set(lastSignInArea).equalToWhenPresent(record::lastSignInArea)
        set(lastClientVersion).equalToWhenPresent(record::lastClientVersion)
        set(memberId).equalToWhenPresent(record::memberId)
        where(id, isEqualTo(record::id))
    }