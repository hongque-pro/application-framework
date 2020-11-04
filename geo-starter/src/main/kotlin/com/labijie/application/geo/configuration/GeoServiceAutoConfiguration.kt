package com.labijie.application.geo.configuration

import com.labijie.application.geo.IGeoService
import com.labijie.application.geo.data.custom.mapper.GeoPointCustomMapper
import com.labijie.application.geo.data.mapper.GeoPointMapper
import com.labijie.application.geo.impl.H3GeoService
import org.springframework.boot.autoconfigure.AutoConfigureAfter
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.transaction.support.TransactionTemplate

@Configuration
@AutoConfigureAfter(GeoMybatisMapperAutoConfiguration::class)
@EnableConfigurationProperties(GeoProperties::class)
class GeoServiceAutoConfiguration {
    @ConditionalOnMissingBean(IGeoService::class)
    @Bean
    fun h3GeoService(
        geoProperties: GeoProperties,
        transactionTemplate: TransactionTemplate,
        geoPointCustomMapper: GeoPointCustomMapper
    ): H3GeoService {
        return H3GeoService(transactionTemplate,geoPointCustomMapper, geoProperties)
    }
}