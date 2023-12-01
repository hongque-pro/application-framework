package com.labijie.application.open.service

import com.labijie.application.ForwardList
import com.labijie.application.open.data.pojo.OpenPartner
import com.labijie.application.open.model.*

interface IOpenPartnerService {
    fun getById(id: Long): OpenPartner?
    fun getByUserId(userId:Long) : List<OpenPartner>
    fun addPartnerUser(partnerId:Long, userId: Long): Boolean
    fun createPartner(parameter: OpenPartnerCreation, userId: Long? = null) : OpenPartner
    fun setPartnerStatus(id: Long, status: OpenPartnerStatus): Boolean
    fun setPartnerExpiration(id: Long, absoluteTimeExpired: Long): Boolean
    fun setPartnerContact(id: Long, contact: OpenPartnerContact): Boolean
    fun listPartners(pageSize: Int, forwardToken: String? = null) : ForwardList<OpenPartnerEntry>
}