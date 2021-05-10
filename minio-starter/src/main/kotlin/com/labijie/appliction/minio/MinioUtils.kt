package com.labijie.appliction.minio

import com.labijie.appliction.minio.configuration.MinioProperties
import org.springframework.web.client.RestTemplate

object MinioUtils {
    /**
     * https://github.com/minio/minio/blob/master/docs/sts/assume-role.md
     */
    fun assumeRole(restTemplate: RestTemplate, minioProperties: MinioProperties){

    }
}