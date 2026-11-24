CREATE TABLE `analyses` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `amount` decimal(38,2) DEFAULT NULL,
  `credit_limit` decimal(38,2) DEFAULT NULL,
  `farmer` varchar(255) DEFAULT NULL,
  `income` decimal(38,2) DEFAULT NULL,
  `score` int DEFAULT NULL,
  `term_months` int DEFAULT NULL,
  `decision` enum('APPROVED','REJECTED') DEFAULT NULL,
  `reason` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;