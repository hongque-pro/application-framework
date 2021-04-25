

DROP TABLE IF EXISTS `identity_role_claims`;
CREATE TABLE `identity_role_claims` (
  `id` bigint(20) NOT NULL,
  `claim_type` varchar(16) NOT NULL,
  `claim_value` varchar(256) NOT NULL,
  `role_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  INDEX(`role_id`)
);

DROP TABLE IF EXISTS `identity_roles`;
CREATE TABLE `identity_roles` (
  `id` bigint(20) NOT NULL,
  `concurrency_stamp` varchar(8) NOT NULL,
  `name` varchar(32) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `role_name_index` (`name`)
);


DROP TABLE IF EXISTS `identity_user_claims`;
CREATE TABLE `identity_user_claims` (
  `id` bigint(20) NOT NULL,
  `claim_type` varchar(16) NOT NULL,
  `claim_value` varchar(256) NOT NULL,
  `user_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  INDEX(`user_id`)
);


DROP TABLE IF EXISTS `identity_user_logins`;
CREATE TABLE `identity_user_logins` (
  `login_provider` varchar(16) NOT NULL,
  `provider_key` varchar(128) NOT NULL,
  `provider_display_name` varchar(32),
  `user_id` bigint(20) NOT NULL,
  PRIMARY KEY (`login_provider`,`provider_key`),
  INDEX(`user_id`)
);


DROP TABLE IF EXISTS `identity_openids`;
CREATE TABLE `identity_openids` (
  `login_provider` varchar(16) NOT NULL,
  `app_id` varchar(32) NOT NULL,
  `open_id` varchar(128) NOT NULL,
  `user_id` bigint(20) NOT NULL,
  PRIMARY KEY (`app_id`,`user_id`, `login_provider`),
  INDEX(`user_id`),
  INDEX(`app_id`),
  INDEX(`login_provider`)
);


DROP TABLE IF EXISTS `identity_user_roles`;
CREATE TABLE `identity_user_roles` (
  `user_id` bigint(20) NOT NULL,
  `role_id` bigint(20) NOT NULL,
  PRIMARY KEY (`user_id`,`role_id`),
  INDEX (`user_id`)
);


DROP TABLE IF EXISTS `identity_user_tokens`;
CREATE TABLE `identity_user_tokens` (
  `user_id` bigint(20) NOT NULL,
  `login_provider` varchar(16) NOT NULL,
  `name` varchar(16) NOT NULL,
  `token` varchar(64) NOT NULL,
  PRIMARY KEY (`user_id`,`login_provider`,`token`)
);

DROP TABLE IF EXISTS `identity_users`;
CREATE TABLE `identity_users` (
  `id` bigint(20) NOT NULL,
  `user_type` tinyint NOT NULL DEFAULT 0,
  `access_failed_count` int(11) NOT NULL DEFAULT 0,
  `concurrency_stamp` varchar(32) NOT NULL,
  `email` varchar(64) NOT NULL DEFAULT '',
  `email_confirmed` bit(1) NOT NULL DEFAULT 0,
  `language` varchar(16) NOT NULL DEFAULT 'zh-CN',
  `lockout_enabled` bit(1) NOT NULL DEFAULT 1,
  `lockout_end` bigint(20) NOT NULL DEFAULT 0,
  `password_hash` varchar(128) NOT NULL,
  `phone_number` varchar(16) NOT NULL DEFAULT '',
  `phone_number_confirmed` bit(1) NOT NULL DEFAULT 0,
  `security_stamp` varchar(32) NOT NULL,
  `time_zone` varchar(32) NOT NULL,
  `two_factor_enabled` bit(1) NOT NULL,
  `user_name` varchar(16) NOT NULL,
  `approved` bit NOT NULL default 1,
  `approver_id` bigint(20) NOT NULL DEFAULT -1,
  `time_expired` bigint(20) NOT NULL DEFAULT 9223372036854775807,
  `time_last_login` bigint(20) NOT NULL DEFAULT 0,
  `time_last_activity` bigint(20) NOT NULL DEFAULT 0,
  `time_created` bigint(20) NOT NULL DEFAULT 0,
  `last_sign_in_ip` varchar(16) NOT NULL DEFAULT '',
  `last_sign_in_platform` varchar(16) NOT NULL DEFAULT '',
  `last_sign_in_area` varchar(16) NOT NULL DEFAULT '',
  `last_client_version` varchar(32) NOT NULL DEFAULT '',
  `member_id` bigint(20) NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`),
  INDEX(`user_type`),
  UNIQUE KEY `user_name_index` (`user_name`),
  UNIQUE KEY `email_index` (`email`),
  UNIQUE KEY `phone_number_index` (`phone_number`)
);

DROP TABLE IF EXISTS `identity_oauth_client_details`;
CREATE TABLE `identity_oauth_client_details` (
  `client_id` varchar(256) NOT NULL,
  `resource_ids` varchar(256) NOT NULL,
  `client_secret` varchar(256) NOT NULL,
  `scope` varchar(256) NOT NULL,
  `authorized_grant_types` varchar(256) NOT NULL,
  `web_server_redirect_uri` varchar(256) NOT NULL DEFAULT '',
  `authorities` varchar(256) NOT NULL DEFAULT '',
  `access_token_validity` int(11) NOT NULL,
  `refresh_token_validity` int(11) NOT NULL,
  `additional_information` varchar(4096) NOT NULL DEFAULT '',
  `autoapprove` varchar(256) NOT NULL DEFAULT '',
  `enabled` bit(1) NOT NULL DEFAULT 1,
  PRIMARY KEY (`client_id`),
  INDEX `identity_oauth_client_details_enabled_index` (`enabled`)
);