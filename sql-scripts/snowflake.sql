DROP TABLE IF EXISTS `core_snowflake_slots`;
CREATE TABLE `core_snowflake_slots` (
  `slot_number` varchar(32) NOT NULL,
  `instance` varchar(32) NOT NULL,
  `addr` varchar(32) NOT NULL,
  `time_expired` BIGINT(20) NOT NULL,
  PRIMARY KEY (`slot_number`),
  INDEX(`instance`),
  INDEX(`time_expired`)
) COMMENT='snowflake id 生成算法插槽';