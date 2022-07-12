CREATE TABLE `modules`
(
    `id`   bigint(20) NOT NULL AUTO_INCREMENT,
    `name` varchar(255) NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_module_name` (`name`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8;

