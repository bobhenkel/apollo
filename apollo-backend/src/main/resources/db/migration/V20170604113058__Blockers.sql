CREATE TABLE `blocker_definition` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(1000) NOT NULL,
  `service_id` int(11) unsigned NULL,
  `environment_id` int(11) unsigned NULL,
  `started_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `is_active` tinyint(1) DEFAULT '1',
  `blocker_type_name` varchar(1000) NOT NULL,
  `blocker_json_configuration` text NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `blocker_service_fk` FOREIGN KEY (`service_id`) REFERENCES `service` (`id`),
  CONSTRAINT `blocker_environment_fk` FOREIGN KEY (`environment_id`) REFERENCES `environment` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;