package com.labijie.application.open.service.impl

import com.labijie.application.ForwardList
import com.labijie.application.configure
import com.labijie.application.crypto.HashUtils
import com.labijie.application.open.OpenSignatureUtils
import com.labijie.application.open.data.OpenAppTable
import com.labijie.application.open.data.OpenPartnerTable
import com.labijie.application.open.data.pojo.OpenApp
import com.labijie.application.open.data.pojo.dsl.OpenAppDSL.insert
import com.labijie.application.open.data.pojo.dsl.OpenAppDSL.selectByPrimaryKey
import com.labijie.application.open.data.pojo.dsl.OpenAppDSL.selectMany
import com.labijie.application.open.data.pojo.dsl.OpenAppDSL.selectOne
import com.labijie.application.open.data.pojo.dsl.OpenAppDSL.toOpenAppList
import com.labijie.application.open.data.pojo.dsl.OpenPartnerDSL.selectOne
import com.labijie.application.open.exception.PartnerNotFoundException
import com.labijie.application.open.model.AlgorithmAndKey
import com.labijie.application.open.model.OpenAppCreation
import com.labijie.application.open.model.OpenAppEntry
import com.labijie.application.open.model.OpenAppStatus
import com.labijie.application.open.service.IOpenAppService
import com.labijie.application.orDefault
import com.labijie.application.propertiesFrom
import com.labijie.infra.IIdGenerator
import com.labijie.infra.json.JacksonHelper
import com.labijie.infra.utils.ShortId
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.SqlExpressionBuilder.plus
import org.jetbrains.exposed.sql.andWhere
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.update
import org.springframework.transaction.support.TransactionTemplate
import java.util.*


class OpenAppService(
    private val idGenerator: IIdGenerator,
    private val transactionTemplate: TransactionTemplate,
) : IOpenAppService {


    override fun createApp(creation: OpenAppCreation, partnerId: Long): OpenApp {

        transactionTemplate.configure(isReadOnly = true).execute {
            OpenPartnerTable.selectOne(OpenPartnerTable.id) {
                andWhere { OpenPartnerTable.id eq partnerId }
            }
        } ?: throw PartnerNotFoundException()

        val appId = idGenerator.newId()
        val app = OpenApp().propertiesFrom(creation).apply {
            this.id = appId
            this.signAlgorithm = "sha256"
            this.appSecret = HashUtils.genHmacSha256Key()
            this.appType = 0
            this.concurrencyStamp = ShortId.newId()
            this.configuration = "{}"
            this.jsApiDomain = "127.0.0.1"
            this.jsApiKey = UUID.randomUUID().toString().replace("-", "")
            this.logoUrl = ""
            this.partnerId = partnerId
            this.status = OpenAppStatus.NORMAL
            this.timeConfigUpdated = System.currentTimeMillis()
            this.timeCreated = System.currentTimeMillis()
        }


        transactionTemplate.execute {
            OpenAppTable.insert(app)

            OpenPartnerTable.update({ OpenPartnerTable.id eq partnerId }) {
                it[appCount] = appCount + 1
            }
        }
        return OpenApp().propertiesFrom(app)
    }

    override fun getByJsApiKey(key: String): OpenApp? {
        if (key.isBlank()) {
            return null
        }
        val record = OpenAppTable.selectOne {
            andWhere { OpenAppTable.appSecret eq key }
        }
        return record
    }

    override fun getByAppId(appId: Long): OpenApp? {
        if (appId <= 0) {
            return null
        }
        return OpenAppTable.selectByPrimaryKey(appId)
    }

    override fun renewSecret(appId: Long, signAlgorithm: String): String? {
        val key = OpenSignatureUtils.generateKey(signAlgorithm)
        val count = this.transactionTemplate.execute {
            OpenAppTable.update({ OpenPartnerTable.id eq appId }) {
                it[appSecret] = key
                it[OpenAppTable.signAlgorithm] = signAlgorithm.lowercase()
            }
        }
        if (count.orDefault(0) <= 0) {
            return null
        }
        return key
    }

    override fun getSecret(appId: Long): AlgorithmAndKey? {
        val app = OpenAppTable.selectByPrimaryKey(appId, OpenAppTable.appSecret, OpenAppTable.signAlgorithm) ?: return null
        return AlgorithmAndKey(app.signAlgorithm, app.appSecret)
    }

    override fun setAppStatus(appId: Long, status: OpenAppStatus): Boolean {
        val count = transactionTemplate.execute {
            OpenAppTable.update({ OpenAppTable.id eq appId }) {
                it[OpenAppTable.status] = status
            }
        }
        return (count ?: 0) > 0
    }

    override fun setAppConfiguration(appId: Long, configuration: Map<String, String>): Boolean {
        val json = JacksonHelper.serializeAsString(configuration)
        val count = transactionTemplate.execute {
            OpenAppTable.update({ OpenAppTable.id eq appId }) {
                it[OpenAppTable.configuration] = json
            }
        }
        return (count ?: 0) > 0
    }

    override fun listApps(pageSize: Int, forwardToken: String?): ForwardList<OpenAppEntry> {
        val size = pageSize.coerceAtMost(100)
        val offset = forwardToken?.toLongOrNull() ?: 0
        val query = OpenAppTable.selectAll()
        if(offset > 0){
            query.andWhere { OpenAppTable.id less offset }
        }
        query.orderBy(OpenAppTable.id to SortOrder.DESC).limit(size)

        val data = query.toOpenAppList()
        return ForwardList(data.map { OpenAppEntry().propertiesFrom(it) }, data.lastOrNull()?.id?.toString())
    }

    override fun listApps(partnerId: Long): List<OpenAppEntry> {
        if (partnerId <= 0) {
            return listOf()
        }
        val list = OpenAppTable.selectMany {
            andWhere { OpenAppTable.partnerId eq partnerId }
            orderBy(OpenAppTable.id to SortOrder.DESC)
        }
        return list.map { OpenAppEntry().propertiesFrom(it) }
    }
}