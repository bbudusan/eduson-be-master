CREATE TABLE `user_emc` (
  `user_id` bigint(20) NOT NULL,
  `emc` BIT NOT NULL,
  `grade` VARCHAR(200),
  `cuim` VARCHAR(100),
  `specialty` VARCHAR(200),
  `job` VARCHAR(200),
  PRIMARY KEY (`user_id`),
  CONSTRAINT `fk_user_emc_user_id` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
