package com.labijie.application.geo.testing

import com.labijie.application.geo.Coordinate
import com.labijie.application.geo.MapPoint
import com.labijie.application.geo.configuration.GeoProperties
import com.labijie.application.geo.data.custom.mapper.GeoPointCustomMapper
import com.labijie.application.geo.data.mapper.GeoPointMapper
import com.labijie.application.geo.impl.H3GeoService
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.extension.ExtendWith
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.transaction.support.TransactionTemplate
import kotlin.test.Test

@ExtendWith(SpringExtension::class)
@MybatisTest
@ContextConfiguration(classes = [UnitTestConfiguration::class])
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class H3GeoServiceTester : ApplicationContextAware {
    private lateinit var applicationContext: ApplicationContext

    @Autowired
    private lateinit var geoPointCustomMapper: GeoPointCustomMapper

    @Autowired
    private lateinit var transactionTemplate: TransactionTemplate

    private val geoService:H3GeoService by lazy {
        H3GeoService(transactionTemplate, geoPointCustomMapper , GeoProperties())
    }

    @Test
    fun getPointsInDistance(){
        val center = Coordinate(39.905374,116.401304)
        this.geoService.getPointsInDistance(center, 2000.0)
    }

    @Test
    fun insertPoint(){
        val id = 12345465L;
        val point = MapPoint(id, "测试点", 3, 39.905374, 116.401304)
        val r = this.geoService.addPoint(point)
        Assertions.assertTrue(r)

        val pointStored = this.geoService.getPoint(id)
        Assertions.assertNotNull(pointStored)
        if(pointStored != null) {
            Assertions.assertEquals(point.id, pointStored.id)
            Assertions.assertEquals(point.pointName, pointStored.pointName)
            Assertions.assertEquals(point.pointType, pointStored.pointType)
            Assertions.assertEquals(point.latitude, pointStored.latitude)
            Assertions.assertEquals(point.longitude, pointStored.longitude)
        }
    }

    override fun setApplicationContext(applicationContext: ApplicationContext) {
         this.applicationContext = applicationContext
    }
}