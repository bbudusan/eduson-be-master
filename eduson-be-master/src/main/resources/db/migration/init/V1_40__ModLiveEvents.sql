ALTER TABLE live_events DROP FOREIGN KEY `fk_events_descr_image_file`;
ALTER TABLE live_events DROP COLUMN file_descr_image_id;
ALTER TABLE live_events ADD COLUMN `session` varchar(1000);
CREATE TABLE `live_event_tags`
(
    `tag_id`    bigint(20) NOT NULL,
    `live_event_id` bigint(20) NOT NULL,
    PRIMARY KEY (`tag_id`, `live_event_id`),
    CONSTRAINT `fk_le_tag_id` FOREIGN KEY `tags` (`tag_id`) REFERENCES `tags` (`id`),
    CONSTRAINT `fk_live_event_tag_id` FOREIGN KEY `live_events` (`live_event_id`) REFERENCES `live_events` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `live_event_modules`
(
    `module_id` bigint(20) NOT NULL,
    `live_event_id` bigint(20) NOT NULL,
    PRIMARY KEY (`module_id`, `live_event_id`),
    CONSTRAINT `fk_le_module_id` FOREIGN KEY `modules` (`module_id`) REFERENCES `modules` (`id`),
    CONSTRAINT `fk_live_event_module_id` FOREIGN KEY `live_events` (`live_event_id`) REFERENCES `live_events` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
ALTER TABLE webinars
    MODIFY COLUMN name varchar(500) NOT NULL;
ALTER TABLE courses
    MODIFY COLUMN name varchar(500) NOT NULL;
ALTER TABLE live_events
    MODIFY COLUMN name varchar(500) NOT NULL;
CREATE TABLE `live_event_favorites`
(
    `live_event_id` bigint(20) NOT NULL,
    `user_id`  bigint(20) NOT NULL,
    PRIMARY KEY (`live_event_id`, `user_id`),
    CONSTRAINT `fk_live_event_f_id` FOREIGN KEY `live_events` (`live_event_id`) REFERENCES `live_events` (`id`),
    CONSTRAINT `fk_live_event_f_user_id` FOREIGN KEY `users` (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
ALTER TABLE `wchatmessages` DROP FOREIGN KEY `fk_wchatmessages_webinars_webinars_id`;
ALTER TABLE `wchatmessages` CHANGE COLUMN `webinar_id` `product_id` bigint(20) NOT NULL;
ALTER TABLE `wchatmessages` ADD COLUMN `dest` varchar(2);
UPDATE `wchatmessages` SET `dest` = 'w';
ALTER TABLE `wchatmessages` MODIFY COLUMN `dest` varchar(2) NOT NULL;
