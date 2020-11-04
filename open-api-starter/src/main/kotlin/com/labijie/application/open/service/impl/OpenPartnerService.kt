package com.labijie.application.open.service.impl

import com.labijie.application.ForwardList
import com.labijie.application.auth.service.IUserService
import com.labijie.application.exception.UserNotFoundException
import com.labijie.application.open.data.OpenPartnerRecord
import com.labijie.application.open.data.OpenPartnerUserRecord
import com.labijie.application.open.data.mapper.*
import com.labijie.application.open.model.*
import com.labijie.application.open.service.IOpenPartnerService
import com.labijie.application.orDefault
import com.labijie.application.propertiesFrom
import com.labijie.infra.IIdGenerator
import org.mybatis.dynamic.sql.SqlBuilder
import org.mybatis.dynamic.sql.render.RenderingStrategy
import org.springframework.dao.DuplicateKeyException
import org.springframework.transaction.support.TransactionTemplate

class OpenPartnerService(
    private val idGenerator: IIdGenerator,
    private val transactionTemplate: TransactionTemplate,
    private val partnerMapper: OpenPartnerMapper,
    private val partnerUserMapper: OpenPartnerUserMapper,
    private val userService: IUserService
) : IOpenPartnerService {

    override fun getById(id: Long): OpenPartner? {
        if(id <= 0){
            return null
        }
        val record = partnerMapper.selectByPrimaryKey(id)
        return if(record != null) OpenPartner().propertiesFrom(record) else null
    }

    override fun getByUserId(userId: Long): List<OpenPartner> {
        if(userId <= 0){
            return listOf()
        }
        val partnerIds = partnerUserMapper.select {
            this.where(OpenPartnerUserDynamicSqlSupport.OpenPartnerUser.userId, SqlBuilder.isEqualTo(userId))
        }.map { it.partnerId }

        val records = partnerMapper.select {
            this.where(OpenPartnerUserDynamicSqlSupport.OpenPartnerUser.partnerId, SqlBuilder.isIn(partnerIds))
        }

        return records.map {
            OpenPartner().propertiesFrom(it)
        }
    }

    override fun addPartnerUser(partnerId: Long, userId: Long): Boolean {
        userService.getUserById(userId) ?: throw UserNotFoundException()
        val record = OpenPartnerUserRecord().apply {
            this.userId = userId
            this.partnerId = partnerId
        }
        try {
            this.transactionTemplate.execute {
                partnerUserMapper.insert(record)
            }
            return true
        }catch (e: DuplicateKeyException){
            return false
        }
    }

    override fun createPartner(parameter: OpenPartnerCreation, userId: Long?): OpenPartner {
        val partnerId = idGenerator.newId();
        val record = OpenPartnerRecord().propertiesFrom(parameter).apply {
            id = partnerId
            status = OpenPartnerStatus.NORMAL.code
            timeLatestPaid = 0
            timeLatestUpdated = System.currentTimeMillis()
        }

        val partnerUser = if(userId != null){
            userService.getUserById(userId) ?: throw UserNotFoundException()
            OpenPartnerUserRecord(partnerId, userId)
        }else null

        transactionTemplate.execute {
            partnerMapper.insertSelective(record)
            if(partnerUser != null) {
                partnerUserMapper.insertSelective(partnerUser)
            }
        }
        return OpenPartner().propertiesFrom(record)
    }

    override fun setPartnerStatus(id: Long, status: OpenPartnerStatus): Boolean {
        val updating = OpenPartnerRecord().apply {
            this.id = id
            this.status = status.code
            this.timeLatestUpdated = System.currentTimeMillis()
        }
        return this.transactionTemplate.execute {
            partnerMapper.updateByPrimaryKeySelective(updating) > 0
        } ?: false
    }

    override fun setPartnerExpiration(id: Long, absoluteTimeExpired: Long): Boolean {
        val updating = OpenPartnerRecord().apply {
            this.id = id
            this.timeExpired = absoluteTimeExpired
        }
        return this.transactionTemplate.execute {
            partnerMapper.updateByPrimaryKeySelective(updating) > 0
        } ?: false
    }

    override fun setPartnerContact(id: Long, contact: OpenPartnerContact): Boolean {
        val updating = OpenPartnerRecord().apply {
            this.id = id
            this.phoneNumber = contact.phoneNumber
            this.email = contact.email
            this.contact = contact.contact
            this.timeLatestUpdated = System.currentTimeMillis()
        }
        return this.transactionTemplate.execute {
            partnerMapper.updateByPrimaryKeySelective(updating) > 0
        } ?: false
    }

    override fun listPartners(pageSize: Int, forwardToken: String?): ForwardList<OpenPartnerEntry> {
        val size = pageSize.coerceAtMost(100)
        val offset = forwardToken?.toLongOrNull() ?: 0
        val list = partnerMapper.select {
            if(offset > 0){
                this.where(OpenPartnerDynamicSqlSupport.OpenPartner.id, SqlBuilder.isLessThan(offset))
            }
            this.orderBy(OpenPartnerDynamicSqlSupport.OpenPartner.id.descending())
            this.limit(size.toLong())
        }
        val data = list.map { OpenPartnerEntry().propertiesFrom(it) }
        return ForwardList(data, list.lastOrNull()?.id?.toString())
    }
}