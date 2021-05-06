package com.labijie.application.open.test

import com.labijie.application.data.SnowflakeCustomMapper
import com.labijie.application.data.mapper.SnowflakeSlotDynamicSqlSupport.SnowflakeSlot
import com.labijie.application.data.mapper.SnowflakeSlotMapper
import com.labijie.application.data.mapper.selectOne
import com.labijie.application.snowflake.InstanceIdentity
import com.labijie.application.snowflake.JdbcSlotProvider
import com.labijie.application.snowflake.JdbcSlotProviderProperties
import com.labijie.infra.collections.ConcurrentHashSet
import com.labijie.infra.commons.snowflake.configuration.SnowflakeConfig
import com.labijie.infra.spring.configuration.NetworkConfig
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.extension.ExtendWith
import org.mybatis.dynamic.sql.SqlBuilder
import org.mybatis.spring.annotation.MapperScan
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.test.annotation.Commit
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.jdbc.Sql
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.context.transaction.TestTransaction
import org.springframework.test.jdbc.JdbcTestUtils
import org.springframework.transaction.support.TransactionTemplate
import java.time.Duration
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger
import javax.sql.DataSource
import kotlin.concurrent.thread
import kotlin.random.Random
import kotlin.test.BeforeTest
import kotlin.test.Test

/**
 *
 * @Auther: AndersXiao
 * @Date: 2021-05-01 13:07
 * @Description:
 */
@ExtendWith(SpringExtension::class)
@MybatisTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ContextConfiguration(classes = [JdbcSnowflakeSlotTester.JdbcSnowflakeTestConfiguration::class])
@Sql("classpath:snowflake.sql")
class JdbcSnowflakeSlotTester : ApplicationContextAware {
    private lateinit var appContext: ApplicationContext

    companion object {
        const val TABLE_NAME = "core_snowflake_slots"
    }

    override fun setApplicationContext(applicationContext: ApplicationContext) {
        appContext = applicationContext
    }

    @Configuration(proxyBeanMethods = false)
    @MapperScan(basePackageClasses = [SnowflakeSlotMapper::class, SnowflakeCustomMapper::class])
    class JdbcSnowflakeTestConfiguration

    @Autowired
    private lateinit var transactionTemplate: TransactionTemplate

    @Autowired
    private lateinit var snMapper: SnowflakeSlotMapper

    @Autowired
    private lateinit var snMapper2: SnowflakeCustomMapper

    @Autowired
    private lateinit var jdbcTemplate: JdbcTemplate


    private fun getSlotProvider(maxSlot: Int = 1024, timeout: Duration = Duration.ofMinutes(1), identity: InstanceIdentity = InstanceIdentity.UUID): JdbcSlotProvider = JdbcSlotProvider(
            maxSlot,
            SnowflakeConfig().apply {
                scope = "test"
            },
            NetworkConfig(null),
            transactionTemplate,
            JdbcSlotProviderProperties().apply {
                this.instanceIdentity = identity
                this.timeout = timeout
            },
            snMapper,
            snMapper2
    ).apply {
        //this.forUnitTest()
    }

    @BeforeTest
    fun clean() {
        JdbcTestUtils.deleteFromTables(jdbcTemplate, TABLE_NAME)
    }

    @Test
    fun testAcquireSlot() {
        val p = getSlotProvider().apply {
            val s = this.acquireSlot(false)

            Assertions.assertTrue(s!! >= 1 && s <= 1024)
            Assertions.assertNotNull(s)
            Assertions.assertEquals(s, this.slot?.toInt())
        }
        val r = this.snMapper.selectOne {
            this.where(SnowflakeSlot.instance, SqlBuilder.isEqualTo(p.instanceId))
        }

        Assertions.assertNotNull(r)
        Assertions.assertEquals(p.instanceId, r!!.instance)
        Assertions.assertEquals(p.getSlotValue(p.slot ?: -1), r.slotNumber)
    }

    @Test
    fun testFullSlot() {
        val maxSlot = 3
        repeat(maxSlot) {
            val provider = getSlotProvider(maxSlot)
            provider.acquireSlot(false)
        }
        val slot = getSlotProvider(maxSlot).acquireSlot(false)


        Assertions.assertNull(slot)
    }


    @Test
    fun testReentrantSlot() {
        val provider = getSlotProvider(2, identity = InstanceIdentity.IP)
        val slot = provider.acquireSlot(false)
        TestTransaction.flagForCommit();
        TestTransaction.end()

        Assertions.assertNotNull(slot)

        val provider2 = getSlotProvider(2, identity = InstanceIdentity.IP)
        val slot2 = provider2.acquireSlot(false)

        Assertions.assertEquals(slot, slot2)

        val provider3 = getSlotProvider(2)
        val slot3 = provider3.acquireSlot(false)

        Assertions.assertNotEquals(slot, slot3)
    }

    @Test
    fun testRenewSlot() {
        val provider = getSlotProvider(1, timeout = Duration.ofSeconds(3))
        provider.acquireSlot(false)

        TestTransaction.flagForCommit();
        TestTransaction.end()

        val provider2 = getSlotProvider(1)

        Thread.sleep(3500)
        val count = provider.getRenewCount()
        Assertions.assertTrue(count > 0, "renew count is 0")

        val s = provider2.acquireSlot(false)
        Assertions.assertNull(s)
    }

    @Test
    fun testConcurrentGetSlot() {
        val maxSlot = 5
        val threadCount = 10

        val gotCount = AtomicInteger()
        val missedCount = AtomicInteger()
        val slots = ConcurrentHashSet<Int>()

        val countDown = CountDownLatch(threadCount)
        repeat(threadCount) { _ ->
            thread {
                Thread.sleep(Random.nextLong(0, 1000))
                val p = getSlotProvider(maxSlot)
                val slot = p.acquireSlot(false)
                if (slot == null) {
                    missedCount.incrementAndGet()
                } else {
                    gotCount.incrementAndGet()
                    slots.add(slot)
                }
                countDown.countDown()
            }
        }


        val done = countDown.await(Duration.ofSeconds(10).seconds, TimeUnit.SECONDS)
        Assertions.assertTrue(done, "No results have been returned for more than 10 minutes and deadlocks may occur.")
        Assertions.assertEquals(maxSlot, gotCount.get(), "excepted got slot is $maxSlot, but it was ${gotCount.get()}")
        Assertions.assertEquals(threadCount - maxSlot, missedCount.get(), "excepted missed slot is ${threadCount - maxSlot}, but it was ${missedCount.get()}")
        Assertions.assertEquals(maxSlot, slots.size, "excepted got $maxSlot duplex slot, but it got ${slots.size}")

        val exceptedArray = mutableListOf<Int>()
        repeat(maxSlot) {
            exceptedArray.add(it + 1)
        }
        Assertions.assertArrayEquals(exceptedArray.toTypedArray(), slots.sorted().toTypedArray())
    }
}