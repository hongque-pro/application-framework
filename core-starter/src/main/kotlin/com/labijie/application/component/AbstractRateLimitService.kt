package com.labijie.application.component

import com.labijie.application.exception.OutOfRateLimitationException
import com.labijie.caching.CacheException
import com.labijie.caching.ICacheManager
import com.labijie.caching.set
import com.labijie.infra.utils.logger
import java.time.Duration

/**
 * @author Anders Xiao
 * @date 2025/7/30
 */
abstract class AbstractRateLimitService(
    protected val cacheManager: ICacheManager,
) {

    protected open val rateLimited: Boolean
        get() = true

    protected abstract fun getRateLimitation(): Duration

    protected open fun onOutOfRateLimit() {}

    protected open fun <T> rateLimit(limitIdentifier: String, operationName: String, sendAction: () -> T): T {

        val rateLimitMills =  getRateLimitation().toMillis()

        try {
            val lastSend = (cacheManager.get(limitIdentifier, Long::class.java) as? Long) ?: 0
            if (this.rateLimited &&  (System.currentTimeMillis() - lastSend) < rateLimitMills) {
                onOutOfRateLimit()
                throw OutOfRateLimitationException(operationName = operationName)
            }
        } catch (e: CacheException) {
            logger.error("Get rate limit frequency from cache fault. key: $limitIdentifier, op: $operationName", e)
        }
        val r = sendAction.invoke()
        try {
            cacheManager.set(limitIdentifier, System.currentTimeMillis(), rateLimitMills)
        } catch (e: CacheException) {
            logger.error("Set rate limit history to cache fault. key: $limitIdentifier, op: $operationName", e)
        }
        return r
    }
}