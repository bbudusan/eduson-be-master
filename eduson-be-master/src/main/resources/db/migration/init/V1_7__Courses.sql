CREATE TABLE `courses`
(
    `id`          bigint(20) NOT NULL AUTO_INCREMENT,
    `name`        VARCHAR(100)  NOT NULL,
    `price`       FLOAT         NOT NULL,
    `course_url`  VARCHAR(100)  NOT NULL,
    `description` VARCHAR(3000) NOT NULL,
    `added_date`  datetime NULL,
    `admin_id`    bigint(20) NOT NULL,
    PRIMARY KEY (`id`),
    KEY           `fk_user_course` (`admin_id`),
    CONSTRAINT `fk_user_course` FOREIGN KEY (`admin_id`) REFERENCES `users` (`id`),
    `lector_id`   bigint(20) NOT NULL,
    KEY           `fk_lector_course` (`lector_id`),
    CONSTRAINT `fk_lector_course` FOREIGN KEY (`lector_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8;

CREATE TABLE `course_tags`
(
    `tag_id`    bigint(20) NOT NULL,
    `course_id` bigint(20) NOT NULL,
    PRIMARY KEY (`tag_id`, `course_id`),
    CONSTRAINT `fk_tag_id` FOREIGN KEY `tags` (`tag_id`) REFERENCES `tags` (`id`),
    CONSTRAINT `fk_course_tag_id` FOREIGN KEY `courses` (`course_id`) REFERENCES `courses` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `course_modules`
(
    `module_id` bigint(20) NOT NULL,
    `course_id` bigint(20) NOT NULL,
    PRIMARY KEY (`module_id`, `course_id`),
    CONSTRAINT `fk_module_id` FOREIGN KEY `modules` (`module_id`) REFERENCES `modules` (`id`),
    CONSTRAINT `fk_course_module_id` FOREIGN KEY `courses` (`course_id`) REFERENCES `courses` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


