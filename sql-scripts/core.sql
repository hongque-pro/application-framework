DROP TABLE IF EXISTS `core_temp_file`;
CREATE TABLE `core_temp_file` (
  `id` BIGINT(20) NOT NULL,
  `path` varchar(256) NOT NULL,
  `time_created` BIGINT(20) NOT NULL DEFAULT 0,
  `file_type` varchar(32) NOT NULL,
  PRIMARY KEY(`id`),
  UNIQUE INDEX (`path`)) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

DROP TABLE IF EXISTS `core_file`;
CREATE TABLE `core_file` (
`id` BIGINT(20) NOT NULL,
`path` varchar(256) NOT NULL,
`time_created` BIGINT(20) NOT NULL DEFAULT 0,
`file_type` varchar(32) NOT NULL,
`entity_id`  BIGINT(20) NOT NULL DEFAULT  0,
PRIMARY KEY(`id`),
KEY(`file_type`),
KEY(`entity_id`),
UNIQUE INDEX (`path`)) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

DROP TABLE IF EXISTS `core_region_province`;
CREATE TABLE `core_region_province` (
  `id` varchar(8) COLLATE utf8mb4_unicode_ci NOT NULL,
  `name` varchar(16) COLLATE utf8mb4_unicode_ci NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `core_trade_data_name_uindex` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='民政部数据-省';


DROP TABLE IF EXISTS `core_region_city`;
CREATE TABLE `core_region_city` (
  `id` varchar(32) COLLATE utf8mb4_unicode_ci NOT NULL,
  `name` varchar(32) COLLATE utf8mb4_unicode_ci NOT NULL,
  `province_id` varchar(8) COLLATE utf8mb4_unicode_ci NOT NULL,
  PRIMARY KEY (`id`),
  KEY `core_region_city_name_index` (`name`),
  KEY `core_region_city_province_id_index` (`province_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='民政部数据-市';


DROP TABLE IF EXISTS `core_region_area`;
CREATE TABLE `core_region_area` (
  `id` varchar(32) COLLATE utf8mb4_unicode_ci NOT NULL,
  `name` varchar(32) COLLATE utf8mb4_unicode_ci NOT NULL,
  `city_id` varchar(32) COLLATE utf8mb4_unicode_ci NOT NULL,
  PRIMARY KEY (`id`),
  KEY `core_region_area_name_index` (`name`),
  KEY `core_region_area_city_id_index` (`city_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='民政部数据-区';
