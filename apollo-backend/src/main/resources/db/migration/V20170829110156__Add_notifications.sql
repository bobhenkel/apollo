CREATE TABLE `notification` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(1000) NOT NULL,
  `service_id` int(11) unsigned NULL,
  `environment_id` int(11) unsigned NULL,
  `type` varchar(100) NOT NULL,
  `notification_json_configuration` text NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `notification_service_fk` FOREIGN KEY (`service_id`) REFERENCES `service` (`id`),
  CONSTRAINT `notification_environment_fk` FOREIGN KEY (`environment_id`) REFERENCES `environment` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;