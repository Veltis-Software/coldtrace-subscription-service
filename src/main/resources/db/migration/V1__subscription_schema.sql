/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `subscription_plans` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  `updated_at` datetime(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
  `active` bit(1) NOT NULL,
  `allows_ai_guidance` bit(1) NOT NULL,
  `allows_ai_report_summary` bit(1) NOT NULL,
  `allows_exports` bit(1) NOT NULL,
  `allows_maintenance` bit(1) NOT NULL,
  `code` varchar(40) NOT NULL,
  `currency` varchar(3) NOT NULL,
  `description` varchar(1000) NOT NULL,
  `history_retention_days` int DEFAULT NULL,
  `max_assets` int DEFAULT NULL,
  `max_iot_devices` int DEFAULT NULL,
  `max_locations` int DEFAULT NULL,
  `max_users` int DEFAULT NULL,
  `monthly_price_cents` int NOT NULL,
  `name` varchar(255) NOT NULL,
  `recommended` bit(1) NOT NULL,
  `recommended_label` varchar(255) DEFAULT NULL,
  `stripe_price_id` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK95fpyxycxc00w3yabipw8jrq7` (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `subscription_plan_included_features` (
  `subscription_plan_id` bigint NOT NULL,
  `feature` varchar(255) NOT NULL,
  KEY `FKptn1n1buq0rysoik0gfdbjgja` (`subscription_plan_id`),
  CONSTRAINT `FKptn1n1buq0rysoik0gfdbjgja` FOREIGN KEY (`subscription_plan_id`) REFERENCES `subscription_plans` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `organization_subscriptions` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  `updated_at` datetime(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
  `cancel_at_period_end` bit(1) NOT NULL,
  `current_period_end` datetime(6) DEFAULT NULL,
  `current_period_start` datetime(6) DEFAULT NULL,
  `metadata` varchar(2000) DEFAULT NULL,
  `organization_id` bigint NOT NULL,
  `plan_code` varchar(40) NOT NULL,
  `provider` enum('NONE','STRIPE') NOT NULL,
  `provider_customer_id` varchar(255) DEFAULT NULL,
  `provider_subscription_id` varchar(255) DEFAULT NULL,
  `status` enum('ACTIVE','CANCELED','FREE','PAST_DUE') NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKh4lcews5jri4aw7upla04b17y` (`organization_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `billing_webhook_events` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  `updated_at` datetime(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
  `event_id` varchar(120) NOT NULL,
  `event_type` varchar(120) NOT NULL,
  `metadata` varchar(2000) DEFAULT NULL,
  `organization_id` bigint DEFAULT NULL,
  `processed_at` datetime(6) NOT NULL,
  `provider` enum('NONE','STRIPE') NOT NULL,
  `provider_customer_id` varchar(255) DEFAULT NULL,
  `provider_subscription_id` varchar(255) DEFAULT NULL,
  `status` enum('IGNORED','PROCESSED') NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_billing_webhook_events_provider_event` (`provider`,`event_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
