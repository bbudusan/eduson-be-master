CREATE TABLE `subscriptions` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(40) NOT NULL,
  `type` varchar(20) NOT NULL, -- RETAIL, CORPORATE, PRIVATE
  `monthly` FLOAT, -- monthly price
  `annually` FLOAT, -- annual price
  `fix` FLOAT, -- price
  `description` VARCHAR(3000) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_subscriptions_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
CREATE TABLE `subscription_products` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `subscription_id` bigint(20) NOT NULL, -- refers to `subscriptions`
  `product_type` varchar(20) NOT NULL, -- SUBSCRIPTION, MODULE, WEBINAR, COURSE, LIVE_EVENT
  `product_id` bigint(20) NOT NULL, -- should reference the table according to product_type
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_subscription_products_subid_ptype_pid` (`subscription_id`, `product_type`, `product_id`),
  CONSTRAINT `fk_subscription_products_subscription_id` FOREIGN KEY `subscriptions` (`subscription_id`) REFERENCES `subscriptions` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
CREATE TABLE `permissions` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) NOT NULL, -- refers to `users`
  `product_type` varchar(20) NOT NULL, -- SUBSCRIPTION, MODULE, WEBINAR, COURSE, LIVE_EVENT
  `product_id` bigint(20) NOT NULL, -- should reference the table according to product_type
  `active` BIT, -- if unset, it checks the start_time. if it is false, it does not check anything. if it is true, it checks the expires and ends_at.
  `start_time` datetime NOT NULL, -- the start of the permission. if a transaction is needed for setting active from NULL, it checks the transaction also.
  `expires` datetime, -- expiry of the manual permission or the active transaction.
  `ends_at` datetime, -- if set, this subscription ends at this point, and maybe some future transactions shall be cancelled. also, it has to be checked when some transactions are scheduled.
  `payment_type` varchar(20) NOT NULL, -- MONTHLY, ANNUALLY, ONCE, NOT_NEEDED
  PRIMARY KEY (`id`),
  CONSTRAINT `fk_permissions_user_id` FOREIGN KEY `users` (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
CREATE TABLE `transactions` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `permission_id` bigint(20) NOT NULL, -- refers to `permissions`
  `transaction_id` varchar(40) NOT NULL, -- the transaction id as it is saved in stripe
  `timestamp` datetime NOT NULL, -- the transaction date and time
  `value` FLOAT, -- the amount paid
  `data` VARCHAR(1000), -- maybe some discount etc.
  PRIMARY KEY (`id`),
  CONSTRAINT `fk_transactions_permission_id` FOREIGN KEY `permissions` (`permission_id`) REFERENCES `permissions` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
