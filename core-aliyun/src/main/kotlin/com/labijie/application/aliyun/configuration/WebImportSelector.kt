package com.labijie.application.aliyun.configuration

import com.labijie.application.aliyun.annotation.EnableAliyunServices
import com.labijie.application.aliyun.controller.CloudProviderController
import org.springframework.context.annotation.ImportSelector
import org.springframework.core.type.AnnotationMetadata

/**
 * Created with IntelliJ IDEA.
 * @author Anders Xiao
 * @date 2019-09-20
 */
class WebImportSelector : ImportSelector {
    override fun selectImports(importingClassMetadata: AnnotationMetadata): Array<String> {
        val attributes = importingClassMetadata.getAnnotationAttributes(EnableAliyunServices::class.java.name)
        val beans = mutableSetOf<String>()

        val includeWeb = attributes!![EnableAliyunServices::loadWebControllers.name] as Boolean

        if(includeWeb){
            beans.add(CloudProviderController::class.java.name)
        }

        return beans.toTypedArray()
    }
}