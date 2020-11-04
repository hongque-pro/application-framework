package com.labijie.application.aliyun

import com.aliyuncs.DefaultAcsClient
import com.aliyuncs.auth.sts.AssumeRoleRequest
import com.aliyuncs.auth.sts.AssumeRoleResponse
import com.aliyuncs.profile.DefaultProfile
import com.aliyuncs.profile.IClientProfile
import com.labijie.application.aliyun.configuration.AliyunProperties
import com.labijie.application.aliyun.utils.AfsUtils
import com.labijie.application.aliyun.utils.OSSUtils
import com.labijie.application.aliyun.utils.SmsUtils

/**
 * Created with IntelliJ IDEA.
 * @author Anders Xiao
 * @date 2019-09-20
 */
open class AliyunUtils(private val sessionName:String, val properties: AliyunProperties) {

    val clientProfile:IClientProfile by lazy {
        if(properties.accessKeyId.isBlank() || properties.accessKeySecret.isBlank()){
            throw AliyunProcessException("Aliyun accessKeyId or accessKeySecret missed.")
        }
        DefaultProfile.addEndpoint("", "Sts", properties.sts.endpoint)
        DefaultProfile.getProfile("", properties.accessKeyId, properties.accessKeySecret)
    }

    val accessClient:DefaultAcsClient by lazy {
        DefaultAcsClient(this.clientProfile)
    }

    val sms: SmsUtils by lazy {
        SmsUtils(accessClient, properties)
    }

    val oss: OSSUtils by lazy {
        OSSUtils(this.properties)
    }

    val afs: AfsUtils by lazy {
        AfsUtils(this.accessClient)
    }


    open fun assumeRole(): AssumeRoleResponse.Credentials {
        if(properties.sts.role.isBlank()){
            throw AliyunProcessException("Role for aliyun sts  can not be empty.")
        }
        val request = AssumeRoleRequest().apply {
            this.roleArn = properties.sts.role
            this.roleSessionName = sessionName
            this.durationSeconds = properties.sts.tokenTimeout.seconds
        }
        val response = this.accessClient.getAcsResponse(request)
        return response.credentials
    }


}