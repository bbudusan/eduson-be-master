CREATE TABLE `user_individual` (
  `user_id` bigint(20) NOT NULL,
  `cnp` VARCHAR (20) NOT NULL,
  `address` VARCHAR(500) NOT NULL,
  `country` VARCHAR(100) NOT NULL,
  `county` VARCHAR(100) NOT NULL,
  `city` VARCHAR(100) NOT NULL,
  `zip_code` VARCHAR(50) NOT NULL,
  `phone` VARCHAR(20),
  PRIMARY KEY (`user_id`),
  CONSTRAINT `fk_user_individual_user_id` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
CREATE TABLE `user_legal` (
  `user_id` bigint(20) NOT NULL,
  `company` VARCHAR (200) NOT NULL,
  `cui` VARCHAR (20) NOT NULL,
  `reg_com` VARCHAR (20),
  `iban` VARCHAR (20) NOT NULL,
  `address` VARCHAR(500) NOT NULL,
  `country` VARCHAR(100) NOT NULL,
  `county` VARCHAR(100) NOT NULL,
  `city` VARCHAR(100) NOT NULL,
  `zip_code` VARCHAR(50) NOT NULL,
  `phone` VARCHAR(20),
  PRIMARY KEY (`user_id`),
  CONSTRAINT `fk_user_legal_user_id` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
