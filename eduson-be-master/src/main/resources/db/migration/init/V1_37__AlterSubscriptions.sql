CREATE TABLE `periods` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(100) NOT NULL,
  `description` VARCHAR(3000),
  -- some recurrence info for stripe:
  `interva_l` VARCHAR(10), -- month, year, week, day - or null for fixed.
  `interval_count` bigint(20),
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_periods_interval_int_count` (`interva_l`, `interval_count`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

ALTER TABLE `subscriptions` DROP COLUMN `monthly`;
ALTER TABLE `subscriptions` DROP COLUMN `annually`;
ALTER TABLE `subscriptions` DROP COLUMN `fix`;

CREATE TABLE `subscription_periods` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `product_type` varchar(20) NOT NULL, -- SUBSCRIPTION, MODULE, WEBINAR, COURSE, LIVE_EVENT (currently only SUBSCRIPTION)
  `product_id` bigint(20) NOT NULL, -- should reference the table according to product_type (currently `subscriptions`)
  `period_id` bigint(20) NOT NULL, -- refers to `periods`
  `price` FLOAT NOT NULL, -- price
  `stripe` varchar(40), -- price id in stripe
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_subscription_periods_subid_ptype_pid` (`period_id`, `product_type`, `product_id`), -- once a period for every product
  CONSTRAINT `fk_subscription_periods_period_id` FOREIGN KEY `periods` (`period_id`) REFERENCES `periods` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

ALTER TABLE `permissions` ADD COLUMN `stripe` varchar(40); -- the subscription id as it is saved in stripe
-- ALTER TABLE `permissions` COLUMN `payment_type`; -- STRIPE, NOT_NEEDED
-- if this is SUBSCRIPTION:
ALTER TABLE `permissions` MODIFY COLUMN `product_type` varchar(20); -- SUBSCRIPTION, MODULE, WEBINAR, COURSE, LIVE_EVENT
ALTER TABLE `permissions` MODIFY COLUMN `product_id` bigint(20); -- should reference the table according to product_type
-- then this must not be NULL:
ALTER TABLE `permissions` ADD COLUMN `period_id` bigint(20); -- refers to `periods`
ALTER TABLE `permissions` ADD CONSTRAINT `fk_permissions_periods_period_id` FOREIGN KEY `periods` (`period_id`) REFERENCES `periods` (`id`);

ALTER TABLE `modules` ADD COLUMN `price` FLOAT; -- can be null if we do no want to sell

ALTER TABLE `users` ADD COLUMN `stripe` varchar(40); -- the consumer id as it is saved in stripe

ALTER TABLE `courses` ADD COLUMN `stripe` varchar(40); -- the product id as it is saved in stripe
ALTER TABLE `webinars` ADD COLUMN `stripe` varchar(40); -- the product id as it is saved in stripe
ALTER TABLE `live_events` ADD COLUMN `stripe` varchar(40); -- the product id as it is saved in stripe
ALTER TABLE `modules` ADD COLUMN `stripe` varchar(40); -- the product id as it is saved in stripe
ALTER TABLE `subscriptions` ADD COLUMN `stripe` varchar(40); -- the product id as it is saved in stripe
