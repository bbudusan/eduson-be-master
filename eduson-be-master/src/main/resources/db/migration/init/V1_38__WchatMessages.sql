CREATE TABLE `wchatmessages` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `webinar_id` bigint(20) NOT NULL,
  `sender_id` bigint(20) NOT NULL,
  `timestamp` datetime NOT NULL,
  `message` VARCHAR(1000) NOT NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `fk_wchatmessages_webinars_webinars_id` FOREIGN KEY `webinars` (`webinar_id`) REFERENCES `webinars` (`id`),
  CONSTRAINT `fk_wchatmessages_webinars_sender_id` FOREIGN KEY `users` (`sender_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
