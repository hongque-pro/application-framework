/*
 * Auto-generated file. Created by MyBatis Generator
 */
package com.labijie.application.identity.data.mapper

import com.labijie.application.identity.data.UserRecord
import org.apache.ibatis.annotations.DeleteProvider
import org.apache.ibatis.annotations.InsertProvider
import org.apache.ibatis.annotations.Mapper
import org.apache.ibatis.annotations.Result
import org.apache.ibatis.annotations.ResultMap
import org.apache.ibatis.annotations.Results
import org.apache.ibatis.annotations.SelectProvider
import org.apache.ibatis.annotations.UpdateProvider
import org.apache.ibatis.type.JdbcType
import org.mybatis.dynamic.sql.delete.render.DeleteStatementProvider
import org.mybatis.dynamic.sql.insert.render.InsertStatementProvider
import org.mybatis.dynamic.sql.insert.render.MultiRowInsertStatementProvider
import org.mybatis.dynamic.sql.select.render.SelectStatementProvider
import org.mybatis.dynamic.sql.update.render.UpdateStatementProvider
import org.mybatis.dynamic.sql.util.SqlProviderAdapter

@Mapper
interface UserMapper {
    @SelectProvider(type=SqlProviderAdapter::class, method="select")
    fun count(selectStatement: SelectStatementProvider): Long

    @DeleteProvider(type=SqlProviderAdapter::class, method="delete")
    fun delete(deleteStatement: DeleteStatementProvider): Int

    @InsertProvider(type=SqlProviderAdapter::class, method="insert")
    fun insert(insertStatement: InsertStatementProvider<UserRecord>): Int

    @InsertProvider(type=SqlProviderAdapter::class, method="insertMultiple")
    fun insertMultiple(multipleInsertStatement: MultiRowInsertStatementProvider<UserRecord>): Int

    @SelectProvider(type=SqlProviderAdapter::class, method="select")
    @ResultMap("UserRecordResult")
    fun selectOne(selectStatement: SelectStatementProvider): UserRecord?

    @SelectProvider(type=SqlProviderAdapter::class, method="select")
    @Results(id="UserRecordResult", value = [
        Result(column="id", property="id", jdbcType=JdbcType.BIGINT, id=true),
        Result(column="user_type", property="userType", jdbcType=JdbcType.TINYINT),
        Result(column="access_failed_count", property="accessFailedCount", jdbcType=JdbcType.INTEGER),
        Result(column="concurrency_stamp", property="concurrencyStamp", jdbcType=JdbcType.VARCHAR),
        Result(column="email", property="email", jdbcType=JdbcType.VARCHAR),
        Result(column="email_confirmed", property="emailConfirmed", jdbcType=JdbcType.BIT),
        Result(column="language", property="language", jdbcType=JdbcType.VARCHAR),
        Result(column="lockout_enabled", property="lockoutEnabled", jdbcType=JdbcType.BIT),
        Result(column="lockout_end", property="lockoutEnd", jdbcType=JdbcType.BIGINT),
        Result(column="password_hash", property="passwordHash", jdbcType=JdbcType.VARCHAR),
        Result(column="phone_number", property="phoneNumber", jdbcType=JdbcType.VARCHAR),
        Result(column="phone_number_confirmed", property="phoneNumberConfirmed", jdbcType=JdbcType.BIT),
        Result(column="security_stamp", property="securityStamp", jdbcType=JdbcType.VARCHAR),
        Result(column="time_zone", property="timeZone", jdbcType=JdbcType.VARCHAR),
        Result(column="two_factor_enabled", property="twoFactorEnabled", jdbcType=JdbcType.BIT),
        Result(column="user_name", property="userName", jdbcType=JdbcType.VARCHAR),
        Result(column="approved", property="approved", jdbcType=JdbcType.BIT),
        Result(column="approver_id", property="approverId", jdbcType=JdbcType.BIGINT),
        Result(column="time_expired", property="timeExpired", jdbcType=JdbcType.BIGINT),
        Result(column="time_last_login", property="timeLastLogin", jdbcType=JdbcType.BIGINT),
        Result(column="time_last_activity", property="timeLastActivity", jdbcType=JdbcType.BIGINT),
        Result(column="time_created", property="timeCreated", jdbcType=JdbcType.BIGINT),
        Result(column="last_sign_in_ip", property="lastSignInIp", jdbcType=JdbcType.VARCHAR),
        Result(column="last_sign_in_platform", property="lastSignInPlatform", jdbcType=JdbcType.VARCHAR),
        Result(column="last_sign_in_area", property="lastSignInArea", jdbcType=JdbcType.VARCHAR),
        Result(column="last_client_version", property="lastClientVersion", jdbcType=JdbcType.VARCHAR),
        Result(column="member_id", property="memberId", jdbcType=JdbcType.BIGINT)
    ])
    fun selectMany(selectStatement: SelectStatementProvider): List<UserRecord>

    @UpdateProvider(type=SqlProviderAdapter::class, method="update")
    fun update(updateStatement: UpdateStatementProvider): Int
}