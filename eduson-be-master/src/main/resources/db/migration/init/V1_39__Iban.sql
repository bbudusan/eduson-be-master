ALTER TABLE `user_legal` MODIFY COLUMN `iban` VARCHAR (40);
ALTER TABLE `permissions` ADD COLUMN `order_date` datetime;
UPDATE `permissions` SET `order_date` = '2021-12-09 14:46:17';
ALTER TABLE `permissions` MODIFY COLUMN `order_date` datetime NOT NULL;
ALTER TABLE `permissions` MODIFY COLUMN `start_time` datetime;
