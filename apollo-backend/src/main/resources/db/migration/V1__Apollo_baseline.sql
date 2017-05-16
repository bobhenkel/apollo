CREATE TABLE `users` (
  `user_email` varchar(100) NOT NULL,
  `first_name` varchar(100) NOT NULL,
  `last_name` varchar(100) NOT NULL,
  `hashed_password` varchar(1000) NOT NULL,
  `is_admin` tinyint(1) DEFAULT '1',
  PRIMARY KEY (`user_email`),
  UNIQUE KEY `user_email` (`user_email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `environment` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(1000) NOT NULL,
  `geo_region` varchar(100) NOT NULL,
  `availability` varchar(100) NOT NULL,
  `kubernetes_master` varchar(1000) NOT NULL,
  `kubernetes_token` varchar(1000) NOT NULL,
  `kubernetes_namespace` varchar(1000) NOT NULL,
  `service_port_coefficient` int(11) unsigned NOT NULL DEFAULT '0',
   PRIMARY KEY (`id`),
   UNIQUE KEY `name` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

CREATE TABLE `service` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(1000) NOT NULL,
  `deployment_yaml` text NOT NULL,
  `service_yaml` text NULL,
   PRIMARY KEY (`id`),
   UNIQUE KEY `name` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

CREATE TABLE `deployable_version` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `git_commit_sha` varchar(1000) NOT NULL,
  `github_repository_url` varchar(1000) NOT NULL,
  `service_id` int(11) unsigned NOT NULL,
  `commit_url` varchar(1000) NULL,
  `commit_message` varchar(1000) NULL,
  `commit_date` TIMESTAMP NULL,
  `committer_avatar_url` varchar(1000) NULL,
  `committer_name` varchar(1000) NULL,
   PRIMARY KEY (`id`),
   UNIQUE KEY `deployable_version_pair` (`service_id`, `git_commit_sha`),
   CONSTRAINT `deployable_version_service_fk` FOREIGN KEY (`service_id`) REFERENCES `service` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

CREATE TABLE `deployment` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `environment_id` int(11) unsigned NOT NULL,
  `service_id` int(11) unsigned NOT NULL,
  `deployable_version_id` int(11) unsigned NOT NULL,
  `user_email` varchar(1000) NOT NULL,
  `status` varchar(1000) NOT NULL,
  `source_version` varchar(1000) NULL,
  `started_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `last_update` TIMESTAMP ON UPDATE CURRENT_TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
   PRIMARY KEY (`id`),
   CONSTRAINT `deployment_environment_fk` FOREIGN KEY (`environment_id`) REFERENCES `environment` (`id`),
   CONSTRAINT `deployment_service_fk` FOREIGN KEY (`service_id`) REFERENCES `service` (`id`),
   CONSTRAINT `deployment_deployable_version_fk` FOREIGN KEY (`deployable_version_id`) REFERENCES `deployable_version` (`id`),
   CONSTRAINT `deployment_user_fk` FOREIGN KEY (`user_email`) REFERENCES `users` (`user_email`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

CREATE TABLE `deployment_permissions` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(1000) NOT NULL,
  `service_id` int(11) unsigned NULL,
  `environment_id` int(11) unsigned NULL,
  `permission_type` varchar(100) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `deployment_permission_pairs` (`service_id`, `environment_id`, `permission_type`),
  CONSTRAINT `deployment_permission_service_fk` FOREIGN KEY (`service_id`) REFERENCES `service` (`id`),
  CONSTRAINT `deployment_permission_environment_fk` FOREIGN KEY (`environment_id`) REFERENCES `environment` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

CREATE TABLE `deployment_groups` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(1000) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

CREATE TABLE `deployment_user_groups` (
  `user_email` varchar(1000) NOT NULL,
  `deployment_group_id` int(11) unsigned NOT NULL,
  UNIQUE KEY (`user_email`, `deployment_group_id`),
  CONSTRAINT `deployment_user_groups_user_email_fk` FOREIGN KEY (`user_email`) REFERENCES `users` (`user_email`),
  CONSTRAINT `deployment_user_groups_deployment_groups_id_fk` FOREIGN KEY (`deployment_group_id`) REFERENCES `deployment_groups` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `deployment_group_permissions` (
  `deployment_group_id` int(11) unsigned NOT NULL,
  `deployment_permission_id` int(11) unsigned NOT NULL,
  UNIQUE KEY (`deployment_group_id`, `deployment_permission_id`),
  CONSTRAINT `deployment_group_permissions_deployment_groups_id_fk` FOREIGN KEY (`deployment_group_id`) REFERENCES `deployment_groups` (`id`),
  CONSTRAINT `deployment_group_permissions_deployment_permissions_id_fk` FOREIGN KEY (`deployment_permission_id`) REFERENCES `deployment_permissions` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
