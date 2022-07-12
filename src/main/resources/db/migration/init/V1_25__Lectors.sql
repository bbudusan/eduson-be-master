CREATE TABLE `lectors`
(
    `id`               bigint(20) NOT NULL AUTO_INCREMENT,
    `title_id`         bigint(20) NOT NULL,
    `description`      VARCHAR(3000) NOT NULL,
    `has_access`       BIT           NOT NULL,
    `profile_image_id` bigint(20) NOT NULL,
    `user_id`          bigint(20) NOT NULL,
    PRIMARY KEY (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8;

CREATE TABLE `lector_titles`
(
    `id`    bigint(20) NOT NULL AUTO_INCREMENT,
    `title` VARCHAR(100) NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_title_lectors` (`title`)
)ENGINE=InnoDB  DEFAULT CHARSET=utf8;