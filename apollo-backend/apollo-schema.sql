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

DROP TABLE IF EXISTS `environment`;
CREATE TABLE `environment` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(1000) NOT NULL,
  `geo_region` varchar(100) NOT NULL,
  `availability` varchar(100) NOT NULL,
  `kubernetes_master` varchar(1000) NOT NULL,
  `kubernetes_token` varchar(1000) NOT NULL,
   PRIMARY KEY (`id`),
   UNIQUE KEY `name` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `service`;
CREATE TABLE `service` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(1000) NOT NULL,

   PRIMARY KEY (`id`),
   UNIQUE KEY `name` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `deployable_version`;
CREATE TABLE `deployable_version` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `git_commit_sha` varchar(1000) NOT NULL,
  `github_repository_url` varchar(1000) NOT NULL,
  `service_id` int(11) unsigned NOT NULL,
   PRIMARY KEY (`id`),
   UNIQUE KEY `deployable_version_pair` (`service_id`, `git_commit_sha`),
   CONSTRAINT `deployable_version_service_fk` FOREIGN KEY (`service_id`) REFERENCES `service` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `deployment`;
CREATE TABLE `deployment` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `environment_id` int(11) unsigned NOT NULL,
  `service_id` int(11) unsigned NOT NULL,
  `deployable_version_id` int(11) unsigned NOT NULL,
  `user_email` varchar(1000) NOT NULL,
  `status` varchar(1000) NOT NULL,
  `source_version` varchar(1000) NOT NULL,
  `started_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `last_update` TIMESTAMP ON UPDATE CURRENT_TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
   PRIMARY KEY (`id`),
   CONSTRAINT `deployment_environment_fk` FOREIGN KEY (`environment_id`) REFERENCES `environment` (`id`),
   CONSTRAINT `deployment_service_fk` FOREIGN KEY (`service_id`) REFERENCES `service` (`id`),
   CONSTRAINT `deployment_deployable_version_fk` FOREIGN KEY (`deployable_version_id`) REFERENCES `service` (`id`),
   CONSTRAINT `deployment_user_fk` FOREIGN KEY (`user_email`) REFERENCES `users` (`user_email`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `permissions`;
CREATE TABLE `permissions` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(1000) NOT NULL,
  `service_id` int(11) unsigned NULL,
  `environment_id` int(11) unsigned NULL,
  `permission_type` varchar(100) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `permission_pairs` (`service_id`, `environment_id`, `permission_type`),
  CONSTRAINT `permission_service_fk` FOREIGN KEY (`service_id`) REFERENCES `service` (`id`),
  CONSTRAINT `permission_environment_fk` FOREIGN KEY (`environment_id`) REFERENCES `environment` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `groups`;
CREATE TABLE `groups` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(1000) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `user_groups`;
CREATE TABLE `user_groups` (
  `user_email` varchar(1000) NOT NULL,
  `group_id` int(11) unsigned NOT NULL,
  UNIQUE KEY (`user_email`, `group_id`),
  CONSTRAINT `user_groups_user_email_fk` FOREIGN KEY (`user_email`) REFERENCES `users` (`user_email`),
  CONSTRAINT `user_groups_group_id_fk` FOREIGN KEY (`group_id`) REFERENCES `groups` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `group_permissions`;
CREATE TABLE `group_permissions` (
  `group_id` int(11) unsigned NOT NULL,
  `permission_id` int(11) unsigned NOT NULL,
  UNIQUE KEY (`group_id`, `permission_id`),
  CONSTRAINT `group_permissions_group_id_fk` FOREIGN KEY (`group_id`) REFERENCES `groups` (`id`),
  CONSTRAINT `group_permissions_permission_id_fk` FOREIGN KEY (`permission_id`) REFERENCES `permissions` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;