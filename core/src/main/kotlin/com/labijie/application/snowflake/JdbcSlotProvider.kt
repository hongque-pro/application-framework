package com.labijie.application.snowflake

import com.labijie.application.configure
import com.labijie.application.data.SnowflakeCustomMapper
import com.labijie.application.data.SnowflakeSlotRecord
import com.labijie.application.data.mapper.*
import com.labijie.application.data.mapper.SnowflakeSlotDynamicSqlSupport.SnowflakeSlot
import com.labijie.application.orDefault
import com.labijie.infra.SecondIntervalTimeoutTimer
import com.labijie.infra.commons.snowflake.ISlotProvider
import com.labijie.infra.commons.snowflake.SnowflakeException
import com.labijie.infra.commons.snowflake.configuration.SnowflakeConfig
import com.labijie.infra.scheduling.IntervalTask
import com.labijie.infra.spring.configuration.NetworkConfig
import io.netty.util.Timeout
import org.mybatis.dynamic.sql.SqlBuilder
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.dao.DuplicateKeyException
import org.springframework.transaction.annotation.Isolation
import org.springframework.transaction.support.TransactionTemplate
import java.lang.RuntimeException
import java.time.Duration
import java.util.*
import java.util.concurrent.atomic.AtomicLong

/**
 *
 * @Auther: AndersXiao
 * @Date: 2021-05-01 10:43
 * @Description:
 */

