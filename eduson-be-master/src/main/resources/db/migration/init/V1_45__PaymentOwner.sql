ALTER TABLE `permissions` ADD COLUMN `owner_id` bigint(20);
UPDATE `permissions` SET `owner_id` = `user_id`;
ALTER TABLE `permissions` ADD CONSTRAINT `fk_permissions_owner` FOREIGN KEY `users` (`owner_id`) REFERENCES `users` (`id`);