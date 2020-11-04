package com.labijie.application.aliyun.utils

import com.aliyun.oss.ClientException
import com.aliyuncs.CommonRequest
import com.aliyuncs.IAcsClient
import com.aliyuncs.http.MethodType
import com.labijie.application.aliyun.AliyunProcessException
import com.labijie.application.aliyun.model.AfsRequest
import com.labijie.application.aliyun.model.AfsResult
import com.labijie.infra.json.JacksonHelper
import com.labijie.infra.utils.logger
import java.rmi.ServerException

/**
 * Created with IntelliJ IDEA.
 * @author Anders Xiao
 * @date 2019-11-23
 */
open class AfsUtils internal constructor(private val client: IAcsClient) {

    fun authSig(request: AfsRequest): AfsResult {
        val r = CommonRequest().apply {
            this.sysMethod = MethodType.GET
            this.sysDomain = "afs.aliyuncs.com"
            this.sysVersion = "2018-01-12"
            this.sysAction = "AuthenticateSig"
            this.putQueryParameter("Token", request.token)
            this.putQueryParameter("Sig", request.sig)
            this.putQueryParameter("SessionId", request.sessionId)
            this.putQueryParameter("Scene", request.scene)
            this.putQueryParameter("AppKey", request.appKey)
            this.putQueryParameter("RemoteIp", request.remoteIp)
        }

        try {
            val response = client.getCommonResponse(r)
            val data = response.data
            val afsResult = JacksonHelper.deserializeFromString(data, AfsResult::class)
            if(!afsResult.success){
                logger.warn("Human check fault: ${System.lineSeparator()} $data");
            }
            return afsResult
        } catch (e: ServerException) {
            throw AliyunProcessException("Afs verify request fault.", e)
        } catch (e: ClientException) {
            throw AliyunProcessException("Afs verify request fault.", e)
        }
    }
}