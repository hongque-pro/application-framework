package com.labijie.application.open.service.impl

import com.labijie.application.ForwardList
import com.labijie.application.crypto.HashUtils
import com.labijie.application.mybatis.equalToColumnPlus
import com.labijie.application.open.OpenSignatureUtils
import com.labijie.application.open.data.OpenAppRecord
import com.labijie.application.open.data.mapper.*
import com.labijie.application.open.exception.PartnerNotFoundException
import com.labijie.application.open.model.*
import com.labijie.application.open.service.IOpenAppService
import com.labijie.application.orDefault
import com.labijie.application.propertiesFrom
import com.labijie.infra.IIdGenerator
import com.labijie.infra.json.JacksonHelper
import com.labijie.infra.utils.ShortId
import com.labijie.infra.utils.deserializeMap
import org.mybatis.dynamic.sql.SqlBuilder
import org.mybatis.dynamic.sql.render.RenderingStrategies
import org.mybatis.dynamic.sql.update.render.UpdateStatementProvider
import org.springframework.transaction.support.TransactionTemplate
import java.util.*


class OpenAppService(
    private val idGenerator: IIdGenerator,
    private val transactionTemplate: TransactionTemplate,
    private val partnerMapper: OpenPartnerMapper,
    private val appMapper: OpenAppMapper
) : IOpenAppService {

    companion object {
        fun OpenAppRecord.mapToOpenApp(): OpenApp {
            return OpenApp().propertiesFrom(this).also {
                val data = this.configuration!!.toByteArray(Charsets.UTF_8)
                it.config = JacksonHelper.defaultObjectMapper.deserializeMap(data, String::class, String::class)
            }
        }

        fun OpenApp.mapToRecord(): OpenAppRecord {
            return OpenAppRecord().propertiesFrom(this).also {
                it.configuration = JacksonHelper.serializeAsString(this.config)
            }
        }
    }

    override fun createApp(creation: OpenAppCreation, partnerId: Long): OpenApp {
        val selector = SqlBuilder.select(
            OpenPartnerDynamicSqlSupport.OpenPartner.id
        )
            .from(OpenPartnerDynamicSqlSupport.OpenPartner)
            .where().and(OpenPartnerDynamicSqlSupport.OpenPartner.id, SqlBuilder.isEqualTo(partnerId))
            .build()
            .render(RenderingStrategies.MYBATIS3)

        partnerMapper.selectOne(selector) ?: throw PartnerNotFoundException()

        val appId = idGenerator.newId()
        val app = OpenAppRecord().propertiesFrom(creation).apply {
            this.appId = appId
            this.signAlgorithm = "sha256"
            this.appSecret = HashUtils.genHmacSha256Key()
            this.appType = 0
            this.concurrencyStamp = ShortId.newId()
            this.configuration = "{}"
            this.jsApiDomain = "127.0.0.1"
            this.jsApiKey = UUID.randomUUID().toString().replace("-", "")
            this.logoUrl = ""
            this.partnerId = partnerId
            this.status = OpenAppStatus.NORMAL.code
            this.timeConfigUpdated = System.currentTimeMillis()
            this.timeCreated = System.currentTimeMillis()
        }

        val appAccountColumn = OpenPartnerDynamicSqlSupport.OpenPartner.appCount
        val partnerIdColumn = OpenPartnerDynamicSqlSupport.OpenPartner.id
        val updateStatement: UpdateStatementProvider = SqlBuilder.update(OpenPartnerDynamicSqlSupport.OpenPartner)
            .set(appAccountColumn).equalToColumnPlus(appAccountColumn, 1)
            .where(partnerIdColumn, SqlBuilder.isEqualTo(partnerId))
            .build()
            .render(RenderingStrategies.MYBATIS3)


        transactionTemplate.execute {
            appMapper.insertSelective(app)
            partnerMapper.update(updateStatement)
        }
        return OpenApp().propertiesFrom(app)
    }

    override fun getByJsApiKey(key: String): OpenApp? {
        if (key.isBlank()) {
            return null
        }
        val record = appMapper.selectOne {
            where(OpenAppDynamicSqlSupport.OpenApp.appSecret, SqlBuilder.isEqualTo(key))
        }
        return record?.mapToOpenApp()
    }

    override fun getByAppId(appId: Long): OpenApp? {
        if (appId <= 0) {
            return null
        }
        val record = appMapper.selectByPrimaryKey(appId)
        return record?.mapToOpenApp()
    }

    override fun renewSecret(appId: Long, signAlgorithm: String): String? {
        val key = OpenSignatureUtils.generateKey(signAlgorithm)
        val updating = OpenAppRecord(appId = appId, appSecret = key, signAlgorithm = signAlgorithm.lowercase())
        val count = this.transactionTemplate.execute {
            appMapper.updateByPrimaryKeySelective(updating)
        }
        if (count.orDefault(0) <= 0) {
            return null
        }
        return key
    }

    override fun getSecret(appId: Long): AlgorithmAndKey? {
        val select = SqlBuilder.select(
            OpenAppDynamicSqlSupport.OpenApp.appSecret,
            OpenAppDynamicSqlSupport.OpenApp.signAlgorithm
        )
            .from(OpenAppDynamicSqlSupport.OpenApp)
            .where(OpenAppDynamicSqlSupport.OpenApp.appId, SqlBuilder.isEqualTo(appId))
            .build()
            .render(RenderingStrategies.MYBATIS3)

        val app = appMapper.selectOne(select) ?: return null
        return AlgorithmAndKey(app.signAlgorithm!!, app.appSecret!!)
    }

    override fun setAppStatus(appId: Long, status: OpenAppStatus): Boolean {
        val updating = OpenAppRecord().apply {
            this.appId = appId
            this.status = status.code
        }
        val count = transactionTemplate.execute {
            appMapper.updateByPrimaryKeySelective(updating)
        }
        return (count ?: 0) > 0
    }

    override fun setAppConfiguration(appId: Long, configuration: Map<String, String>): Boolean {
        val updating = OpenAppRecord().apply {
            this.appId = appId
            this.configuration = JacksonHelper.serializeAsString(configuration)
        }
        val count = transactionTemplate.execute {
            appMapper.updateByPrimaryKeySelective(updating)
        }
        return (count ?: 0) > 0
    }

    override fun listApps(pageSize: Int, forwardToken: String?): ForwardList<OpenAppEntry> {
        val size = pageSize.coerceAtMost(100)
        val offset = forwardToken?.toLongOrNull() ?: 0
        val list = appMapper.select {
            if(offset > 0){
                this.where(OpenAppDynamicSqlSupport.OpenApp.appId, SqlBuilder.isLessThan(offset))
            }
            this.orderBy(OpenAppDynamicSqlSupport.OpenApp.appId.descending())
            this.limit(size.toLong())
        }
        val data = list.map { OpenAppEntry().propertiesFrom(it) }
        return ForwardList(data, list.lastOrNull()?.appId?.toString())
    }

    override fun listApps(partnerId: Long): List<OpenAppEntry> {
        if(partnerId <= 0){
            return listOf()
        }
        val list = appMapper.select {
            where(OpenAppDynamicSqlSupport.OpenApp.partnerId, SqlBuilder.isEqualTo(partnerId))
            orderBy(OpenAppDynamicSqlSupport.OpenApp.appId.descending())
        }
        return list.map { OpenAppEntry().propertiesFrom(it) }
    }
}