ALTER TABLE `confirmation_tokens` ADD COLUMN `type` varchar(3);
UPDATE `confirmation_tokens` set `type`='CO';
ALTER TABLE `confirmation_tokens` ADD COLUMN `used` BIT;
