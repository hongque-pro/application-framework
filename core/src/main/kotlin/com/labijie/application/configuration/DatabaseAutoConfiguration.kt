package com.labijie.application.configuration

import com.labijie.infra.utils.logger
import org.springframework.beans.factory.config.BeanPostProcessor
import org.springframework.boot.autoconfigure.AutoConfigureBefore
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.autoconfigure.transaction.TransactionAutoConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.annotation.Isolation
import org.springframework.transaction.interceptor.MatchAlwaysTransactionAttributeSource
import org.springframework.transaction.interceptor.NoRollbackRuleAttribute
import org.springframework.transaction.interceptor.RuleBasedTransactionAttribute
import org.springframework.transaction.interceptor.TransactionInterceptor
import org.springframework.transaction.support.TransactionOperations
import org.springframework.transaction.support.TransactionTemplate

/**
 *
 * @Author: Anders Xiao
 * @Date: 2022/1/2
 * @Description:
 */
@ConditionalOnClass(PlatformTransactionManager::class)
@AutoConfigureBefore(TransactionAutoConfiguration::class)
@Configuration(proxyBeanMethods = false)
class DatabaseAutoConfiguration: BeanPostProcessor {

    @Bean
    @ConditionalOnMissingBean(TransactionOperations::class)
    fun transactionTemplate(transactionManager: PlatformTransactionManager): TransactionTemplate {
        val rules = RuleBasedTransactionAttribute()
        rules.isolationLevel = Isolation.READ_COMMITTED.value()
        rules.rollbackRules.add(NoRollbackRuleAttribute(Throwable::class.java))

        logger.info("TransactionTemplate set to rollback for Throwable.")

        return TransactionTemplate(transactionManager, rules)
    }

    override fun postProcessAfterInitialization(bean: Any, beanName: String): Any? {
        if(bean is TransactionInterceptor){
            val txAttributeSource = MatchAlwaysTransactionAttributeSource().apply {
                setTransactionAttribute(RollbackForThrowableAttribute())
            }
            val attribute = bean.transactionAttributeSource
            if(attribute != null){
                bean.setTransactionAttributeSources(txAttributeSource, attribute)
            }else{
                bean.setTransactionAttributeSources(txAttributeSource)
            }
            logger.info("TransactionInterceptor set to rollback for Throwable.")
        }
        return bean
    }



}