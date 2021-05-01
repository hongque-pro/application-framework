package com.labijie.application.data

import org.apache.ibatis.annotations.Mapper
import org.apache.ibatis.annotations.Param
import org.apache.ibatis.annotations.Update

/**
 *
 * @Auther: AndersXiao
 * @Date: 2021-05-01 11:49
 * @Description:
 */

@Mapper
interface SnowflakeCustomMapper {

    @Update("update core_snowflake_slots set " +
            "instance=#{instanceId}, addr=#{addr}, time_expired=#{timeExpired} " +
            "where slot_number=#{slotNumber} and (instance=#{instanceId} or time_expired <= #{nowTime})")
    fun tryUpdate(
            @Param("slotNumber") slotNumber: String,
            @Param("instanceId") instanceId: String,
            @Param("addr") addr: String,
            @Param("timeExpired") timeExpired: Long,
            @Param("nowTime") nowTime: Long): Int
}