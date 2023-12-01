package com.labijie.application.open.service.impl

import com.labijie.application.ForwardList
import com.labijie.application.exception.UserNotFoundException
import com.labijie.application.identity.service.IUserService
import com.labijie.application.open.data.OpenPartnerTable
import com.labijie.application.open.data.OpenPartnerUserTable
import com.labijie.application.open.data.pojo.OpenPartner
import com.labijie.application.open.data.pojo.OpenPartnerUser
import com.labijie.application.open.data.pojo.dsl.OpenPartnerDSL.insert
import com.labijie.application.open.data.pojo.dsl.OpenPartnerDSL.selectByPrimaryKey
import com.labijie.application.open.data.pojo.dsl.OpenPartnerDSL.selectMany
import com.labijie.application.open.data.pojo.dsl.OpenPartnerDSL.toOpenPartnerList
import com.labijie.application.open.data.pojo.dsl.OpenPartnerUserDSL.insert
import com.labijie.application.open.data.pojo.dsl.OpenPartnerUserDSL.selectMany
import com.labijie.application.open.model.*
import com.labijie.application.open.service.IOpenPartnerService
import com.labijie.application.propertiesFrom
import com.labijie.infra.IIdGenerator
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.andWhere
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.update
import org.springframework.dao.DuplicateKeyException
import org.springframework.transaction.support.TransactionTemplate

class OpenPartnerService(
    private val idGenerator: IIdGenerator,
    private val transactionTemplate: TransactionTemplate,
    private val userService: IUserService
) : IOpenPartnerService {

    override fun getById(id: Long): OpenPartner? {
        if(id <= 0){
            return null
        }
        val record = OpenPartnerTable.selectByPrimaryKey(id)
        return if(record != null) OpenPartner().propertiesFrom(record) else null
    }

    override fun getByUserId(userId: Long): List<OpenPartner> {
        if(userId <= 0){
            return listOf()
        }
        val partnerIds = OpenPartnerUserTable.selectMany(OpenPartnerUserTable.partnerId) {
            andWhere { OpenPartnerUserTable.userId eq userId }
        }.map { it.partnerId }

        if(partnerIds.isEmpty()){
            return listOf()
        }

        val records = OpenPartnerTable.selectMany {
            andWhere { OpenPartnerTable.id inList partnerIds }
        }

        return records
    }

    override fun addPartnerUser(partnerId: Long, userId: Long): Boolean {
        userService.getUserById(userId) ?: throw UserNotFoundException()
        val record = OpenPartnerUser().apply {
            this.userId = userId
            this.partnerId = partnerId
        }
        return try {
            this.transactionTemplate.execute {
                OpenPartnerUserTable.insert(record)
            }
            true
        }catch (e: DuplicateKeyException){
            false
        }
    }

    override fun createPartner(parameter: OpenPartnerCreation, userId: Long?): OpenPartner {
        val partnerId = idGenerator.newId();
        val record = OpenPartner().propertiesFrom(parameter).apply {
            id = partnerId
            status = OpenPartnerStatus.NORMAL
            timeLatestPaid = 0
            timeLatestUpdated = System.currentTimeMillis()
        }

        val partnerUser = if(userId != null){
            userService.getUserById(userId) ?: throw UserNotFoundException()
            OpenPartnerUser().apply {
                this.partnerId = partnerId
                this.userId = userId
            }
        }else null

        transactionTemplate.execute {
            OpenPartnerTable.insert(record)
            if(partnerUser != null) {
                OpenPartnerUserTable.insert(partnerUser)
            }
        }
        return OpenPartner().propertiesFrom(record)
    }

    override fun setPartnerStatus(id: Long, status: OpenPartnerStatus): Boolean {
        return this.transactionTemplate.execute {
            OpenPartnerTable.update({ OpenPartnerTable.id eq id }) {
                it[OpenPartnerTable.status] = status
                it[timeLatestUpdated] = System.currentTimeMillis()
            } > 0
        } ?: false
    }

    override fun setPartnerExpiration(id: Long, absoluteTimeExpired: Long): Boolean {
        return this.transactionTemplate.execute {
            OpenPartnerTable.update({ OpenPartnerTable.id eq id }) {
                it[timeExpired] = absoluteTimeExpired
            } > 0
        } ?: false
    }

    override fun setPartnerContact(id: Long, contact: OpenPartnerContact): Boolean {

        if(contact.contact != null || contact.email != null || contact.phoneNumber != null) {
            return this.transactionTemplate.execute {
                OpenPartnerTable.update({ OpenPartnerTable.id eq id }) {
                    if (contact.contact != null) {
                        it[OpenPartnerTable.contact] = contact.contact.orEmpty()
                    }
                    if (contact.email != null) {
                        it[email] = contact.email.orEmpty()
                    }
                    if (contact.phoneNumber != null) {
                        it[phoneNumber] = contact.phoneNumber.orEmpty()
                    }
                } > 0
            } ?: false
        }
        return false
    }

    override fun listPartners(pageSize: Int, forwardToken: String?): ForwardList<OpenPartnerEntry> {
        val size = pageSize.coerceAtMost(100)
        val offset = forwardToken?.toLongOrNull() ?: 0
        val query = OpenPartnerTable.selectAll()

        if(offset > 0){
            query.andWhere { OpenPartnerTable.id less  offset }
        }
        val list = query.orderBy(OpenPartnerTable.id to SortOrder.DESC).limit(size).toOpenPartnerList()

        val data = list.map { OpenPartnerEntry().propertiesFrom(it) }
        return ForwardList(data, list.lastOrNull()?.id?.toString())
    }
}