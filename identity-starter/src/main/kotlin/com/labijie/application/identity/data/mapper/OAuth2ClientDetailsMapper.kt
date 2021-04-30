/*
 * Auto-generated file. Created by MyBatis Generator
 */
package com.labijie.application.identity.data.mapper

import com.labijie.application.identity.data.OAuth2ClientDetailsRecord
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
interface OAuth2ClientDetailsMapper {
    @SelectProvider(type=SqlProviderAdapter::class, method="select")
    fun count(selectStatement: SelectStatementProvider): Long

    @DeleteProvider(type=SqlProviderAdapter::class, method="delete")
    fun delete(deleteStatement: DeleteStatementProvider): Int

    @InsertProvider(type=SqlProviderAdapter::class, method="insert")
    fun insert(insertStatement: InsertStatementProvider<OAuth2ClientDetailsRecord>): Int

    @InsertProvider(type=SqlProviderAdapter::class, method="insertMultiple")
    fun insertMultiple(multipleInsertStatement: MultiRowInsertStatementProvider<OAuth2ClientDetailsRecord>): Int

    @SelectProvider(type=SqlProviderAdapter::class, method="select")
    @ResultMap("OAuth2ClientDetailsRecordResult")
    fun selectOne(selectStatement: SelectStatementProvider): OAuth2ClientDetailsRecord?

    @SelectProvider(type=SqlProviderAdapter::class, method="select")
    @Results(id="OAuth2ClientDetailsRecordResult", value = [
        Result(column="client_id", property="clientId", jdbcType=JdbcType.VARCHAR, id=true),
        Result(column="resource_ids", property="resourceIds", jdbcType=JdbcType.VARCHAR),
        Result(column="client_secret", property="clientSecret", jdbcType=JdbcType.VARCHAR),
        Result(column="scope", property="scope", jdbcType=JdbcType.VARCHAR),
        Result(column="authorized_grant_types", property="authorizedGrantTypes", jdbcType=JdbcType.VARCHAR),
        Result(column="web_server_redirect_uri", property="webServerRedirectUri", jdbcType=JdbcType.VARCHAR),
        Result(column="authorities", property="authorities", jdbcType=JdbcType.VARCHAR),
        Result(column="access_token_validity", property="accessTokenValidity", jdbcType=JdbcType.INTEGER),
        Result(column="refresh_token_validity", property="refreshTokenValidity", jdbcType=JdbcType.INTEGER),
        Result(column="additional_information", property="additionalInformation", jdbcType=JdbcType.VARCHAR),
        Result(column="autoapprove", property="autoapprove", jdbcType=JdbcType.VARCHAR),
        Result(column="enabled", property="enabled", jdbcType=JdbcType.BIT)
    ])
    fun selectMany(selectStatement: SelectStatementProvider): List<OAuth2ClientDetailsRecord>

    @UpdateProvider(type=SqlProviderAdapter::class, method="update")
    fun update(updateStatement: UpdateStatementProvider): Int
}