class JdbcSlotProvider constructor(
        maxSlot: Int,
        snowflakeConfig: SnowflakeConfig,
        networkConfig: NetworkConfig,
        private val transactionTemplate: TransactionTemplate,
        private val jdbcSlotProviderProperties: JdbcSlotProviderProperties,
        private val mapper: SnowflakeSlotMapper,
        private val updateMapper: SnowflakeCustomMapper) : ISlotProvider, AutoCloseable {

    @Autowired
    constructor(
            snowflakeConfig: SnowflakeConfig,
            networkConfig: NetworkConfig,
            transactionTemplate: TransactionTemplate,
            jdbcSlotProviderProperties: JdbcSlotProviderProperties,
            mapper: SnowflakeSlotMapper,
            updateMapper: SnowflakeCustomMapper) :
            this(1024, snowflakeConfig, networkConfig, transactionTemplate, jdbcSlotProviderProperties, mapper, updateMapper)

    companion object {
        @JvmStatic
        val log = LoggerFactory.getLogger(JdbcSlotProvider::class.java)
    }

    private var maxSlotCount = maxSlot
    private var scope = snowflakeConfig.scope

    private val ipAddr = networkConfig.getIPAddress()
    private var task: IntervalTask? = null
    private val renewCount = AtomicLong()

    fun getRenewCount(): Long {
        return renewCount.get()
    }

    @Volatile
    private var stopped = false
    var slot: Short? = null

    
    fun getSlotValue(slot: Short): String {
        return "$scope:$slot"
    }

    val instanceId: String by lazy {
        when (jdbcSlotProviderProperties.instanceIdentity) {
            InstanceIdentity.IP -> ipAddr
            else -> UUID.randomUUID().toString().replace("-", "")
        }
    }



    private fun getTimeExpired(): Long = System.currentTimeMillis() + jdbcSlotProviderProperties.timeout.toMillis()

    override fun acquireSlot(throwIfNoneSlot: Boolean): Int? {
        val slotGot = acquire()
        if (slotGot != null) {
            log.info("Jdbc snowflake slot '$slotGot' retained by instance '$instanceId' ( ip: $ipAddr ) .")
            val interval = (jdbcSlotProviderProperties.timeout.toMillis() / 2.5).toLong()
            if (interval < 500) {
                log.warn("Jdbc snowflake slot timeout too short, current timeout: ${jdbcSlotProviderProperties.timeout}.")
            }
            this.task?.cancel()
            this.slot = slotGot.toShort()
            SecondIntervalTimeoutTimer.interval(Duration.ofMillis(interval), this::updateTimeExpired)
        }else if(throwIfNoneSlot){
            throw SnowflakeException("There is no available slot for snowflake.")
        }
        return slotGot
    }

    private fun updateTimeExpired(timout: Timeout?): SecondIntervalTimeoutTimer.TaskResult {
        if (this.stopped) {
            return SecondIntervalTimeoutTimer.TaskResult.Break
        }
        val id = instanceId
        val expired = getTimeExpired()
        val slotValue = getSlotValue(slot ?: throw RuntimeException("slot value is null currently, jdbc slot update task fault."))

        transactionTemplate.configure(isolationLevel = Isolation.SERIALIZABLE).execute {
            val count = this.mapper.update {
                set(SnowflakeSlot.timeExpired).equalTo(expired)
                where(SnowflakeSlot.slotNumber, SqlBuilder.isEqualTo(slotValue))
                        .and(SnowflakeSlot.instance, SqlBuilder.isEqualTo(id))
            }

            if (count > 0) {
                renewCount.incrementAndGet()
                if (log.isDebugEnabled) {
                    log.debug("Jdbc snowflake slot '$slotValue' updated.")
                }
            } else {
                log.error("unable to renew jdbc snowflake slot '$slotValue' for instance '$instanceId'.")
            }
        }
        return SecondIntervalTimeoutTimer.TaskResult.Continue
    }

    private fun acquire(): Int? {
        var latestSlot: Short = 1

        while (latestSlot <= maxSlotCount) {
            val (record, isNew) = getOrCreateSlot(latestSlot)
            if (isNew) {
                return latestSlot.toInt()
            }
            if (record.instance != this.instanceId && record.timeExpired.orDefault() > System.currentTimeMillis()) {
                latestSlot++
                continue
            }

            val count = transactionTemplate.configure(isolationLevel = Isolation.SERIALIZABLE).execute {
                updateMapper.tryUpdate(getSlotValue(latestSlot), instanceId, ipAddr, getTimeExpired(), System.currentTimeMillis())
            } ?: 0

            if (count == 1) {
                return latestSlot.toInt()
            }

            latestSlot++
        }
        return null
    }

    /**
     * @return record, isNew
     */
    private fun getOrCreateSlot(latestId: Short): Pair<SnowflakeSlotRecord, Boolean> {
        val slotValue = getSlotValue(latestId)

        var isNew = false
        var r = transactionTemplate.configure(isReadOnly = true, isolationLevel = Isolation.SERIALIZABLE).execute {
            mapper.selectOne {
                where(SnowflakeSlot.slotNumber, SqlBuilder.isEqualTo(slotValue))
            }
        }

        if (r == null) {
            r = try {
                val record = SnowflakeSlotRecord(
                        slotValue,
                        this.instanceId,
                        ipAddr,
                        getTimeExpired()
                )
                transactionTemplate.configure(isReadOnly = false, isolationLevel = Isolation.SERIALIZABLE).execute {
                    this.mapper.insert(record)
                }
                isNew = true
                record
            } catch (e: DuplicateKeyException) {
                log.debug(e.toString())
                val existed = transactionTemplate.configure(isReadOnly = true, isolationLevel = Isolation.SERIALIZABLE).execute {
                    mapper.selectOne {
                        where(SnowflakeSlot.slotNumber, SqlBuilder.isEqualTo(slotValue))
                    }
                }
                existed
            }
        }

        return Pair(r!!, isNew)
    }

    override fun close() {
        this.stopped = true
        this.task?.cancel()
        val id = this.instanceId
        transactionTemplate.configure(isolationLevel = Isolation.SERIALIZABLE).execute {
            val count = this.mapper.delete {
                where(SnowflakeSlot.instance, SqlBuilder.isEqualTo(id))
            }
            log.info("$count jdbc snowflake slot has been released.")
        }
    }
}