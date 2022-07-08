ALTER TABLE `wchatmessages` ADD COLUMN `session` VARCHAR(20);
ALTER TABLE `wchatmessages` MODIFY COLUMN `sender_id` bigint(20);
ALTER TABLE `wchatmessages` MODIFY COLUMN `product_id` bigint(20);
