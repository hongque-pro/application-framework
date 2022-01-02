package com.labijie.application.configuration

import com.labijie.infra.utils.logger
import org.springframework.beans.factory.InitializingBean
import org.springframework.boot.autoconfigure.AutoConfigureBefore
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.autoconfigure.transaction.TransactionAutoConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.annotation.Isolation
import org.springframework.transaction.interceptor.NoRollbackRuleAttribute
import org.springframework.transaction.interceptor.RuleBasedTransactionAttribute
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
class DatabaseAutoConfiguration: InitializingBean {

    @Bean
    @ConditionalOnMissingBean(TransactionOperations::class)
    fun transactionTemplate(transactionManager: PlatformTransactionManager): TransactionTemplate {
        val rules = RuleBasedTransactionAttribute()
        rules.isolationLevel = Isolation.READ_COMMITTED.value()
        rules.rollbackRules.add(NoRollbackRuleAttribute(Throwable::class.java))
        return TransactionTemplate(transactionManager, rules)
    }

    override fun afterPropertiesSet() {
        logger.info("TransactionTemplate set to rollback for Throwable.")
    }

}