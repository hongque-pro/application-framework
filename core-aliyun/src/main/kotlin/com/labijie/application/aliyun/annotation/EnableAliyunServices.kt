package com.labijie.application.aliyun.annotation

import com.labijie.application.aliyun.configuration.AliyunAutoConfiguration
import com.labijie.application.aliyun.configuration.WebImportSelector
import org.springframework.context.annotation.Import

/**
 * Created with IntelliJ IDEA.
 * @author Anders Xiao
 * @date 2019-09-20
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@Import(WebImportSelector::class, AliyunAutoConfiguration::class)
annotation class EnableAliyunServices(val loadWebControllers:Boolean = false) {
}