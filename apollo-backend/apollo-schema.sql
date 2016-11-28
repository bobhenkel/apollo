DROP TABLE IF EXISTS `users`;
CREATE TABLE `users` (
  `user_email` varchar(100) NOT NULL,
  `first_name` varchar(100) NOT NULL,
  `last_name` varchar(100) NOT NULL,
  `hashed_password` varchar(1000) NOT NULL,
  `is_admin` tinyint(1) DEFAULT '1',
  PRIMARY KEY (`user_email`),
  UNIQUE KEY `user_email` (`user_email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;