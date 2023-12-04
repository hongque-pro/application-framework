package com.labijie.application.web.annotation

import java.time.Duration
import java.util.concurrent.TimeUnit

/**
 * Created with IntelliJ IDEA.
 * @author Anders Xiao
 * @date 2019-09-21
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class HttpCache(val maxAge: Long, val unit: TimeUnit)