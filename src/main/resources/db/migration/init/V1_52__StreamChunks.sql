CREATE TABLE `chunks`
(
  `id` bigint(20) NOT NULL,
  `webinar_id` bigint(20) NULL,
  `course_id` bigint(20) NULL,
  `advert_id` bigint(20) NULL,
  `number` bigint(20),
  `quality`  VARCHAR(100),
  `type` VARCHAR(10), -- live or vod
  `duration` TIME,
  `active` BIT,
  PRIMARY KEY (`id`),
  CONSTRAINT `fk_course_chunks_id` FOREIGN KEY `courses` (`course_id`) REFERENCES `courses` (`id`),
  CONSTRAINT `fk_webinar_chunks_id` FOREIGN KEY `webinars` (`webinar_id`) REFERENCES `webinars` (`id`),
  CONSTRAINT `fk_advert_chunks_id` FOREIGN KEY `ads` (`advert_id`) REFERENCES `ads` (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8;

CREATE TABLE `chunk_usages`
(
  `id` bigint(20) NOT NULL,
  `chunk_id` bigint(20) NOT NULL,
  `user_id` bigint(20) NOT NULL,
  `point` datetime,
  `action_id` bigint(20), -- action_id might be a reference to a future actions table. currently, 7: 'downloaded', 8: 'displayed'
  PRIMARY KEY (`id`),
  CONSTRAINT `fk_chunk_usages_user_id` FOREIGN KEY `users` (`user_id`) REFERENCES `users` (`id`),
  CONSTRAINT `fk_chunk_usages_chunk_id` FOREIGN KEY `chunks` (`chunk_id`) REFERENCES `chunks` (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8;
