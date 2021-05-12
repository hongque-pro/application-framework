package com.labijie.appliction.minio.model

import com.fasterxml.jackson.annotation.JsonProperty
import com.labijie.infra.json.JacksonHelper

class S3Policy private constructor(buckets: List<String>, policy: List<S3PolicyStatement>? = null){
    /*
    //TODO:目前并不支持创建非匿名用户的策略：
    https://github.com/minio/minio/issues/9530
     */

    companion object {
        fun make(vararg buckets: String) = S3Policy(buckets.toList())

        const val POLICY_EFFECT_ALLOW = "Allow"
        const val POLICY_EFFECT_DENY = "Deny"


        fun makePrivate(bucket: String): S3Policy {
            val statement: S3PolicyStatement = S3PolicyStatementWithPrincipal(bucket).apply {
                this.action = listOf("s3:*")
                this.effect = POLICY_EFFECT_DENY
            }

            return S3Policy(listOf(bucket), listOf(statement))
        }

        fun makePublic(bucket: String): S3Policy {
            val statement: S3PolicyStatement = S3PolicyStatementWithPrincipal(bucket).apply {
                this.action = listOf("s3:GetObject")
            }

            return S3Policy(listOf(bucket), listOf(statement))
        }
    }

    @get:JsonProperty("Version")
    val version = "2012-10-17"

    @get:JsonProperty("Statement")
    var statement = policy ?: listOf(S3PolicyStatement(buckets))


    override fun toString(): String = JacksonHelper.serializeAsString(this)

    fun toPrettyString(): String = JacksonHelper.defaultObjectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(this)

}