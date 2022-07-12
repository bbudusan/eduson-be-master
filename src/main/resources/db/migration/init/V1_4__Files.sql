CREATE TABLE `files` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT,
    `name` varchar(100) NOT NULL,
    `content_type` varchar(50) NOT NULL,
    `user_id` bigint(20) NOT NULL,
     PRIMARY KEY (`id`),
     KEY `fk_user_file` (`user_id`),
     CONSTRAINT `fk_user_file` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
 ) ENGINE=InnoDB  DEFAULT CHARSET=utf8;

