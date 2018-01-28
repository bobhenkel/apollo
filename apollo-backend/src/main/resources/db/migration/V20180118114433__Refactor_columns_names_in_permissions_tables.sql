ALTER TABLE deployment_role_permissions DROP FOREIGN KEY `deployment_group_permissions_deployment_groups_id_fk`;
ALTER TABLE deployment_role_permissions CHANGE COLUMN `deployment_group_id` `deployment_role_id` INT(11) unsigned NOT NULL;
ALTER TABLE deployment_role_permissions ADD CONSTRAINT `deployment_role_permissions_deployment_roles_id_fk` FOREIGN KEY (`deployment_role_id`) REFERENCES `deployment_roles` (`id`);

ALTER TABLE user_deployment_roles DROP FOREIGN KEY `deployment_user_groups_deployment_groups_id_fk`;
ALTER TABLE user_deployment_roles CHANGE COLUMN `deployment_group_id` `deployment_role_id` INT(11) unsigned NOT NULL;
ALTER TABLE user_deployment_roles ADD CONSTRAINT `deployment_user_roles_deployment_roles_id_fk` FOREIGN KEY (`deployment_role_id`) REFERENCES `deployment_roles` (`id`);

ALTER TABLE deployment_role_permissions DROP FOREIGN KEY `deployment_group_permissions_deployment_permissions_id_fk`;
ALTER TABLE deployment_role_permissions  ADD CONSTRAINT `deployment_group_permissions_deployment_permissions_id_fk` FOREIGN KEY (`deployment_permission_id`) REFERENCES `deployment_permissions` (`id`);

ALTER TABLE user_deployment_roles DROP FOREIGN KEY `deployment_user_groups_user_email_fk`;
ALTER TABLE user_deployment_roles  ADD CONSTRAINT `user_deployment_roles_user_email_fk` FOREIGN KEY (`user_email`) REFERENCES `users` (`user_email`);