DROP TABLE IF EXISTS `order_payment_trades`;
CREATE TABLE `order_payment_trades` (
  `id` bigint(20) NOT NULL,
  `order_id` bigint(20) NOT NULL,
  `user_id` bigint(20) NOT NULL,
  `payment_provider` varchar(16) NOT NULL,
  `payment_type` tinyint NOT NULL,
  `platform_id` varchar(64) NOT NULL DEFAULT '',
  `mode` tinyint NOT NULL DEFAULT 0,
  `platform_merchant_key` varchar(56) NOT NULL,
  `amount` decimal (10, 2) NOT NULL,
  `platform_buyer_id` varchar(32) NOT NULL,
  `time_expired` bigint(20) NOT NULL,
  `time_created` bigint(20) NOT NULL,
  `time_effected` bigint(20) NOT NULL,
  `method` tinyint NOT NULL,
  `order_type` varchar(32) NOT NULL,
  `status` tinyint NOT NULL,
  `tag` json NOT NULL ,
  PRIMARY KEY (`id`),
  KEY(`status`),
  KEY(`order_id`),
  KEY(`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;