ALTER TABLE `courses` MODIFY COLUMN `description` longtext;
ALTER TABLE `webinars` MODIFY COLUMN `description` longtext;
ALTER TABLE `live_events` MODIFY COLUMN `description` longtext;
ALTER TABLE `lectors` MODIFY COLUMN `description` longtext;
ALTER TABLE `wchatmessages` ADD COLUMN `hidden` BIT;
UPDATE `wchatmessages` SET `hidden` = 0;
