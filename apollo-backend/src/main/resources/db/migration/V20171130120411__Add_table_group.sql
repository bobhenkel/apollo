CREATE TABLE `groups` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(1000) NOT NULL,
  `service_id` int(11) unsigned NULL,
  `environment_id` int(11) unsigned NULL,
  `scaling_factor` int(11) DEFAULT 1,
  `json_params` text DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `name` (`name`),
  CONSTRAINT `group_environment_fk` FOREIGN KEY (`environment_id`) REFERENCES `environment` (`id`),
  CONSTRAINT `group_service_fk` FOREIGN KEY (`service_id`) REFERENCES `service` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;
