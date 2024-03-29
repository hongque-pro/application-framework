package com.labijie.application.configuration

import com.labijie.infra.utils.logger
import org.springframework.boot.autoconfigure.AutoConfigureAfter
import org.springframework.boot.autoconfigure.AutoConfigureBefore
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration
import org.springframework.boot.autoconfigure.transaction.TransactionAutoConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.annotation.Isolation
import org.springframework.transaction.interceptor.RollbackRuleAttribute
import org.springframework.transaction.interceptor.RuleBasedTransactionAttribute
import org.springframework.transaction.support.TransactionOperations
import org.springframework.transaction.support.TransactionTemplate
import javax.sql.DataSource

/**
 *
 * @Author: Anders Xiao
 * @Date: 2022/1/2
 * @Description:
 */
@ConditionalOnClass(PlatformTransactionManager::class)
@AutoConfigureBefore(TransactionAutoConfiguration::class)
@AutoConfigureAfter(DataSourceAutoConfiguration::class)
@Configuration(proxyBeanMethods = false)
@ConditionalOnBean(DataSource::class)
class ApplicationTransactionAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(TransactionOperations::class)
    fun transactionTemplate(transactionManager: PlatformTransactionManager): TransactionTemplate {
        val rules = RuleBasedTransactionAttribute()
        rules.isolationLevel = Isolation.DEFAULT.value()
        rules.rollbackRules.add(RollbackRuleAttribute(Throwable::class.java))
        rules.isReadOnly = false

        logger.info("TransactionTemplate set to rollback for Throwable.")

        return TransactionTemplate(transactionManager, rules)
    }
}