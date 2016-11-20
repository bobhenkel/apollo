DROP TABLE IF EXISTS `users`;
CREATE TABLE `users` (
  `email_address` varchar(100) NOT NULL,
  `hashed_password` varchar(1000) NOT NULL,
   PRIMARY KEY(`email_address`),
    UNIQUE KEY `email_address` (`email_address`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;