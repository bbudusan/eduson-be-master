CREATE TABLE `webinar_tags`
(
    `tag_id`    bigint(20) NOT NULL,
    `webinar_id` bigint(20) NOT NULL,
    PRIMARY KEY (`tag_id`, `webinar_id`),
    CONSTRAINT `fk_w_tag_id` FOREIGN KEY `tags` (`tag_id`) REFERENCES `tags` (`id`),
    CONSTRAINT `fk_webinar_tag_id` FOREIGN KEY `webinars` (`webinar_id`) REFERENCES `webinars` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `webinar_modules`
(
    `module_id` bigint(20) NOT NULL,
    `webinar_id` bigint(20) NOT NULL,
    PRIMARY KEY (`module_id`, `webinar_id`),
    CONSTRAINT `fk_w_module_id` FOREIGN KEY `modules` (`module_id`) REFERENCES `modules` (`id`),
    CONSTRAINT `fk_webinar_module_id` FOREIGN KEY `webinars` (`webinar_id`) REFERENCES `webinars` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
