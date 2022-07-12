CREATE TABLE `general_files`
(
    `id` bigint(20) NOT NULL AUTO_INCREMENT,
    `gkey` varchar(500) NOT NULL,
    `version` bigint(20) NOT NULL,
    `file_id` bigint(20),
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_general_files_gkey` (`gkey`, `version`),
    CONSTRAINT `fk_general_files_file_id` FOREIGN KEY `files` (`file_id`) REFERENCES `files` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
ALTER TABLE `users` ADD COLUMN `invoice_address_personal` BIT;
ALTER TABLE `transactions` ADD COLUMN `invoice_file_id` bigint(20);
ALTER TABLE `transactions` ADD CONSTRAINT `fk_transactions_invoice_file_id` FOREIGN KEY `files` (`invoice_file_id`) REFERENCES `files` (`id`);
ALTER TABLE `permissions` ADD COLUMN `transfer_file_id` bigint(20);
ALTER TABLE `permissions` ADD CONSTRAINT `fk_permissions_transfer_file_id` FOREIGN KEY `files` (`transfer_file_id`) REFERENCES `files` (`id`);