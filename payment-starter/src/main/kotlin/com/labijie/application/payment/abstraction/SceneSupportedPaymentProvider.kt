package com.labijie.application.payment.abstraction

import com.labijie.application.payment.*
import com.labijie.application.payment.configuration.PaymentProperties
import com.labijie.application.payment.exception.PaymentException
import com.labijie.application.payment.providers.alipay.AlipayPaymentOptions
import com.labijie.application.payment.scene.ISceneSupport
import com.labijie.application.web.client.MultiRestTemplates
import org.springframework.web.client.RestTemplate

abstract class SceneSupportedPaymentProvider<TOptions: PaymentOptions, TSceneSupport: ISceneSupport>(
    paymentProperties: PaymentProperties,
    options: TOptions,
    restTemplates: MultiRestTemplates
) :
    AbstractPaymentProvider<TOptions>(paymentProperties, options, restTemplates) {
    private val sceneSupports = mutableListOf<TSceneSupport>()

    protected val allowScene:Boolean = true

    fun addSceneSupport(sceneSupport: TSceneSupport){
        if(!allowScene) {
            throw PaymentException(this.name, "Payment scene was not supported.")
        }
        this.sceneSupports.add(sceneSupport)
    }

    protected fun findSceneSupport(sceneData: Any?): TSceneSupport? {
        return if(allowScene && sceneData != null){
            val s = this.sceneSupports.firstOrNull { it.isSupported(sceneData) }
            if(s == null){
                log.debug("Supply a value for PlatformTrade.scene, but scene support was not found ( payment provider: ${this.name}, scene data: ${sceneData::class.java.simpleName} ) .")
            }
            s
        }else{
            null
        }
    }

    override fun doCreateTrade(trade: PlatformTrade): PaymentTradeCreationResult {
        val sceneSupport  = this.findSceneSupport(trade.scene)
        return this.doCreateTrade(trade, sceneSupport)
    }


    protected final inline fun <reified T:TOptions> checkPaymentOptions(overrideOptions: PaymentOptions?):T? {
       if(overrideOptions != null) {
            val platformOptions = overrideOptions as? T
            return  platformOptions ?: throw IllegalArgumentException("The 'overrideOptions' parameter must be an ${this.options::class.java.simpleName} when ${this::class.java.simpleName} be used.")
        }
        return  null
    }

    abstract fun doCreateTrade(trade: PlatformTrade, sceneSupport: TSceneSupport?):PaymentTradeCreationResult

}