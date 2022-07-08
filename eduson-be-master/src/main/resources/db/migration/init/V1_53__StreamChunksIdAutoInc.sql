ALTER TABLE `chunk_usages` DROP FOREIGN KEY `fk_chunk_usages_chunk_id`;
ALTER TABLE `chunks` MODIFY COLUMN `id`  bigint(20) NOT NULL  AUTO_INCREMENT;
ALTER TABLE `chunks` MODIFY COLUMN `duration` TIME(3);
ALTER TABLE `chunk_usages` ADD CONSTRAINT `fk_chunk_usages_chunk_id` FOREIGN KEY `chunks` (`chunk_id`) REFERENCES `chunks` (`id`);
ALTER TABLE `chunk_usages` MODIFY COLUMN `id`  bigint(20) NOT NULL  AUTO_INCREMENT;
