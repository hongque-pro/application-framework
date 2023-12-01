package com.labijie.application.open.service

import com.labijie.application.ForwardList
import com.labijie.application.open.data.pojo.OpenApp
import com.labijie.application.open.model.*

interface IOpenAppService {
    fun createApp(creation: OpenAppCreation, partnerId: Long): OpenApp
    fun getByJsApiKey(key: String): OpenApp?
    fun getByAppId(appId: Long): OpenApp?
    fun renewSecret(appId: Long, signAlgorithm: String = "sha256"): String?
    fun getSecret(appId: Long): AlgorithmAndKey?
    fun setAppStatus(appId: Long, status: OpenAppStatus): Boolean
    fun listApps(pageSize: Int, forwardToken: String? = null) : ForwardList<OpenAppEntry>
    fun listApps(partnerId: Long) : List<OpenAppEntry>
    fun setAppConfiguration(appId: Long, configuration: Map<String, String>): Boolean
}