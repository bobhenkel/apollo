CREATE TABLE `users_blockers_override` (
  `user_email` varchar(100) NOT NULL,
  `blocker_id` int(11) unsigned NOT NULL,
  UNIQUE KEY (`user_email`, `blocker_id`),
  CONSTRAINT `users_blockers_override_user_email_fk` FOREIGN KEY (`user_email`) REFERENCES `users` (`user_email`),
  CONSTRAINT `users_blockers_override_blocker_id_fk` FOREIGN KEY (`blocker_id`) REFERENCES `blocker_definition` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;