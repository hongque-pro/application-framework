package com.labijie.application.web.annotation

import com.labijie.application.model.OneTimeCodeTarget
import com.labijie.application.model.OneTimeCodeVerifyResult

/**
 * @author Anders Xiao
 * @date 2025/7/31
 */

/**
 * Got [OneTimeCodeTarget] or [OneTimeCodeVerifyResult] on controller method.
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class OneTimeCodeVerify