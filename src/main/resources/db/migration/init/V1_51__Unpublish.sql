ALTER TABLE `courses` CHANGE COLUMN `published` `published_date` datetime;
ALTER TABLE `courses` ADD COLUMN `published` BIT;
ALTER TABLE `webinars` ADD COLUMN `published` BIT;
ALTER TABLE `live_events` ADD COLUMN `published` BIT;
ALTER TABLE `modules` ADD COLUMN `published` BIT;
ALTER TABLE `subscriptions` ADD COLUMN `published` BIT;