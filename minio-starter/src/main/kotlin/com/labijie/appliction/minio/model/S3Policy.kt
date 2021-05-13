package com.labijie.appliction.minio.model

import com.fasterxml.jackson.annotation.JsonProperty
import com.labijie.infra.json.JacksonHelper

class S3Policy private constructor(buckets: List<String>, policy: List<S3PolicyStatement>? = null){

    private constructor(bucket: String, policy: List<S3PolicyStatement>? = null): this(listOf(bucket), policy)

    private constructor(bucket: String, policy: S3PolicyStatement? = null): this(listOf(bucket), policy?.let { listOf(it) })

    /*
    //TODO:目前并不支持创建非匿名用户的策略：
    https://github.com/minio/minio/issues/9530
     */

    companion object {
        const val POLICY_EFFECT_ALLOW = "Allow"
        //const val POLICY_EFFECT_DENY = "Deny"

        fun makeReadWrite(vararg buckets: String) : S3Policy {
           return S3Policy(buckets.toList()).apply {
               this.statement.forEach {
                   it.action = listOf("s3:GetObject", "s3:PutObject")
               }
           }
        }

        fun makeReadonly(bucket: String, includePrincipal: Boolean = true): S3Policy {
            val getStatement = if(includePrincipal) S3PolicyStatementWithPrincipal(bucket) else S3PolicyStatement(bucket)
            return S3Policy(bucket, getStatement)
        }
    }

    @get:JsonProperty("Version")
    val version = "2012-10-17"

    @get:JsonProperty("Statement")
    var statement = policy ?: listOf(S3PolicyStatement(buckets))


    override fun toString(): String = JacksonHelper.serializeAsString(this)

    fun toPrettyString(): String = JacksonHelper.defaultObjectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(this)

}