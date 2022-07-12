CREATE TABLE `webinars`
(
    `id`                    bigint(20) NOT NULL AUTO_INCREMENT,
    `name`                  VARCHAR(100)  NOT NULL,
    `price`                 FLOAT         NOT NULL,
    `credits`               INT           NOT NULL,
    `acronym`               VARCHAR(100)  NOT NULL,
    `description`           VARCHAR(3000) NOT NULL,
    `image_url`             VARCHAR(100)  NOT NULL,
    `description_image_url` VARCHAR(100)  NOT NULL,
    `start_time`            datetime      NOT NULL,
    `end_time`              datetime      NOT NULL,
    `added_date`            datetime NULL,
    `admin_id`              bigint(20) NOT NULL,
    PRIMARY KEY (`id`),
    KEY                     `fk_user_webminar` (`admin_id`),
    CONSTRAINT `fk_user_webminar` FOREIGN KEY (`admin_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8;

CREATE TABLE `webinar_courses`
(
    `webinar_id` bigint(20) NOT NULL,
    `course_id`  bigint(20) NOT NULL,
    PRIMARY KEY (`webinar_id`, `course_id`),
    CONSTRAINT `fk_webinar_id` FOREIGN KEY `webinars` (`webinar_id`) REFERENCES `webinars` (`id`),
    CONSTRAINT `fk_webinar_course_id` FOREIGN KEY `courses` (`course_id`) REFERENCES `courses` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `webinar_coordinators`
(
    `webinar_id`     bigint(20) NOT NULL,
    `coordinator_id` bigint(20) NOT NULL,
    PRIMARY KEY (`webinar_id`, `coordinator_id`),
    CONSTRAINT `fk_webinar_coord_id` FOREIGN KEY `webinars` (`webinar_id`) REFERENCES `webinars` (`id`),
    CONSTRAINT `fk_webinar_coordinator_id` FOREIGN KEY `users` (`coordinator_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
