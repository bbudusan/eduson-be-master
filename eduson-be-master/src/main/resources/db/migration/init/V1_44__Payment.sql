CREATE TABLE `invitations` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `email` varchar(40) NOT NULL,
  `status` varchar(12) NOT NULL,
  `invited_by` bigint(20) NOT NULL,
  `user_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_invitations` (`invited_by`, `user_id`),
  CONSTRAINT `fk_invitations_invited_by` FOREIGN KEY (`invited_by`) REFERENCES `users` (`id`),
  CONSTRAINT `fk_invitations_user_id` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
ALTER TABLE `permissions` ADD COLUMN `cart` BIT;
ALTER TABLE `permissions` CHANGE COLUMN `order_date` `added_date` datetime NOT NULL;
ALTER TABLE `permissions` DROP FOREIGN KEY `fk_permissions_transfer_file_id`;
ALTER TABLE `permissions` DROP COLUMN `transfer_file_id`;
ALTER TABLE `permissions` DROP COLUMN `payment_type`;
ALTER TABLE `permissions` ADD COLUMN `transaction_id` bigint(20);
ALTER TABLE `permissions` ADD CONSTRAINT `fk_permissions_transaction_id` FOREIGN KEY (`transaction_id`) REFERENCES `transactions` (`id`);
ALTER TABLE `permissions` ADD COLUMN `value` FLOAT; -- the amount paid

ALTER TABLE `transactions` DROP FOREIGN KEY `fk_transactions_permission_id`;
ALTER TABLE `transactions` DROP COLUMN `permission_id`;
ALTER TABLE `transactions` MODIFY COLUMN `transaction_id` varchar(40);
ALTER TABLE `transactions` ADD COLUMN `payment_intent` varchar(40);
ALTER TABLE `transactions` ADD COLUMN `payment_type` varchar(40) NOT NULL; -- STRIPE, TRANSFER, NOT_NEEDED
ALTER TABLE `transactions` ADD COLUMN `paid_at` datetime;
ALTER TABLE `transactions` ADD COLUMN `transfer_file_id` bigint(20);
ALTER TABLE `transactions` ADD CONSTRAINT `fk_transactions_transfer_file_id` FOREIGN KEY `files` (`transfer_file_id`) REFERENCES `files` (`id`);
ALTER TABLE `transactions` ADD COLUMN `user_id` bigint(20) NOT NULL; -- the (sponsor) user who pays
ALTER TABLE `transactions` ADD COLUMN `invoice_id` varchar(20); -- e.g. "12345", without "EDU "

ALTER TABLE `courses` ADD COLUMN `published` datetime;
