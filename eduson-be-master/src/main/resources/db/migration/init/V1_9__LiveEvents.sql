CREATE TABLE `live_events`
(
    `id`                    bigint(20) NOT NULL AUTO_INCREMENT,
    `name`                  VARCHAR(100)  NOT NULL,
    `price`                 FLOAT         NOT NULL,
    `description`           VARCHAR(3000) NOT NULL,
    `image_url`             VARCHAR(100)  NOT NULL,
    `description_image_url` VARCHAR(100)  NOT NULL,
    `credits`               INT           NOT NULL,
    `start_time`            datetime      NOT NULL,
    `end_time`              datetime      NOT NULL,
    `admin_id`              bigint(20) NOT NULL,
    `added_date`            datetime NULL,
    PRIMARY KEY (`id`),
    KEY                     `fk_user_live_event` (`admin_id`),
    CONSTRAINT `fk_user_live_event` FOREIGN KEY (`admin_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8;

CREATE TABLE `live_events_lectors`
(
    `live_event_id` bigint(20) NOT NULL,
    `lector_id`     bigint(20) NOT NULL,
    PRIMARY KEY (`live_event_id`, `lector_id`),
    CONSTRAINT `fk_events_lec_id` FOREIGN KEY `live_events` (`live_event_id`) REFERENCES `live_events` (`id`),
    CONSTRAINT `fk_events_lector_id` FOREIGN KEY `users` (`lector_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `live_events_coordinators`
(
    `live_event_id`  bigint(20) NOT NULL,
    `coordinator_id` bigint(20) NOT NULL,
    PRIMARY KEY (`live_event_id`, `coordinator_id`),
    CONSTRAINT `fk_events_coord_id` FOREIGN KEY `live_events` (`live_event_id`) REFERENCES `live_events` (`id`),
    CONSTRAINT `fk_events_coordinator_id` FOREIGN KEY `users` (`coordinator_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

