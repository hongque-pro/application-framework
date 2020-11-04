DROP TABLE IF EXISTS `open_partners`;
CREATE TABLE `open_partners` (
  `id` bigint(20) NOT NULL,
  `name` varchar(64) NOT NULL,
  `time_expired` bigint(20) NOT NULL,
  `status` tinyint NOT NULL DEFAULT 1,
  `time_latest_paid` bigint(20) NOT NULL DEFAULT 0,
  `time_latest_updated` bigint(20) NOT NULL DEFAULT 0,
  `phone_number` varchar(16) NOT NULL DEFAULT '',
  `contact` varchar(8) NOT NULL DEFAULT '' COMMENT '联系人',
  `email` varchar(64) NOT NULL DEFAULT '',
  `app_count` smallint NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`),
  KEY(`status`),
  UNIQUE KEY(`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

DROP TABLE IF EXISTS `open_partner_user`;
CREATE TABLE `open_partner_user` (
  `partner_id` bigint(20) NOT NULL,
  `user_id` bigint(64) NOT NULL,
  PRIMARY KEY (`partner_id`, `user_id`),
  KEY(`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

DROP TABLE IF EXISTS `open_apps`;
CREATE TABLE `open_apps` (
  `app_id` bigint(20) NOT NULL,
  `display_name` varchar(32) NOT NULL,
  `app_secret` char(32) NOT NULL,
  `sign_algorithm` varchar(8) NOT NULL,
  `app_type` tinyint NOT NULL DEFAULT 0,
  `js_api_key` char(32) NOT NULL DEFAULT '',
  `js_api_domain` varchar(128) NOT NULL DEFAULT '',
  `logo_url`  varchar(512) NOT NULL DEFAULT '',
  `status` tinyint NOT NULL DEFAULT 1,
  `partner_id` bigint(20) NOT NULL,
  `configuration` json NOT NULL,
  `time_created` bigint(20) NOT NULL,
  `time_config_updated` bigint(20) NOT NULL,
  `concurrency_stamp` varchar(32) NOT NULL,
  PRIMARY KEY (`app_id`),
  KEY(`js_api_key`),
  KEY(`status`),
  KEY(`partner_id`),
  UNIQUE KEY (`display_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;