CREATE TABLE `tags`
(
    `id`   bigint(20) NOT NULL AUTO_INCREMENT,
    `name` varchar(255) NOT NULL,
    `tag_category_id` bigint(20) NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_tags_name` (`name`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8;


CREATE TABLE `tag_categories`
(
    `id`   bigint(20) NOT NULL AUTO_INCREMENT,
    `name` varchar(255) NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_tag_categories_name` (`name`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8;

-- CREATE TABLE `tag_categories_tags`
-- (
--     `tag_id`           bigint(20) NOT NULL,
--     `tag_categoriy_id` bigint(20) NOT NULL,
--     PRIMARY KEY (`tag_id`, `tag_categoriy_id`),
--     CONSTRAINT `fk_tag_id` FOREIGN KEY `tags` (`tag_id`) REFERENCES `tags` (`id`),
--     CONSTRAINT `fk_tag_category_id` FOREIGN KEY `tag_categories` (`tag_categoriy_id`) REFERENCES `tag_categories` (`id`)
-- ) ENGINE=InnoDB DEFAULT CHARSET=utf8;
--
