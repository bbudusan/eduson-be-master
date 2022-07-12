CREATE TABLE `course_favorites`
(
    `course_id` bigint(20) NOT NULL,
    `user_id`  bigint(20) NOT NULL,
    PRIMARY KEY (`course_id`, `user_id`),
    CONSTRAINT `fk_course_f_id` FOREIGN KEY `courses` (`course_id`) REFERENCES `courses` (`id`),
    CONSTRAINT `fk_course_f_user_id` FOREIGN KEY `users` (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
CREATE TABLE `webinar_favorites`
(
    `webinar_id` bigint(20) NOT NULL,
    `user_id`  bigint(20) NOT NULL,
    PRIMARY KEY (`webinar_id`, `user_id`),
    CONSTRAINT `fk_webinar_f_id` FOREIGN KEY `webinars` (`webinar_id`) REFERENCES `webinars` (`id`),
    CONSTRAINT `fk_webinar_f_user_id` FOREIGN KEY `users` (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
