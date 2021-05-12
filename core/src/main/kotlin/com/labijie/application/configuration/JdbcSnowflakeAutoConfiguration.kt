package com.labijie.application.configuration

import com.labijie.application.data.SnowflakeCustomMapper
import com.labijie.application.data.mapper.SnowflakeSlotMapper
import com.labijie.application.snowflake.JdbcSlotProvider
import com.labijie.application.snowflake.JdbcSlotProviderProperties
import com.labijie.infra.commons.snowflake.ISlotProvider
import com.labijie.infra.commons.snowflake.configuration.SnowflakeConfig
import com.labijie.infra.commons.snowflake.providers.StaticSlotProvider
import com.labijie.infra.spring.configuration.CommonsAutoConfiguration
import com.labijie.infra.spring.configuration.NetworkConfig
import org.mybatis.spring.annotation.MapperScan
import org.mybatis.spring.annotation.MapperScans
import org.mybatis.spring.boot.autoconfigure.MybatisAutoConfiguration
import org.springframework.boot.autoconfigure.AutoConfigureAfter
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.transaction.support.TransactionTemplate

/**
 *
 * @Auther: AndersXiao
 * @Date: 2021-05-01 12:19
 * @Description:
 */
@ConditionalOnProperty(prefix = "infra.snowflake", name = ["provider"], havingValue = "jdbc", matchIfMissing = false)
@Configuration(proxyBeanMethods = false)
@AutoConfigureAfter(DataSourceAutoConfiguration::class, MybatisAutoConfiguration::class, CommonsAutoConfiguration::class)
@MapperScan(basePackageClasses = [SnowflakeSlotMapper::class, SnowflakeCustomMapper::class])
@EnableConfigurationProperties(JdbcSlotProviderProperties::class)
class JdbcSnowflakeAutoConfiguration {

    @ConditionalOnMissingBean(JdbcSlotProvider::class)
    @Bean
    fun jdbcSnowflakeSlotProvider(
            mapper: SnowflakeSlotMapper,
            updateMapper: SnowflakeCustomMapper,
            jdbcSlotProviderProperties: JdbcSlotProviderProperties,
            transactionTemplate:TransactionTemplate,
            networkConfig: NetworkConfig,
            snowflakeConfig: SnowflakeConfig): ISlotProvider {
        return JdbcSlotProvider(snowflakeConfig, networkConfig, transactionTemplate, jdbcSlotProviderProperties, mapper, updateMapper)
    }
}