ALTER TABLE environment ADD require_deployment_message tinyint(1) DEFAULT '0';
ALTER TABLE deployment ADD deployment_message TEXT NULL;