ALTER TABLE `webinars` MODIFY COLUMN `credits` INT;
ALTER TABLE `live_events` MODIFY COLUMN `credits` INT;

CREATE TABLE `ads`
(
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `duration` TIME,
  `file_id` bigint(20) NULL,
  `title` VARCHAR(500) NULL,
  `description` longtext NULL,
  `onclick` VARCHAR(1000) NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `fk_ad_file` FOREIGN KEY (file_id) REFERENCES files(id)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8;
CREATE TABLE `course_ads`
(
  `advert_id` bigint(20) NOT NULL,
  `course_id` bigint(20) NOT NULL,
  `start` TIME,
  `priority` INT NULL,
  `rule_id` bigint(20), -- rule_id might be a reference to a future ad_rules table. currently, 1: 'inactive', 2: 'active'
  PRIMARY KEY (`advert_id`, `course_id`),
  CONSTRAINT `fk_ad_id` FOREIGN KEY `ads` (`advert_id`) REFERENCES `ads` (`id`),
  CONSTRAINT `fk_course_ad_id` FOREIGN KEY `courses` (`course_id`) REFERENCES `courses` (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8;

CREATE TABLE `usage`
(
  `user_id` bigint(20) NOT NULL,
  `course_id` bigint(20) NOT NULL,
  `point` TIME,
  `action_id` bigint(20), -- action_id might be a reference to a future actions table. currently, 1: 'started', 2: 'playing', 3: 'stopped', 4: 'seeked from', 5: 'seeked to', 6: 'finished'
  PRIMARY KEY (`user_id`, `course_id`),
  CONSTRAINT `fk_user_id` FOREIGN KEY `users` (`user_id`) REFERENCES `users` (`id`),
  CONSTRAINT `fk_course_usage_id` FOREIGN KEY `courses` (`course_id`) REFERENCES `courses` (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8;

CREATE TABLE `mailing_list`
(
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `email` varchar(100) NOT NULL, -- it will store the bcypted email after the unsubscribe to keep the record of subscription without personal data
  `name` varchar(100) NULL,
  `time` TIME NULL,
  `subscribed` VARCHAR(500) NULL,
  `active` BIT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8;

CREATE TABLE `open_positions`
(
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `title` VARCHAR(500) NULL,
  `description` longtext NULL,
  `deadline` datetime NULL,
  `location` varchar(100),
  PRIMARY KEY (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8;