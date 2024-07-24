package com.labijie.application.dapr

import com.labijie.application.IModuleInitializer
import com.labijie.application.dapr.configuration.DaprProperties
import io.dapr.client.DaprClient
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.DisposableBean

/**
 * @author Anders Xiao
 * @date 2023-12-09
 */
open class DaprDisposable(
    private val daprClient: DaprClient,
    private val daprProperties: DaprProperties) : IModuleInitializer, DisposableBean {

    companion object {
        private val logger by lazy {
            LoggerFactory.getLogger(DaprDisposable::class.java)
        }
    }

    override fun getModuleName(): String {
        return "Dapr"
    }
    override fun destroy() {
        if(daprProperties.shutdownDaprOnExit) {
            var retryCount = 0
            val ex: Throwable? = null
            logger.info("Shutdown dapr ...")
            while (true) {
                try {
                    daprClient.shutdown()?.block()
                } catch (e: Throwable) {
                    retryCount++
                    Thread.sleep(500)
                    if(retryCount < 3){
                        continue
                    }
                }
                break
            }
            ex?.let {
                logger.warn("Shutdown dapr failed.", it)
            }
            try {
                daprClient.close()
            } catch (e: Throwable) {
                logger.warn("Close dapr failed.", e)
            }
        }
    }
}